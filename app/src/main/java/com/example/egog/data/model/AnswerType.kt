package com.example.egog.data.model

enum class AnswerType(val displayName: String) {
    SHORT_ANSWER("Краткий ответ"),
    SINGLE_CHOICE("Единичный выбор"),
    MULTIPLE_CHOICE("Множественный выбор"),
    MATCHING("Соответствие"),
    UNKNOWN("Неизвестный тип")
}

fun String.toAnswerType(): AnswerType {
    return when {
        this.contains("Краткий ответ", ignoreCase = true) -> AnswerType.SHORT_ANSWER
        this.contains("Единичный выбор", ignoreCase = true) -> AnswerType.SINGLE_CHOICE
        this.contains("Множественный выбор", ignoreCase = true) -> AnswerType.MULTIPLE_CHOICE
        this.contains("Соответствие", ignoreCase = true) -> AnswerType.MATCHING
        else -> AnswerType.UNKNOWN
    }
}

