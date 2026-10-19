package es.politictest.app.feature.result

import android.content.Context
import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import es.politictest.app.core.comparison.PartyAffinity
import es.politictest.app.core.humor.HumorProfile
import es.politictest.app.core.scoring.NolanCalculationResult
import es.politictest.app.ui.theme.AccentAmber
import es.politictest.app.ui.theme.AccentIndigo
import es.politictest.app.ui.theme.AccentTeal
import es.politictest.app.ui.theme.Slate100
import es.politictest.app.ui.theme.Slate200
import es.politictest.app.ui.theme.Slate500
import es.politictest.app.ui.theme.Slate700
import es.politictest.app.ui.theme.Slate800

@Composable
fun ResultScreen(
    nolanResult: NolanCalculationResult,
    affinities: List<PartyAffinity>,
    humorProfile: HumorProfile,
    onRestart: () -> Unit
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    Scaffold(
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onRestart,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Repetir")
                }

                Button(
                    onClick = {
                        shareResult(
                            context = context,
                            region = nolanResult.region.title,
                            economic = nolanResult.economicScore,
                            personal = nolanResult.personalScore,
                            archetype = humorProfile.archetype.title
                        )
                    },
                    modifier = Modifier.weight(1.5f),
                    colors = ButtonDefaults.buttonColors(containerColor = AccentIndigo)
                ) {
                    Icon(Icons.Default.Share, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Compartir")
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Cabecera con coordenadas y región
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = humorProfile.archetype.title,
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "«${humorProfile.archetype.punchline}»",
                    style = MaterialTheme.typography.bodyLarge,
                    color = AccentIndigo,
                    fontWeight = FontWeight.Medium
                )
            }

            // Lienzo interactivo del Diagrama de Nolan
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    NolanCanvas(coordinates = nolanResult.coordinates)

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        ScoreBadge(
                            label = "Libertad Económica",
                            value = "${nolanResult.economicScore}/100",
                            color = AccentIndigo
                        )
                        ScoreBadge(
                            label = "Libertad Personal",
                            value = "${nolanResult.personalScore}/100",
                            color = AccentTeal
                        )
                    }
                }
            }

            // Contradicciones y tensiones detectadas
            if (humorProfile.detectedTensions.isNotEmpty()) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Tensiones Ideológicas Detectadas",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )

                    humorProfile.detectedTensions.forEach { tension ->
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = AccentAmber.copy(alpha = 0.1f)),
                            border = androidx.compose.foundation.BorderStroke(1.dp, AccentAmber.copy(alpha = 0.3f))
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(
                                    text = "⚡ ${tension.title}",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Slate900
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = tension.commentary,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Slate700
                                )
                            }
                        }
                    }
                }
            }

            // Módulo de Afinidad Electoral con Partidos
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "Afinidad Programática Electoral",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Calculada sobre los posicionamientos verificados en las 20 cuestiones del test. No constituye una recomendación de voto.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Slate500
                )

                affinities.forEach { affinity ->
                    PartyAffinityCard(affinity = affinity)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun ScoreBadge(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, fontSize = 12.sp, color = Slate500)
        Text(text = value, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = color)
    }
}

@Composable
private fun PartyAffinityCard(affinity: PartyAffinity) {
    var expanded by remember { mutableStateOf(false) }

    val partyColor = try {
        Color(android.graphics.Color.parseColor(affinity.party.primaryColorHex))
    } catch (_: Exception) {
        AccentIndigo
    }

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, Slate200),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded }
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .width(12.dp)
                            .height(12.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(partyColor)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "${affinity.party.name} (${affinity.party.acronym})",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Text(
                    text = if (affinity.isComplete) "${affinity.percentage}%" else "Incompleto",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (affinity.isComplete) partyColor else Slate500
                )
            }

            if (affinity.isComplete && affinity.percentage != null) {
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = { affinity.percentage.toFloat() / 100f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = partyColor,
                    trackColor = Slate100
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = affinity.statusMessage,
                    style = MaterialTheme.typography.bodySmall,
                    color = Slate500
                )
                Icon(
                    imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = Slate500
                )
            }

            AnimatedVisibility(visible = expanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (affinity.topAgreements.isNotEmpty()) {
                        Text(
                            text = "✓ Mayores Coincidencias:",
                            style = MaterialTheme.typography.labelLarge,
                            color = AccentTeal
                        )
                        affinity.topAgreements.forEach {
                            Text(
                                text = "• ${it.topic}: Coincidencia en ${it.questionText}",
                                style = MaterialTheme.typography.bodySmall,
                                color = Slate700
                            )
                        }
                    }

                    if (affinity.topDisagreements.isNotEmpty()) {
                        Text(
                            text = "✗ Mayores Discrepancias:",
                            style = MaterialTheme.typography.labelLarge,
                            color = Color(0xFFDC2626)
                        )
                        affinity.topDisagreements.forEach {
                            Text(
                                text = "• ${it.topic}: Postura opuesta en ${it.questionText}",
                                style = MaterialTheme.typography.bodySmall,
                                color = Slate700
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun shareResult(
    context: Context,
    region: String,
    economic: Int,
    personal: Int,
    archetype: String
) {
    val sendIntent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(
            Intent.EXTRA_TEXT,
            "¡He completado el Test Político de Nolan para España 2026! 🧭\n\n" +
                "Perfil: $archetype ($region)\n" +
                "Libertad Económica: $economic%\n" +
                "Libertad Personal: $personal%\n\n" +
                "Descubre tu posición y afinidad electoral en PoliticTest."
        )
        type = "text/plain"
    }
    context.startActivity(Intent.createChooser(sendIntent, "Compartir resultado"))
}
