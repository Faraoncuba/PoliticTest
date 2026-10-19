package es.politictest.app.core.humor

import es.politictest.app.core.model.Archetype
import es.politictest.app.core.model.NolanCoordinates
import es.politictest.app.core.model.NolanRegion
import es.politictest.app.core.model.TensionRule

data class HumorProfile(
    val archetype: Archetype,
    val detectedTensions: List<TensionRule>
)

object HumorEngine {

    /**
     * Resuelve el perfil humorístico a posteriori, garantizando que nunca
     * altera las coordenadas ni las puntuaciones ideológicas.
     */
    fun resolve(
        coordinates: NolanCoordinates,
        region: NolanRegion,
        userScores: Map<String, Int>,
        archetypes: List<Archetype>,
        tensionRules: List<TensionRule>
    ): HumorProfile {
        // Seleccionar arquetipo que coincida con la región
        val candidateArchetype = archetypes.firstOrNull { it.region == region }
            ?: Archetype(
                id = "default_${region.name.lowercase()}",
                region = region,
                title = region.title,
                punchline = region.subtitle,
                description = "Perfil clasificado dentro del cuadrante de ${region.title}."
            )

        // Evaluar reglas de contradicción/tensión
        val triggeredTensions = tensionRules.filter { rule ->
            rule.conditions.isNotEmpty() && rule.conditions.all { condition ->
                condition.matches(userScores[condition.questionId])
            }
        }

        return HumorProfile(
            archetype = candidateArchetype,
            detectedTensions = triggeredTensions
        )
    }
}
