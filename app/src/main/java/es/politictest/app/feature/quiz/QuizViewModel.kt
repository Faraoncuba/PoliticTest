package es.politictest.app.feature.quiz

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import es.politictest.app.core.comparison.PartyAffinity
import es.politictest.app.core.comparison.PartyAffinityCalculator
import es.politictest.app.core.data.LocalAssetQuizRepository
import es.politictest.app.core.data.QuizRepository
import es.politictest.app.core.humor.HumorEngine
import es.politictest.app.core.humor.HumorProfile
import es.politictest.app.core.model.AnswerOption
import es.politictest.app.core.model.Question
import es.politictest.app.core.model.QuizDataset
import es.politictest.app.core.scoring.NolanCalculationResult
import es.politictest.app.core.scoring.NolanCalculator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed interface QuizUiState {
    data object Loading : QuizUiState
    data class Onboarding(val dataset: QuizDataset) : QuizUiState
    data class InProgress(
        val dataset: QuizDataset,
        val currentIndex: Int,
        val currentQuestion: Question,
        val selectedAnswer: AnswerOption?,
        val answeredCount: Int,
        val totalQuestions: Int = 20
    ) : QuizUiState
    data class Completed(
        val dataset: QuizDataset,
        val nolanResult: NolanCalculationResult,
        val affinities: List<PartyAffinity>,
        val humorProfile: HumorProfile
    ) : QuizUiState
    data class Error(val message: String) : QuizUiState
}

class QuizViewModel(
    application: Application,
    private val repository: QuizRepository = LocalAssetQuizRepository(application)
) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow<QuizUiState>(QuizUiState.Loading)
    val uiState: StateFlow<QuizUiState> = _uiState.asStateFlow()

    private var dataset: QuizDataset? = null
    private val userAnswers = mutableMapOf<String, AnswerOption>()
    private var currentIndex = 0

    init {
        loadDataset()
    }

    fun loadDataset() {
        viewModelScope.launch {
            _uiState.value = QuizUiState.Loading
            try {
                val data = repository.getDataset()
                dataset = data
                _uiState.value = QuizUiState.Onboarding(data)
            } catch (e: Exception) {
                _uiState.value = QuizUiState.Error("Error al cargar las preguntas: ${e.localizedMessage}")
            }
        }
    }

    fun startQuiz() {
        val data = dataset ?: return
        currentIndex = 0
        userAnswers.clear()
        updateInProgressState(data)
    }

    fun selectAnswer(option: AnswerOption) {
        val data = dataset ?: return
        val currentQuestion = data.questions.getOrNull(currentIndex) ?: return

        userAnswers[currentQuestion.id] = option

        if (currentIndex < data.questions.size - 1) {
            currentIndex++
            updateInProgressState(data)
        } else {
            finishQuiz(data)
        }
    }

    fun previousQuestion() {
        val data = dataset ?: return
        if (currentIndex > 0) {
            currentIndex--
            updateInProgressState(data)
        }
    }

    private fun updateInProgressState(data: QuizDataset) {
        val question = data.questions[currentIndex]
        _uiState.update {
            QuizUiState.InProgress(
                dataset = data,
                currentIndex = currentIndex,
                currentQuestion = question,
                selectedAnswer = userAnswers[question.id],
                answeredCount = userAnswers.size,
                totalQuestions = data.questions.size
            )
        }
    }

    private fun finishQuiz(data: QuizDataset) {
        // 1. Motor Nolan
        val nolanResult = NolanCalculator.calculate(data.questions, userAnswers)

        // 2. Módulo de comparación electoral desacoplado
        val affinities = PartyAffinityCalculator.calculateAffinity(
            userScores = nolanResult.questionScores,
            questions = data.questions,
            parties = data.parties
        )

        // 3. Motor humorístico a posteriori
        val humorProfile = HumorEngine.resolve(
            coordinates = nolanResult.coordinates,
            region = nolanResult.region,
            userScores = nolanResult.questionScores,
            archetypes = data.archetypes,
            tensionRules = data.tensionRules
        )

        _uiState.value = QuizUiState.Completed(
            dataset = data,
            nolanResult = nolanResult,
            affinities = affinities,
            humorProfile = humorProfile
        )
    }
}
