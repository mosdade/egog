package com.example.egog.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.egog.data.model.Question
import com.example.egog.data.repository.QuestionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class QuestionViewModel(
    application: Application
) : AndroidViewModel(application) {
    private val repository = QuestionRepository(application.applicationContext)
    
    private val _question = MutableStateFlow<Question?>(null)
    val question: StateFlow<Question?> = _question.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    fun loadQuestion(questionId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _question.value = repository.getQuestionById(questionId)
            } catch (e: Exception) {
                _question.value = null
            } finally {
                _isLoading.value = false
            }
        }
    }
}

