package com.example.egog.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.egog.data.model.Question
import com.example.egog.data.repository.QuestionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull

class QuestionsListViewModel(
    application: Application
) : AndroidViewModel(application) {
    private val repository = QuestionRepository(application.applicationContext)
    
    private val _questions = MutableStateFlow<List<Question>>(emptyList())
    val questions: StateFlow<List<Question>> = _questions.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    
    fun loadQuestions(subjectCode: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                // Используем Flow для получения данных с кешированием
                // Таймаут 10 секунд на загрузку
                val questionsList = withTimeoutOrNull(10000) {
                    repository.getQuestions(subjectCode).first()
                } ?: emptyList()
                
                _questions.value = questionsList
                _isLoading.value = false
                
                if (questionsList.isEmpty()) {
                    _error.value = "Вопросы не найдены.\n\nДля работы приложения:\n1. Включите Firestore API в Google Cloud Console\n2. Или поместите JSON файлы в папку app/src/main/assets/"
                }
            } catch (e: Exception) {
                _questions.value = emptyList()
                _isLoading.value = false
                val errorMessage = when {
                    e.message?.contains("PERMISSION_DENIED") == true -> 
                        "Firestore API не включен. Включите его в Google Cloud Console:\nhttps://console.developers.google.com/apis/api/firestore.googleapis.com/overview?project=egog-771fc"
                    e.message?.contains("timeout") == true -> 
                        "Превышено время ожидания. Проверьте подключение к интернету."
                    else -> 
                        "Ошибка загрузки: ${e.message}"
                }
                _error.value = errorMessage
            }
        }
    }
}

