package es.politictest.app.feature.quiz

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import es.politictest.app.core.model.AnswerOption
import es.politictest.app.core.model.Axis
import es.politictest.app.core.model.Question
import es.politictest.app.ui.theme.AccentIndigo
import es.politictest.app.ui.theme.AccentTeal
import es.politictest.app.ui.theme.Slate200
import es.politictest.app.ui.theme.Slate500
import es.politictest.app.ui.theme.Slate700
import es.politictest.app.ui.theme.Slate800

@Composable
fun QuizScreen(
    question: Question,
    currentIndex: Int,
    totalQuestions: Int,
    selectedAnswer: AnswerOption?,
    onAnswerSelected: (AnswerOption) -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onBack,
                        enabled = currentIndex > 0
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Anterior",
                            tint = if (currentIndex > 0) MaterialTheme.colorScheme.onSurface else Color.Transparent
                        )
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    Text(
                        text = "Pregunta ${currentIndex + 1} de $totalQuestions",
                        style = MaterialTheme.typography.labelLarge,
                        color = Slate500
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                LinearProgressIndicator(
                    progress = { (currentIndex + 1).toFloat() / totalQuestions.toFloat() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = AccentIndigo,
                    trackColor = Slate200
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(20.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            AnimatedContent(
                targetState = question,
                transitionSpec = {
                    (slideInHorizontally { width -> width } + fadeIn()).togetherWith(
                        slideOutHorizontally { width -> -width } + fadeOut()
                    )
                },
                label = "question_transition"
            ) { currentQuestion ->
                Column {
                    // Badge del eje temático
                    val axisColor = if (currentQuestion.axis == Axis.ECONOMIC) AccentIndigo else AccentTeal
                    val axisLabel = if (currentQuestion.axis == Axis.ECONOMIC) "LIBERTAD ECONÓMICA" else "LIBERTAD PERSONAL"

                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(axisColor.copy(alpha = 0.12f))
                            .padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .width(8.dp)
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(axisColor)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "$axisLabel • ${currentQuestion.topic.uppercase()}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = axisColor
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = currentQuestion.text,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        lineHeight = 30.sp
                    )
                }
            }

            // Opciones Likert
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                AnswerOption.entries.forEach { option ->
                    val isSelected = selectedAnswer == option
                    LikertOptionCard(
                        option = option,
                        isSelected = isSelected,
                        onClick = { onAnswerSelected(option) }
                    )
                }
            }
        }
    }
}

@Composable
private fun LikertOptionCard(
    option: AnswerOption,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val borderColor = if (isSelected) AccentIndigo else Slate200
    val backgroundColor = if (isSelected) AccentIndigo.copy(alpha = 0.08f) else MaterialTheme.colorScheme.surface

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        border = androidx.compose.foundation.BorderStroke(if (isSelected) 2.dp else 1.dp, borderColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = option.label,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) AccentIndigo else Slate800
            )

            // Indicador circular tipo radio button
            Box(
                modifier = Modifier
                    .width(20.dp)
                    .height(20.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (isSelected) AccentIndigo else Color.Transparent)
                    .then(
                        if (!isSelected) androidx.compose.foundation.border(
                            1.5.dp,
                            Slate500.copy(alpha = 0.5f),
                            RoundedCornerShape(10.dp)
                        ) else Modifier
                    )
            )
        }
    }
}
