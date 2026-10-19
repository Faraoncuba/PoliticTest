package es.politictest.app.core.model

import kotlinx.serialization.Serializable

@Serializable
data class MethodologyInfo(
    val engine: String,
    val economicQuestionsCount: Int,
    val personalQuestionsCount: Int,
    val scoreRangeMin: Int,
    val scoreRangeMax: Int
)

@Serializable
data class QuizDataset(
    val datasetVersion: Long,
    val minAppVersionCode: Int,
    val methodology: MethodologyInfo,
    val questions: List<Question>,
    val parties: List<Party>,
    val archetypes: List<Archetype>,
    val tensionRules: List<TensionRule>
)
