package com.example.egog.data.model

data class UserAnswer(
    val questionId: String,
    val userAnswer: String,
    val isCorrect: Boolean? = null
)

