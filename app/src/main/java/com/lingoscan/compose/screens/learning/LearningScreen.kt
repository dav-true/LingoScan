package com.lingoscan.compose.screens.learning

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.lingoscan.compose.components.common.SelectDictionaryDialog
import com.lingoscan.compose.navigation.Routes
import com.lingoscan.viewmodels.MainViewModel

@Composable fun LearningScreen(
    navController: NavController
) {

    val mainViewModel = hiltViewModel<MainViewModel>()

    val dictionaries by mainViewModel.dictionaries.collectAsState()

    var testType by remember {
        mutableStateOf<TestType?>(null)
    }

    var showSelectDictionaryDialog by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(Unit) {
        mainViewModel.getDictionaries()
    }


    Column(
        modifier = Modifier
            .background(Color.White)
            .fillMaxSize()
    ) {
        LearningTest(
            title = "Word Selection",
            subtitle = "In this test you have to choose one correct word from four options.",
            onClick = {
                testType = TestType.WORD_SELECTION
                showSelectDictionaryDialog = true
            }
        )

        LearningTest(
            title = "Fill the gap",
            subtitle = "You have to fill the gap with correct word.",
            onClick = {

            }
        )
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
fun LearningTest(
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {

    Card(
        modifier = Modifier
            .padding(10.dp)
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

enum class TestType {
    WORD_SELECTION,
    FILL_THE_GAP
}