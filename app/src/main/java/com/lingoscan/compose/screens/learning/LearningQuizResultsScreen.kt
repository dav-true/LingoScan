package com.lingoscan.compose.screens.learning

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.lingoscan.R


@Composable
fun LearningQuizResultsScreen(
    navController: NavHostController,
    userScore: Int,
    totalWordsCount: Int
) {

    val result = ((userScore.toFloat() / totalWordsCount.toFloat()) * 100).toInt()

    Column(
        modifier = Modifier.fillMaxSize().padding(10.dp),
        horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
    ) {
        if (result >= 80) {
            Image(painter = painterResource(id = R.drawable.happy), contentDescription = null)
        } else if (result >= 50) {
            Image(painter = painterResource(id = R.drawable.neutral), contentDescription = null)
        } else {
            Image(painter = painterResource(id = R.drawable.sad), contentDescription = null)
        }

        Text(text = "Your score is $result%", style = MaterialTheme.typography.headlineLarge)
        Text(
            text = "Correct words: $userScore/$totalWordsCount",
            style = MaterialTheme.typography.headlineLarge
        )
    }
}