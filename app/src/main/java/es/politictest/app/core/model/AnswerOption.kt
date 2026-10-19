package es.politictest.app.core.model

import kotlinx.serialization.Serializable

@Serializable
enum class AnswerOption(val label: String, val proScore: Int, val antiScore: Int) {
    STRONGLY_AGREE("Totalmente de acuerdo", 10, 0),
    AGREE("Parcialmente de acuerdo", 7, 3),
    NEUTRAL("Neutral", 5, 5),
    DISAGREE("Parcialmente en desacuerdo", 3, 7),
    STRONGLY_DISAGREE("Totalmente en desacuerdo", 0, 10);

    /**
     * Devuelve los puntos calculados según si la pregunta está orientada
     * a favor de la libertad (favorableToFreedom = true) o en contra (favorableToFreedom = false).
     */
    fun score(favorableToFreedom: Boolean): Int {
        return if (favorableToFreedom) proScore else antiScore
    }
}
