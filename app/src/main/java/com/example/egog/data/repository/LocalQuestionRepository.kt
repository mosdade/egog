package com.example.egog.data.repository

import com.example.egog.data.model.Question
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStream

class LocalQuestionRepository {
    private val gson = Gson()
    
    suspend fun loadQuestionsFromAssets(
        inputStream: InputStream?,
        examType: String
    ): List<Question> = withContext(Dispatchers.IO) {
        if (inputStream == null) return@withContext emptyList()
        
        try {
            val json = inputStream.bufferedReader().use { it.readText() }
            val listType = object : TypeToken<List<QuestionJson>>() {}.type
            val questionsJson: List<QuestionJson> = gson.fromJson(json, listType)
            
            questionsJson.mapNotNull { q ->
                try {
                    Question(
                        id = q.id ?: "",
                        guid = q.guid,
                        hint = q.hint ?: "",
                        codifier = q.codifier ?: emptyList(),
                        question = q.question ?: "",
                        problem = q.problem ?: "",
                        img = q.img ?: emptyList(),
                        imgUrls = q.imgUrls ?: emptyList(),
                        audioUrls = q.audioUrls ?: emptyList(),
                        numberInGroup = q.numberInGroup ?: "",
                        answerType = q.answerType ?: "",
                        answer = q.answer ?: ""
                    )
                } catch (e: Exception) {
                    null
                }
            }
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    private data class QuestionJson(
        val id: String?,
        val guid: String?,
        val hint: String?,
        val codifier: List<String>?,
        val question: String?,
        val problem: String?,
        val img: List<String>?,
        val imgUrls: List<String>?,
        val audioUrls: List<String>?,
        val numberInGroup: String?,
        val answerType: String?,
        val answer: String?
    )
}

