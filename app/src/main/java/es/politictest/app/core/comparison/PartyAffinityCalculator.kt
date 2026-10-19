package es.politictest.app.core.comparison

import es.politictest.app.core.model.Party
import es.politictest.app.core.model.Question
import kotlin.math.abs
import kotlin.math.roundToInt

data class TopicAlignment(
    val questionId: String,
    val topic: String,
    val questionText: String,
    val userScore: Int,
    val partyScore: Int,
    val difference: Int,
    val sourceOrQuote: String?
)

data class PartyAffinity(
    val party: Party,
    val percentage: Int?, // null si no hay suficientes evidencias (menos del 60%)
    val isComplete: Boolean,
    val comparedQuestionsCount: Int,
    val topAgreements: List<TopicAlignment>,
    val topDisagreements: List<TopicAlignment>,
    val statusMessage: String
)

object PartyAffinityCalculator {

    private const val MIN_COMPARABLE_QUESTIONS = 12 // 60% de las 20 preguntas

    /**
     * Calcula la afinidad temática porcentual con cada partido político.
     * Módulo estrictamente independiente del cálculo del diagrama de Nolan.
     */
    fun calculateAffinity(
        userScores: Map<String, Int>, // questionId -> userScore (0..10)
        questions: List<Question>,
        parties: List<Party>
    ): List<PartyAffinity> {
        val questionsById = questions.associateBy { it.id }

        return parties.map { party ->
            val comparableAlignments = mutableListOf<TopicAlignment>()

            for ((questionId, userScore) in userScores) {
                val partyScore = party.answers[questionId] ?: continue
                val question = questionsById[questionId] ?: continue
                val diff = abs(userScore - partyScore)

                comparableAlignments.add(
                    TopicAlignment(
                        questionId = questionId,
                        topic = question.topic,
                        questionText = question.text,
                        userScore = userScore,
                        partyScore = partyScore,
                        difference = diff,
                        sourceOrQuote = party.sources[questionId]
                    )
                )
            }

            val count = comparableAlignments.size
            if (count < MIN_COMPARABLE_QUESTIONS) {
                PartyAffinity(
                    party = party,
                    percentage = null,
                    isComplete = false,
                    comparedQuestionsCount = count,
                    topAgreements = emptyList(),
                    topDisagreements = emptyList(),
                    statusMessage = "Comparación incompleta: posicionamientos parlamentarios insuficientes o no documentados ($count/20 cuestiones)."
                )
            } else {
                // Discrepancia media normalizada (0..10)
                val averageDiscrepancy = comparableAlignments.sumOf { it.difference }.toDouble() / count
                // Porcentaje de coincidencia programática: 100 - (discrepancia / 10 * 100)
                val affinityPercentage = ((1.0 - (averageDiscrepancy / 10.0)) * 100.0)
                    .coerceIn(0.0, 100.0)
                    .roundToInt()

                // Ordenar para encontrar mayores acuerdos (menor diferencia) y mayores discrepancias (mayor diferencia)
                val sortedAlignments = comparableAlignments.sortedBy { it.difference }
                val topAgreements = sortedAlignments.take(2).filter { it.difference <= 3 }
                val topDisagreements = sortedAlignments.reversed().take(2).filter { it.difference >= 5 }

                PartyAffinity(
                    party = party,
                    percentage = affinityPercentage,
                    isComplete = true,
                    comparedQuestionsCount = count,
                    topAgreements = topAgreements,
                    topDisagreements = topDisagreements,
                    statusMessage = "$affinityPercentage% de coincidencia sobre $count cuestiones documentadas."
                )
            }
        }.sortedWith(
            compareByDescending<PartyAffinity> { it.isComplete }
                .thenByDescending { it.percentage ?: -1 }
        )
    }
}
