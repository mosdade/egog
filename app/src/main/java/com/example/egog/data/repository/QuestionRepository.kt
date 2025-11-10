package com.example.egog.data.repository

import android.content.Context
import com.example.egog.data.cache.QuestionCache
import com.example.egog.data.model.Question
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withTimeout
import kotlinx.coroutines.withTimeoutOrNull
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class QuestionRepository(private val context: Context? = null) {
    private val firestore = FirebaseFirestore.getInstance()
    private val localRepository = LocalQuestionRepository()
    private val cache = context?.let { QuestionCache(it) }
    
    suspend fun getQuestions(subjectCode: String): Flow<List<Question>> = flow {
        // Сначала пытаемся загрузить из кеша
        val cachedQuestions = cache?.getCachedQuestions(subjectCode)
        if (cachedQuestions != null && cachedQuestions.isNotEmpty()) {
            emit(cachedQuestions)
        }
        
        // Параллельно загружаем из Firebase и локальных файлов
        coroutineScope {
            val firebaseDeferred = async(Dispatchers.IO) {
                // Таймаут для Firebase - 5 секунд
                withTimeoutOrNull(5000) {
                    loadFromFirebase(subjectCode)
                } ?: emptyList()
            }
            
            val localDeferred = async(Dispatchers.IO) {
                loadFromLocal(subjectCode)
            }
            
            val firebaseQuestions = firebaseDeferred.await()
            val localQuestions = localDeferred.await()
            
            // Приоритет: Firebase > Local
            val questions = when {
                firebaseQuestions.isNotEmpty() -> {
                    // Кешируем данные из Firebase
                    cache?.cacheQuestions(subjectCode, firebaseQuestions)
                    firebaseQuestions
                }
                localQuestions.isNotEmpty() -> {
                    // Кешируем данные из локальных файлов
                    cache?.cacheQuestions(subjectCode, localQuestions)
                    localQuestions
                }
                else -> emptyList()
            }
            
            // Если есть новые данные, отдаем их
            if (questions.isNotEmpty() && questions != cachedQuestions) {
                emit(questions)
            } else if (questions.isEmpty() && cachedQuestions == null) {
                // Если нет данных нигде, отдаем пустой список
                emit(emptyList())
            }
        }
    }.flowOn(Dispatchers.IO)
    
    private suspend fun loadFromFirebase(subjectCode: String): List<Question> = withContext(Dispatchers.IO) {
        try {
            val snapshot = firestore
                .collection("questions")
                .whereEqualTo("subjectCode", subjectCode)
                .get()
                .await()
            
            snapshot.documents.mapNotNull { doc ->
                try {
                    Question(
                        id = doc.getString("id") ?: "",
                        guid = doc.getString("guid"),
                        hint = doc.getString("hint") ?: "",
                        codifier = (doc.get("codifier") as? List<*>)?.mapNotNull { it.toString() } ?: emptyList(),
                        question = doc.getString("question") ?: "",
                        problem = doc.getString("problem") ?: "",
                        img = (doc.get("img") as? List<*>)?.mapNotNull { it.toString() } ?: emptyList(),
                        imgUrls = (doc.get("imgUrls") as? List<*>)?.mapNotNull { it.toString() } ?: emptyList(),
                        audioUrls = (doc.get("audioUrls") as? List<*>)?.mapNotNull { it.toString() } ?: emptyList(),
                        numberInGroup = doc.getString("numberInGroup") ?: "",
                        answerType = doc.getString("answerType") ?: "",
                        answer = doc.getString("answer") ?: ""
                    )
                } catch (e: Exception) {
                    null
                }
            }
        } catch (e: Exception) {
            // Игнорируем ошибки Firestore (PERMISSION_DENIED, API не включен и т.д.)
            // Возвращаем пустой список, чтобы попробовать загрузить из локальных файлов
            emptyList()
        }
    }
    
    private suspend fun loadFromLocal(subjectCode: String): List<Question> = withContext(Dispatchers.IO) {
        if (context == null) return@withContext emptyList()
        
        try {
            // Определяем тип экзамена из кода предмета
            val examType = when {
                subjectCode.contains("ege") -> "ege"
                subjectCode.contains("oge") -> "oge"
                else -> "ege"
            }
            
            val fileName = "${examType}_questions.json"
            val inputStream = context.assets.open(fileName)
            localRepository.loadQuestionsFromAssets(inputStream, examType)
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    suspend fun getQuestionById(questionId: String, subjectCode: String? = null): Question? = withContext(Dispatchers.IO) {
        // Сначала ищем в кеше по subjectCode, если он указан
        if (subjectCode != null && cache != null) {
            val cachedQuestions = cache.getCachedQuestions(subjectCode)
            cachedQuestions?.firstOrNull { it.id == questionId }?.let {
                return@withContext it
            }
        }
        
        // Параллельно ищем в Firebase и локальных файлах
        coroutineScope {
            val firebaseDeferred = async {
                try {
                    val snapshot = firestore
                        .collection("questions")
                        .whereEqualTo("id", questionId)
                        .limit(1)
                        .get()
                        .await()
                    
                    snapshot.documents.firstOrNull()?.let { doc ->
                        Question(
                            id = doc.getString("id") ?: "",
                            guid = doc.getString("guid"),
                            hint = doc.getString("hint") ?: "",
                            codifier = (doc.get("codifier") as? List<*>)?.mapNotNull { it.toString() } ?: emptyList(),
                            question = doc.getString("question") ?: "",
                            problem = doc.getString("problem") ?: "",
                            img = (doc.get("img") as? List<*>)?.mapNotNull { it.toString() } ?: emptyList(),
                            imgUrls = (doc.get("imgUrls") as? List<*>)?.mapNotNull { it.toString() } ?: emptyList(),
                            audioUrls = (doc.get("audioUrls") as? List<*>)?.mapNotNull { it.toString() } ?: emptyList(),
                            numberInGroup = doc.getString("numberInGroup") ?: "",
                            answerType = doc.getString("answerType") ?: "",
                            answer = doc.getString("answer") ?: ""
                        )
                    }
                } catch (e: Exception) {
                    null
                }
            }
            
            val localDeferred = async {
                if (context == null) return@async null
                
                try {
                    val examTypes = listOf("ege", "oge")
                    for (examType in examTypes) {
                        val fileName = "${examType}_questions.json"
                        val inputStream = context.assets.open(fileName)
                        val questions = localRepository.loadQuestionsFromAssets(inputStream, examType)
                        questions.firstOrNull { it.id == questionId }?.let {
                            return@async it
                        }
                    }
                } catch (e: Exception) {
                    // Файлы не найдены
                }
                null
            }
            
            // Приоритет: Firebase > Local
            firebaseDeferred.await() ?: localDeferred.await()
        }
    }
}

