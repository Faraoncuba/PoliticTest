package es.politictest.app.core.data

import android.content.Context
import es.politictest.app.core.model.QuizDataset
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json

interface QuizRepository {
    suspend fun getDataset(): QuizDataset
}

class LocalAssetQuizRepository(
    private val context: Context,
    private val json: Json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        coerceInputValues = true
    }
) : QuizRepository {

    private var cachedDataset: QuizDataset? = null

    override suspend fun getDataset(): QuizDataset = withContext(Dispatchers.IO) {
        cachedDataset?.let { return@withContext it }

        val jsonString = context.assets.open("data/electoral_dataset_v1.json")
            .bufferedReader()
            .use { it.readText() }

        val dataset = json.decodeFromString<QuizDataset>(jsonString)
        cachedDataset = dataset
        dataset
    }
}
