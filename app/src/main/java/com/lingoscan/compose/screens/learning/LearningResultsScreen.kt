package com.lingoscan.compose.screens.learning

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.lingoscan.viewmodels.LearningViewModel


@Composable
fun LearningResultsScreen(
    navController: NavHostController
) {
    val learningViewModel = hiltViewModel<LearningViewModel>()

    Log.w(
        "learning-results",
        "${learningViewModel.currentScore.value} + ${learningViewModel.currentScore}"
    )
}