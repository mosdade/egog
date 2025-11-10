package com.example.egog.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.egog.ui.screens.ExamSelectionScreen
import com.example.egog.ui.screens.DisclaimerScreen
import com.example.egog.ui.screens.SubjectSelectionScreen
import com.example.egog.ui.screens.QuestionsListScreen
import com.example.egog.ui.screens.QuestionScreen

sealed class Screen(val route: String) {
    object ExamSelection : Screen("exam_selection")
    object Disclaimer : Screen("disclaimer/{examType}") {
        fun createRoute(examType: String) = "disclaimer/$examType"
    }
    object SubjectSelection : Screen("subject_selection/{examType}") {
        fun createRoute(examType: String) = "subject_selection/$examType"
    }
    object QuestionsList : Screen("questions_list/{subjectCode}") {
        fun createRoute(subjectCode: String) = "questions_list/$subjectCode"
    }
    object Question : Screen("question/{questionId}") {
        fun createRoute(questionId: String) = "question/$questionId"
    }
}

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.ExamSelection.route
    ) {
        composable(Screen.ExamSelection.route) {
            ExamSelectionScreen(
                onExamSelected = { examType ->
                    navController.navigate(Screen.Disclaimer.createRoute(examType))
                }
            )
        }
        
        composable(Screen.Disclaimer.route) { backStackEntry ->
            val examType = backStackEntry.arguments?.getString("examType") ?: ""
            DisclaimerScreen(
                examType = examType,
                onAccept = {
                    navController.navigate(Screen.SubjectSelection.createRoute(examType))
                },
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
        
        composable(Screen.SubjectSelection.route) { backStackEntry ->
            val examType = backStackEntry.arguments?.getString("examType") ?: ""
            SubjectSelectionScreen(
                examType = examType,
                onSubjectSelected = { subjectCode ->
                    navController.navigate(Screen.QuestionsList.createRoute(subjectCode))
                },
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
        
        composable(Screen.QuestionsList.route) { backStackEntry ->
            val subjectCode = backStackEntry.arguments?.getString("subjectCode") ?: ""
            QuestionsListScreen(
                subjectCode = subjectCode,
                onQuestionSelected = { questionId ->
                    navController.navigate(Screen.Question.createRoute(questionId))
                },
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
        
        composable(Screen.Question.route) { backStackEntry ->
            val questionId = backStackEntry.arguments?.getString("questionId") ?: ""
            QuestionScreen(
                questionId = questionId,
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
    }
}

