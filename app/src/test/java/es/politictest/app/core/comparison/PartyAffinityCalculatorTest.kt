package es.politictest.app.core.comparison

import es.politictest.app.core.model.Axis
import es.politictest.app.core.model.NolanCoordinates
import es.politictest.app.core.model.Party
import es.politictest.app.core.model.Question
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class PartyAffinityCalculatorTest {

    private lateinit var mockQuestions: List<Question>

    @Before
    fun setUp() {
        mockQuestions = (1..20).map { i ->
            Question(
                id = "q_$i",
                index = i,
                axis = if (i <= 10) Axis.ECONOMIC else Axis.PERSONAL,
                text = "Pregunta $i",
                favorableToFreedom = true,
                topic = "Tema $i"
            )
        }
    }

    @Test
    fun testExactMatchGives100Percent() {
        val answers = (1..20).associate { "q_$it" to 10 }
        val party = Party(
            id = "test_party",
            name = "Partido Test",
            acronym = "PT",
            primaryColorHex = "#FFFFFF",
            nolanCoordinates = NolanCoordinates(100, 100),
            answers = answers
        )

        val affinities = PartyAffinityCalculator.calculateAffinity(
            userScores = answers,
            questions = mockQuestions,
            parties = listOf(party)
        )

        assertEquals(1, affinities.size)
        val affinity = affinities.first()
        assertTrue(affinity.isComplete)
        assertEquals(100, affinity.percentage)
    }

    @Test
    fun testIncompleteWhenFewerThan12QuestionsDocumented() {
        val userAnswers = (1..20).associate { "q_$it" to 5 }
        // Partido que solo tiene 5 preguntas documentadas (< 12)
        val incompleteAnswers = (1..5).associate { "q_$it" to 5 }
        val party = Party(
            id = "incomplete_party",
            name = "Partido Incompleto",
            acronym = "PI",
            primaryColorHex = "#CCCCCC",
            nolanCoordinates = NolanCoordinates(50, 50),
            answers = incompleteAnswers
        )

        val affinities = PartyAffinityCalculator.calculateAffinity(
            userScores = userAnswers,
            questions = mockQuestions,
            parties = listOf(party)
        )

        assertEquals(1, affinities.size)
        val affinity = affinities.first()
        assertFalse(affinity.isComplete)
        assertNull(affinity.percentage)
        assertTrue(affinity.statusMessage.contains("Comparación incompleta"))
    }
}
