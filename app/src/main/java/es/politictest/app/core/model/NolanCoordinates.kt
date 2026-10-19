package es.politictest.app.core.model

import kotlinx.serialization.Serializable
import kotlin.math.abs

@Serializable
enum class NolanRegion(
    val title: String,
    val subtitle: String
) {
    LIBERTARIAN(
        title = "Libertario",
        subtitle = "Prioridad a las libertades individuales civiles y al libre mercado económico."
    ),
    PROGRESSIVE(
        title = "Progresista",
        subtitle = "Prioridad a los derechos y libertades civiles, con intervención pública en la economía."
    ),
    CONSERVATIVE(
        title = "Conservador",
        subtitle = "Prioridad al libre mercado y la propiedad, con preservación de normas sociales tradicionales."
    ),
    AUTHORITARIAN(
        title = "Autoritario / Estatista",
        subtitle = "Preferencia por un Estado fuerte que tutele tanto la economía como el orden moral y civil."
    ),
    CENTRIST(
        title = "Centrista",
        subtitle = "Posición moderada y pragmática que busca el equilibrio entre libertades y regulación."
    )
}

@Serializable
data class NolanCoordinates(
    val economic: Int, // 0..100
    val personal: Int  // 0..100
) {
    init {
        require(economic in 0..100) { "Coordenada económica debe estar entre 0 y 100, recibido: $economic" }
        require(personal in 0..100) { "Coordenada personal debe estar entre 0 y 100, recibido: $personal" }
    }

    /**
     * Clasificación geométrica del diagrama de Nolan:
     * - Rombo central: |economic - 50| + |personal - 50| <= 25
     * - Fuera del rombo se determinan los 4 cuadrantes.
     */
    fun determineRegion(): NolanRegion {
        val manhattanDistanceToCenter = abs(economic - 50) + abs(personal - 50)
        if (manhattanDistanceToCenter <= 25) {
            return NolanRegion.CENTRIST
        }

        return when {
            economic >= 50 && personal >= 50 -> NolanRegion.LIBERTARIAN
            economic < 50 && personal >= 50 -> NolanRegion.PROGRESSIVE
            economic >= 50 && personal < 50 -> NolanRegion.CONSERVATIVE
            else -> NolanRegion.AUTHORITARIAN
        }
    }
}
