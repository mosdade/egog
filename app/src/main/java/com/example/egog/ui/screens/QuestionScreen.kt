package com.example.egog.ui.screens

import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage
import com.example.egog.data.model.AnswerType
import com.example.egog.data.model.AnswerOption
import com.example.egog.data.model.toAnswerType
import com.example.egog.ui.viewmodel.QuestionViewModel
import com.example.egog.utils.AnswerParser

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuestionScreen(
    questionId: String,
    onBackClick: () -> Unit,
    viewModel: QuestionViewModel = viewModel(
        factory = androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.getInstance(
            LocalContext.current.applicationContext as android.app.Application
        )
    )
) {
    viewModel.loadQuestion(questionId)
    val question by viewModel.question.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    
    // Парсим варианты ответов и тип вопроса
    val answerOptions = remember(question?.problem) {
        question?.problem?.let { AnswerParser.parseAnswerOptions(it) } ?: emptyList()
    }
    
    val answerType = remember(question?.answerType) {
        question?.answerType?.toAnswerType() ?: AnswerType.UNKNOWN
    }
    
    // Состояние для разных типов вопросов
    var userTextAnswer by remember { mutableStateOf("") }
    var selectedSingleChoice by remember { mutableStateOf<String?>(null) }
    var selectedMultipleChoice by remember { mutableStateOf<Set<String>>(emptySet()) }
    
    var showResult by remember { mutableStateOf(false) }
    var isCorrect by remember { mutableStateOf<Boolean?>(null) }
    var correctAnswerText by remember { mutableStateOf("") }
    
    // Парсим правильный ответ из HTML
    val parsedCorrectAnswer = remember(question?.problem, answerType) {
        question?.problem?.let { AnswerParser.parseCorrectAnswer(it, answerType) } ?: ""
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Вопрос") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Назад"
                        )
                    }
                }
            )
        },
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // Показываем соответствующий UI в зависимости от типа вопроса
                when (answerType) {
                    AnswerType.SHORT_ANSWER -> {
                        OutlinedTextField(
                            value = userTextAnswer,
                            onValueChange = { userTextAnswer = it },
                            label = { Text("Ваш ответ") },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = !showResult
                        )
                    }
                    
                    AnswerType.SINGLE_CHOICE -> {
                        if (answerOptions.isNotEmpty()) {
                            Column(modifier = Modifier.selectableGroup()) {
                                answerOptions.forEach { option ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .selectable(
                                                selected = selectedSingleChoice == option.id,
                                                onClick = { selectedSingleChoice = option.id },
                                                role = Role.RadioButton
                                            )
                                            .padding(vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        RadioButton(
                                            selected = selectedSingleChoice == option.id,
                                            onClick = { selectedSingleChoice = option.id },
                                            enabled = !showResult
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = option.text,
                                            modifier = Modifier.weight(1f)
                                        )
                                    }
                                }
                            }
                        } else {
                            OutlinedTextField(
                                value = userTextAnswer,
                                onValueChange = { userTextAnswer = it },
                                label = { Text("Ваш ответ") },
                                modifier = Modifier.fillMaxWidth(),
                                enabled = !showResult
                            )
                        }
                    }
                    
                    AnswerType.MULTIPLE_CHOICE -> {
                        if (answerOptions.isNotEmpty()) {
                            Column {
                                answerOptions.forEach { option ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Checkbox(
                                            checked = selectedMultipleChoice.contains(option.id),
                                            onCheckedChange = { checked ->
                                                selectedMultipleChoice = if (checked) {
                                                    selectedMultipleChoice + option.id
                                                } else {
                                                    selectedMultipleChoice - option.id
                                                }
                                            },
                                            enabled = !showResult
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = option.text,
                                            modifier = Modifier.weight(1f)
                                        )
                                    }
                                }
                            }
                        } else {
                            OutlinedTextField(
                                value = userTextAnswer,
                                onValueChange = { userTextAnswer = it },
                                label = { Text("Ваш ответ (через запятую)") },
                                modifier = Modifier.fillMaxWidth(),
                                enabled = !showResult
                            )
                        }
                    }
                    
                    else -> {
                        OutlinedTextField(
                            value = userTextAnswer,
                            onValueChange = { userTextAnswer = it },
                            label = { Text("Ваш ответ") },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = !showResult
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            if (question != null) {
                                val userAnswer = when (answerType) {
                                    AnswerType.SHORT_ANSWER -> userTextAnswer
                                    AnswerType.SINGLE_CHOICE -> {
                                        selectedSingleChoice ?: userTextAnswer
                                    }
                                    AnswerType.MULTIPLE_CHOICE -> {
                                        if (answerOptions.isNotEmpty()) {
                                            selectedMultipleChoice.joinToString(", ")
                                        } else {
                                            userTextAnswer
                                        }
                                    }
                                    else -> userTextAnswer
                                }
                                
                                val currentQuestion = question
                                if (currentQuestion != null) {
                                    val correctAnswer = parsedCorrectAnswer.ifBlank { currentQuestion.answer }
                                    correctAnswerText = correctAnswer
                                    
                                    val correct = AnswerParser.checkAnswer(
                                        userAnswer = userAnswer,
                                        correctAnswer = correctAnswer,
                                        answerType = answerType,
                                        options = answerOptions
                                    )
                                    
                                    isCorrect = correct
                                    showResult = true
                                }
                            }
                        },
                        modifier = Modifier.weight(1f),
                        enabled = when (answerType) {
                            AnswerType.SHORT_ANSWER -> userTextAnswer.isNotBlank() && !showResult
                            AnswerType.SINGLE_CHOICE -> (selectedSingleChoice != null || userTextAnswer.isNotBlank()) && !showResult
                            AnswerType.MULTIPLE_CHOICE -> (selectedMultipleChoice.isNotEmpty() || userTextAnswer.isNotBlank()) && !showResult
                            else -> userTextAnswer.isNotBlank() && !showResult
                        }
                    ) {
                        Text("Проверить")
                    }
                    
                    if (showResult) {
                        OutlinedButton(
                            onClick = {
                                showResult = false
                                userTextAnswer = ""
                                selectedSingleChoice = null
                                selectedMultipleChoice = emptySet()
                                isCorrect = null
                                correctAnswerText = ""
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Заново")
                        }
                    }
                }
                
                if (showResult && isCorrect != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = if (isCorrect == true) {
                                MaterialTheme.colorScheme.primaryContainer
                            } else {
                                MaterialTheme.colorScheme.errorContainer
                            }
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Text(
                                text = if (isCorrect == true) "✓ Правильно!" else "✗ Неправильно",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (isCorrect == true) {
                                    MaterialTheme.colorScheme.onPrimaryContainer
                                } else {
                                    MaterialTheme.colorScheme.onErrorContainer
                                }
                            )
                            if (correctAnswerText.isNotBlank()) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Правильный ответ: $correctAnswerText",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (question != null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                // Отображение изображений
                question!!.imgUrls.forEach { imgUrl ->
                    AsyncImage(
                        model = imgUrl,
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    )
                }
                
                // Отображение HTML контента вопроса
                if (question!!.problem.isNotBlank()) {
                    HtmlContent(html = question!!.problem)
                }
                
                // Текст вопроса
                if (question!!.question.isNotBlank()) {
                    Text(
                        text = question!!.question,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
                
                // Подсказка
                if (question!!.hint.isNotBlank()) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Text(
                            text = "Подсказка: ${question!!.hint}",
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }
                
                // Кодификатор
                if (question!!.codifier.isNotEmpty()) {
                    question!!.codifier.forEach { codifier ->
                        Text(
                            text = codifier,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }
                }
                
                // Тип ответа
                if (question!!.answerType.isNotBlank()) {
                    Text(
                        text = "Тип ответа: ${question!!.answerType}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun HtmlContent(html: String) {
    AndroidView(
        factory = { context ->
            WebView(context).apply {
                webViewClient = WebViewClient()
                settings.javaScriptEnabled = true
                loadDataWithBaseURL(null, html, "text/html", "UTF-8", null)
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(400.dp)
    )
}
