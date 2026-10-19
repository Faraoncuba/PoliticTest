package es.politictest.app.core.model

import kotlinx.serialization.Serializable

@Serializable
data class Party(
    val id: String,
    val name: String,
    val acronym: String,
    val primaryColorHex: String,
    val nolanCoordinates: NolanCoordinates,
    val answers: Map<String, Int>, // questionId -> 0, 3, 5, 7, 10 (null si no está)
    val sources: Map<String, String> = emptyMap(), // questionId -> cita/fuente documental
    val description: String = ""
)
