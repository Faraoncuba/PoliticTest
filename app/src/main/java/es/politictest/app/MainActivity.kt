package es.politictest.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import es.politictest.app.feature.onboarding.OnboardingScreen
import es.politictest.app.feature.quiz.QuizScreen
import es.politictest.app.feature.quiz.QuizUiState
import es.politictest.app.feature.quiz.QuizViewModel
import es.politictest.app.feature.result.ResultScreen
import es.politictest.app.ui.theme.PoliticTestTheme

class MainActivity : ComponentActivity() {

    private val viewModel: QuizViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PoliticTestTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val uiState by viewModel.uiState.collectAsState()

                    when (val state = uiState) {
                        is QuizUiState.Loading -> {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator()
                            }
                        }
                        is QuizUiState.Onboarding -> {
                            OnboardingScreen(
                                onStartQuiz = { viewModel.startQuiz() }
                            )
                        }
                        is QuizUiState.InProgress -> {
                            QuizScreen(
                                question = state.currentQuestion,
                                currentIndex = state.currentIndex,
                                totalQuestions = state.totalQuestions,
                                selectedAnswer = state.selectedAnswer,
                                onAnswerSelected = { viewModel.selectAnswer(it) },
                                onBack = { viewModel.previousQuestion() }
                            )
                        }
                        is QuizUiState.Completed -> {
                            ResultScreen(
                                nolanResult = state.nolanResult,
                                affinities = state.affinities,
                                humorProfile = state.humorProfile,
                                onRestart = { viewModel.startQuiz() }
                            )
                        }
                        is QuizUiState.Error -> {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text(
                                    text = state.message,
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
