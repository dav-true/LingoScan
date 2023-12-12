package com.lingoscan.compose.screens.learning

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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

    val result by remember {
        mutableIntStateOf(
            ((userScore.toFloat() / totalWordsCount.toFloat()) * 100).toInt()
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp),
        horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
    ) {
        ResultImage(
            result = result
        )
        Text(
            text = "Your score is $result%",
            style = MaterialTheme.typography.headlineLarge
        )
        Text(
            text = "Correct words: $userScore/$totalWordsCount",
            style = MaterialTheme.typography.headlineLarge
        )
    }
}


@Composable
fun ResultImage(
    modifier: Modifier = Modifier,
    result: Int
) {
    val imageResource = when {
        result >= 80 -> R.drawable.happy
        result >= 50 -> R.drawable.neutral
        else -> R.drawable.sad
    }

    Image(
        modifier = modifier,
        painter = painterResource(id = imageResource),
        contentDescription = null,
    )
}