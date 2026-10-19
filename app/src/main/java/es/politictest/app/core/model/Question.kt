package es.politictest.app.core.model

import kotlinx.serialization.Serializable

@Serializable
data class Question(
    val id: String,
    val index: Int, // 1 a 20
    val axis: Axis,
    val text: String,
    val favorableToFreedom: Boolean,
    val topic: String,
    val detail: String? = null
)
