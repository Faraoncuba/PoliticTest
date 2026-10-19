package es.politictest.app.core.scoring

import es.politictest.app.core.model.AnswerOption
import es.politictest.app.core.model.Axis
import es.politictest.app.core.model.NolanCoordinates
import es.politictest.app.core.model.NolanRegion
import es.politictest.app.core.model.Question
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class NolanCalculatorTest {

    private lateinit var mockQuestions: List<Question>

    @Before
    fun setUp() {
        // Generar 10 preguntas económicas (5 normales, 5 invertidas) y 10 personales (5 normales, 5 invertidas)
        val questions = mutableListOf<Question>()
        for (i in 1..10) {
            questions.add(
                Question(
                    id = "econ_$i",
                    index = i,
                    axis = Axis.ECONOMIC,
                    text = "Pregunta económica $i",
                    favorableToFreedom = (i % 2 != 0),
                    topic = "Economía"
                )
            )
        }
        for (i in 1..10) {
            questions.add(
                Question(
                    id = "pers_$i",
                    index = i + 10,
                    axis = Axis.PERSONAL,
                    text = "Pregunta personal $i",
                    favorableToFreedom = (i % 2 != 0),
                    topic = "Libertad personal"
                )
            )
        }
        mockQuestions = questions
    }

    @Test
    fun testAllNeutralAnswersGiveCenterRhombus50_50() {
        val answers = mockQuestions.associate { it.id to AnswerOption.NEUTRAL }
        val result = NolanCalculator.calculate(mockQuestions, answers)

        assertEquals(50, result.economicScore)
        assertEquals(50, result.personalScore)
        assertEquals(50, result.coordinates.economic)
        assertEquals(50, result.coordinates.personal)
        assertEquals(NolanRegion.CENTRIST, result.region)
    }

    @Test
    fun testMaximalFreedomAnswersGive100_100Libertarian() {
        // En pro-libertad respondemos STRONGLY_AGREE (+10), en anti-libertad respondemos STRONGLY_DISAGREE (+10)
        val answers = mockQuestions.associate { q ->
            q.id to if (q.favorableToFreedom) AnswerOption.STRONGLY_AGREE else AnswerOption.STRONGLY_DISAGREE
        }
        val result = NolanCalculator.calculate(mockQuestions, answers)

        assertEquals(100, result.economicScore)
        assertEquals(100, result.personalScore)
        assertEquals(NolanRegion.LIBERTARIAN, result.region)
    }

    @Test
    fun testMinimalFreedomAnswersGive0_0Authoritarian() {
        // En pro-libertad respondemos STRONGLY_DISAGREE (0), en anti-libertad respondemos STRONGLY_AGREE (0)
        val answers = mockQuestions.associate { q ->
            q.id to if (q.favorableToFreedom) AnswerOption.STRONGLY_DISAGREE else AnswerOption.STRONGLY_AGREE
        }
        val result = NolanCalculator.calculate(mockQuestions, answers)

        assertEquals(0, result.economicScore)
        assertEquals(0, result.personalScore)
        assertEquals(NolanRegion.AUTHORITARIAN, result.region)
    }

    @Test
    fun testRhombusBoundaryGeometry() {
        // Centro estricto (50, 50) -> Distancia = 0 <= 25 -> CENTRIST
        assertEquals(NolanRegion.CENTRIST, NolanCoordinates(50, 50).determineRegion())

        // Vértices del rombo central (Distancia = 25 <= 25 -> CENTRIST)
        assertEquals(NolanRegion.CENTRIST, NolanCoordinates(75, 50).determineRegion())
        assertEquals(NolanRegion.CENTRIST, NolanCoordinates(25, 50).determineRegion())
        assertEquals(NolanRegion.CENTRIST, NolanCoordinates(50, 75).determineRegion())
        assertEquals(NolanRegion.CENTRIST, NolanCoordinates(50, 25).determineRegion())
        assertEquals(NolanRegion.CENTRIST, NolanCoordinates(60, 65).determineRegion()) // |10| + |15| = 25

        // Inmediatamente fuera del rombo central
        // (76, 50) -> Distancia = 26 > 25 -> X >= 50, Y >= 50 -> LIBERTARIAN
        assertEquals(NolanRegion.LIBERTARIAN, NolanCoordinates(76, 50).determineRegion())
        // (24, 50) -> Distancia = 26 > 25 -> X < 50, Y >= 50 -> PROGRESSIVE
        assertEquals(NolanRegion.PROGRESSIVE, NolanCoordinates(24, 50).determineRegion())
        // (70, 20) -> Distancia = 50 > 25 -> X >= 50, Y < 50 -> CONSERVATIVE
        assertEquals(NolanRegion.CONSERVATIVE, NolanCoordinates(70, 20).determineRegion())
        // (20, 20) -> Distancia = 60 > 25 -> X < 50, Y < 50 -> AUTHORITARIAN
        assertEquals(NolanRegion.AUTHORITARIAN, NolanCoordinates(20, 20).determineRegion())
    }
}
