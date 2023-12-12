package com.lingoscan.compose.screens.learning

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.sharp.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.lingoscan.compose.components.common.SelectDictionaryDialog
import com.lingoscan.compose.navigation.Routes
import com.lingoscan.presentations.StatisticPresentation
import com.lingoscan.viewmodels.MainViewModel

@Composable fun LearningScreen(
    navController: NavController
) {

    val mainViewModel = hiltViewModel<MainViewModel>()

    val dictionaries by mainViewModel.dictionaries.collectAsState()

    val statistics by mainViewModel.statistics.collectAsState()

    var testType by remember {
        mutableStateOf<TestType?>(null)
    }

    var showSelectDictionaryDialog by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(Unit) {
        mainViewModel.getDictionaries()
        mainViewModel.getStatistics()
    }


    Column(
        modifier = Modifier
            .background(Color.White)
            .padding(10.dp)
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Row() {
            Text(text = "Quizzes")
            Icon(
                painter = rememberVectorPainter(image = Icons.Sharp.Star),
                contentDescription = null
            )
        }
        Spacer(modifier = Modifier.height(10.dp))

        LearningTestItem(
            title = "Word Selection",
            subtitle = "In this test you have to choose one correct word from four options.",
            onClick = {
                testType = TestType.WORD_SELECTION
                showSelectDictionaryDialog = true
            }
        )

//        LearningTest(
//            title = "Fill the gap",
//            subtitle = "You have to fill the gap with correct word.",
//            onClick = {
//
//            }
//        )

        Spacer(modifier = Modifier.height(15.dp))

        Row {
            Text(text = "History")
            Icon(
                painter = rememberVectorPainter(image = Icons.Outlined.DateRange),
                contentDescription = null,
                tint = Color.Unspecified
            )
        }
        Spacer(modifier = Modifier.height(10.dp))

        statistics?.forEach {
            HistoryItem(presentation = it)
            Spacer(modifier = Modifier.height(10.dp))
        }

        if (statistics.isNullOrEmpty()) {
            Text(text = "No completed quizzes yet")
        }

    }

    if (showSelectDictionaryDialog) {
        dictionaries?.let { it ->
            SelectDictionaryDialog(
                dictionaries = it.filter { it.words.size >= 4 },
                noDictionariesText = "You have no dictionaries with 4 or more words. Add more words and come back later!",
                onDismissRequest = {
                    testType = null
                    showSelectDictionaryDialog = false
                },
                onSelectDictionary = {
                    showSelectDictionaryDialog = false
                    if (testType == TestType.WORD_SELECTION) {
                        navController.navigate("${Routes.LearningScreen.Quiz}/${it.id}")
                    }
                }
            )
        }
    }
}


@Composable
fun LearningTestItem(
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .clickable {
                onClick.invoke()
            }
    ) {
        Column(
            modifier = Modifier
                .padding(10.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = title, style = MaterialTheme.typography.headlineSmall, color = Color.Black
            )
            Text(text = subtitle, style = MaterialTheme.typography.bodyMedium)
        }
    }
}


@Composable
fun HistoryItem(
    presentation: StatisticPresentation
) {

    val score by remember {
        mutableIntStateOf(((presentation.wordsCorrect.toFloat() / presentation.wordsTotal.toFloat()) * 100).toInt())
    }

    Card(
        colors = CardDefaults.cardColors(
            containerColor = Color.White,
        ),

        border = BorderStroke(1.dp, Color.Black)
    ) {
        Row(modifier = Modifier.padding(10.dp)) {
            Column(Modifier.weight(1f)) {
                Text(text = "Date: ${presentation.date}", color = Color.Black)
                Text(text = "Dictionary: ${presentation.dictionary_name}", color = Color.Black)
                Text(text = "Test: ${presentation.test_type}", color = Color.Black)
                Text(
                    text = "Results: ${presentation.wordsCorrect}/${presentation.wordsTotal}",
                    color = Color.Black
                )
            }

            ResultImage(modifier = Modifier.size(90.dp), result = score)
        }
    }
}

@Preview
@Composable
fun HistoryItemPreview() {
    HistoryItem(
        presentation = StatisticPresentation(
            id = "",
            dictionary_name = "Dictionary name",
            wordsCorrect = 10,
            wordsTotal = 20,
            date = "2021-10-10",
            test_type = "WORD_SELECTION",
            language = "en"
        )
    )
}

enum class TestType {
    WORD_SELECTION,
    FILL_THE_GAP
}