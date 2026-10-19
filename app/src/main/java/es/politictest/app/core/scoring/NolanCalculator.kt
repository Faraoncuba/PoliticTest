package es.politictest.app.core.scoring

import es.politictest.app.core.model.AnswerOption
import es.politictest.app.core.model.Axis
import es.politictest.app.core.model.NolanCoordinates
import es.politictest.app.core.model.NolanRegion
import es.politictest.app.core.model.Question

data class NolanCalculationResult(
    val coordinates: NolanCoordinates,
    val region: NolanRegion,
    val economicScore: Int,
    val personalScore: Int,
    val questionScores: Map<String, Int> // questionId -> score obtenido (0..10)
)

object NolanCalculator {

    /**
     * Calcula la posición exacta en el Diagrama de Nolan a partir de las respuestas del usuario.
     *
     * @param questions Lista completa de las 20 preguntas (10 de libertad económica, 10 de libertad personal).
     * @param answers Mapa de questionId a la AnswerOption seleccionada por el usuario.
     */
    fun calculate(
        questions: List<Question>,
        answers: Map<String, AnswerOption>
    ): NolanCalculationResult {
        var economicSum = 0
        var personalSum = 0
        val questionScores = mutableMapOf<String, Int>()

        for (question in questions) {
            val answer = answers[question.id] ?: AnswerOption.NEUTRAL
            val score = answer.score(question.favorableToFreedom)
            questionScores[question.id] = score

            when (question.axis) {
                Axis.ECONOMIC -> economicSum += score
                Axis.PERSONAL -> personalSum += score
            }
        }

        // Asegurar rango estricto [0, 100]
        val clampedEconomic = economicSum.coerceIn(0, 100)
        val clampedPersonal = personalSum.coerceIn(0, 100)
        val coordinates = NolanCoordinates(clampedEconomic, clampedPersonal)

        return NolanCalculationResult(
            coordinates = coordinates,
            region = coordinates.determineRegion(),
            economicScore = clampedEconomic,
            personalScore = clampedPersonal,
            questionScores = questionScores
        )
    }
}
