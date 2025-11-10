package com.example.egog.data.model

data class Question(
    val id: String,
    val guid: String?,
    val hint: String,
    val codifier: List<String>,
    val question: String,
    val problem: String, // HTML content
    val img: List<String>,
    val imgUrls: List<String>,
    val audioUrls: List<String> = emptyList(),
    val numberInGroup: String,
    val answerType: String,
    val answer: String
)

