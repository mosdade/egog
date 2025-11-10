package com.example.egog.utils

import com.example.egog.data.model.AnswerOption
import org.jsoup.Jsoup
import org.jsoup.nodes.Element

object AnswerParser {
    
    /**
     * Парсит варианты ответов из HTML с distractors-table
     * @param html HTML контент вопроса
     * @return Список вариантов ответов
     */
    fun parseAnswerOptions(html: String): List<AnswerOption> {
        val options = mutableListOf<AnswerOption>()
        
        try {
            val doc = Jsoup.parse(html)
            val distractorsTable = doc.select("table.distractors-table").first()
                ?: doc.select("table").firstOrNull { 
                    it.select("input[type=radio], input[type=checkbox]").isNotEmpty() 
                }
            
            if (distractorsTable != null) {
                // Ищем все input элементы (radio или checkbox)
                val inputs = distractorsTable.select("input[type=radio], input[type=checkbox]")
                
                inputs.forEachIndexed { index, input ->
                    val isChecked = input.hasAttr("checked")
                    val value = input.attr("value")
                    
                    // Получаем текст варианта ответа
                    val text = getOptionText(input)
                    
                    if (text.isNotBlank()) {
                        options.add(
                            AnswerOption(
                                id = value.ifBlank { "option_$index" },
                                text = text,
                                isCorrect = isChecked
                            )
                        )
                    }
                }
                
                // Если не нашли input, пытаемся извлечь из ячеек таблицы
                if (options.isEmpty()) {
                    val cells = distractorsTable.select("td, th")
                    cells.forEachIndexed { index, cell ->
                        val text = cell.text().trim()
                        if (text.isNotBlank() && text.length > 1) {
                            options.add(
                                AnswerOption(
                                    id = "option_$index",
                                    text = text,
                                    isCorrect = false // Не можем определить без checked атрибута
                                )
                            )
                        }
                    }
                }
            }
        } catch (e: Exception) {
            // Если парсинг не удался, возвращаем пустой список
        }
        
        return options
    }
    
    /**
     * Извлекает текст варианта ответа из элемента input
     */
    private fun getOptionText(input: Element): String {
        // Ищем текст в родительском элементе (label или td)
        var parent = input.parent()
        
        // Пытаемся найти label
        val label = parent.select("label").first()
        if (label != null) {
            return label.text().trim()
        }
        
        // Или текст в родительской ячейке
        val td = parent.select("td").first() ?: parent
        val text = td.text().trim()
        
        // Убираем текст самого input
        return text.replace(input.attr("value"), "").trim()
    }
    
    /**
     * Парсит правильный ответ из HTML
     * Ищет checked атрибуты у radio/checkbox или текст в answer-table
     */
    fun parseCorrectAnswer(html: String, answerType: com.example.egog.data.model.AnswerType): String {
        return try {
            val doc = Jsoup.parse(html)
            
            when (answerType) {
                com.example.egog.data.model.AnswerType.SINGLE_CHOICE -> {
                    // Для единичного выбора ищем checked radio
                    val checkedRadio = doc.select("input[type=radio][checked]").first()
                    checkedRadio?.let {
                        getOptionText(it)
                    } ?: ""
                }
                
                com.example.egog.data.model.AnswerType.MULTIPLE_CHOICE -> {
                    // Для множественного выбора собираем все checked checkbox
                    val checkedBoxes = doc.select("input[type=checkbox][checked]")
                    checkedBoxes.map { getOptionText(it) }
                        .filter { it.isNotBlank() }
                        .joinToString(", ")
                }
                
                com.example.egog.data.model.AnswerType.SHORT_ANSWER -> {
                    // Для краткого ответа ищем в answer-table
                    val answerTable = doc.select("table.answer-table").first()
                    answerTable?.text()?.trim() ?: ""
                }
                
                else -> ""
            }
        } catch (e: Exception) {
            ""
        }
    }
    
    /**
     * Проверяет правильность ответа пользователя
     */
    fun checkAnswer(
        userAnswer: String,
        correctAnswer: String,
        answerType: com.example.egog.data.model.AnswerType,
        options: List<AnswerOption> = emptyList()
    ): Boolean {
        return when (answerType) {
            com.example.egog.data.model.AnswerType.SHORT_ANSWER -> {
                // Для краткого ответа сравниваем строки (без учета регистра и пробелов)
                val normalizedUser = userAnswer.trim().lowercase().replace("\\s+".toRegex(), " ")
                val normalizedCorrect = correctAnswer.trim().lowercase().replace("\\s+".toRegex(), " ")
                normalizedUser == normalizedCorrect
            }
            
            com.example.egog.data.model.AnswerType.SINGLE_CHOICE -> {
                // Для единичного выбора сравниваем выбранный вариант
                val userSelected = options.find { it.id == userAnswer || it.text == userAnswer }
                userSelected?.isCorrect == true
            }
            
            com.example.egog.data.model.AnswerType.MULTIPLE_CHOICE -> {
                // Для множественного выбора проверяем, что выбраны все правильные и только они
                val userSelections = userAnswer.split(",").map { it.trim() }
                val correctSelections = options.filter { it.isCorrect }.map { it.id }
                
                userSelections.size == correctSelections.size &&
                userSelections.all { selection ->
                    correctSelections.any { correct ->
                        selection == correct || options.find { it.id == selection }?.text == options.find { it.id == correct }?.text
                    }
                }
            }
            
            com.example.egog.data.model.AnswerType.MATCHING -> {
                // Для соответствия сравниваем строки
                userAnswer.trim().equals(correctAnswer.trim(), ignoreCase = true)
            }
            
            else -> false
        }
    }
}

