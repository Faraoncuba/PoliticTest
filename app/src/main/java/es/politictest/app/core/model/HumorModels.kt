package es.politictest.app.core.model

import kotlinx.serialization.Serializable

@Serializable
data class Archetype(
    val id: String,
    val region: NolanRegion,
    val title: String,
    val punchline: String,
    val description: String
)

@Serializable
data class TensionCondition(
    val questionId: String,
    val operator: String, // "GTE", "LTE", "EQ"
    val targetScore: Int
) {
    fun matches(actualScore: Int?): Boolean {
        if (actualScore == null) return false
        return when (operator) {
            "GTE" -> actualScore >= targetScore
            "LTE" -> actualScore <= targetScore
            "EQ" -> actualScore == targetScore
            else -> false
        }
    }
}

@Serializable
data class TensionRule(
    val id: String,
    val title: String,
    val commentary: String,
    val conditions: List<TensionCondition>
)
