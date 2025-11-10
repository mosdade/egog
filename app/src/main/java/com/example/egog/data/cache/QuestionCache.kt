package com.example.egog.data.cache

import android.content.Context
import android.content.SharedPreferences
import com.example.egog.data.model.Question
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class QuestionCache(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences(
        "question_cache",
        Context.MODE_PRIVATE
    )
    private val gson = Gson()
    
    companion object {
        private const val CACHE_EXPIRY_TIME = 24 * 60 * 60 * 1000L // 24 часа
        private const val KEY_PREFIX = "questions_"
        private const val TIMESTAMP_PREFIX = "timestamp_"
    }
    
    suspend fun getCachedQuestions(subjectCode: String): List<Question>? = withContext(Dispatchers.IO) {
        val timestamp = prefs.getLong("${TIMESTAMP_PREFIX}$subjectCode", 0)
        val currentTime = System.currentTimeMillis()
        
        // Проверяем, не истек ли кеш
        if (currentTime - timestamp > CACHE_EXPIRY_TIME) {
            return@withContext null
        }
        
        val cachedJson = prefs.getString("${KEY_PREFIX}$subjectCode", null) ?: return@withContext null
        
        try {
            val listType = object : TypeToken<List<Question>>() {}.type
            gson.fromJson<List<Question>>(cachedJson, listType)
        } catch (e: Exception) {
            null
        }
    }
    
    suspend fun cacheQuestions(subjectCode: String, questions: List<Question>) = withContext(Dispatchers.IO) {
        try {
            val json = gson.toJson(questions)
            prefs.edit()
                .putString("${KEY_PREFIX}$subjectCode", json)
                .putLong("${TIMESTAMP_PREFIX}$subjectCode", System.currentTimeMillis())
                .apply()
        } catch (e: Exception) {
            // Игнорируем ошибки кеширования
        }
    }
    
    suspend fun clearCache(subjectCode: String) = withContext(Dispatchers.IO) {
        prefs.edit()
            .remove("${KEY_PREFIX}$subjectCode")
            .remove("${TIMESTAMP_PREFIX}$subjectCode")
            .apply()
    }
    
    suspend fun clearAllCache() = withContext(Dispatchers.IO) {
        prefs.edit().clear().apply()
    }
    
    fun getCacheSize(subjectCode: String): Long {
        val cachedJson = prefs.getString("${KEY_PREFIX}$subjectCode", null) ?: return 0
        return cachedJson.length.toLong()
    }
}

