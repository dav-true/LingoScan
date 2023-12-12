package com.lingoscan.compose.screens.learning

import android.util.Log
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.animation.with
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.lingoscan.R
import com.lingoscan.compose.navigation.Routes
import com.lingoscan.presentations.WordPresentation
import com.lingoscan.utils.scan.ImageUtils
import com.lingoscan.viewmodels.LearningViewModel
import com.lingoscan.viewmodels.MainViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LearningWordSelectionScreen(
    navController: NavHostController,
    dictionaryId: String
) {
    val mainViewModel = hiltViewModel<MainViewModel>()

    val learningViewModel = hiltViewModel<LearningViewModel>()

    val words by mainViewModel.words.collectAsState()

    val shuffledWords by remember(words) {
        mutableStateOf(words?.shuffled())
    }

    LaunchedEffect(Unit) {
        mainViewModel.getWords(dictionaryId)
    }

    val coroutineScope = rememberCoroutineScope()

    shuffledWords?.let { _shuffledWords ->

        val pagerState = rememberPagerState(pageCount = {
            _shuffledWords.size
        })


        LaunchedEffect(pagerState.currentPage) {
            learningViewModel.getTranslationOptions(
                _shuffledWords,
                _shuffledWords[pagerState.currentPage]
            )
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            PageCouner(
                modifier = Modifier.padding(vertical = 10.dp),
                currentPage = pagerState.currentPage + 1,
                size = _shuffledWords.size
            )
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                HorizontalPager(state = pagerState, userScrollEnabled = false) { _ ->
                    Log.w("mytag", "page: ${pagerState.currentPage}")

                    val isLastPage = (pagerState.currentPage == _shuffledWords.size - 1)

                    WordSelectionTestItem(
                        presentation = _shuffledWords[pagerState.currentPage],
                        options = learningViewModel.options.value,
                        onNextClick = { wasAnswerCorrect ->
                            if (wasAnswerCorrect) {
                                learningViewModel.currentScore.value++
                            }

                            if (isLastPage) {
                                navController.navigate(
                                    "${Routes.LearningScreen.LearningResultsScreen}/${learningViewModel.currentScore.value}/${_shuffledWords.size}"
                                ) {
                                    popUpTo(Routes.LearningScreen.Root) {
                                        inclusive = false
                                    }
                                }
                            } else {
                                coroutineScope.launch {
                                    pagerState.animateScrollToPage(pagerState.currentPage + 1)
                                }
                            }
                        },
                        isLastPage = isLastPage
                    )
                }
            }
        }
    }
}

@Composable
fun WordSelectionTestItem(
    presentation: WordPresentation,
    options: List<String>,
    onNextClick: (wasAnswerCorrect: Boolean) -> Unit,
    isLastPage: Boolean
) {
    var selectedOption by remember { mutableStateOf<String?>(null) }

    var wrongAnswer by remember {
        mutableStateOf<String?>(null)
    }

    var correctAnswer by remember {
        mutableStateOf<String?>(null)
    }

    var showNextButton by remember {
        mutableStateOf(false)
    }

    var wasAnswerCorrect by remember {
        mutableStateOf(false)
    }

    var isSelectionEnabled by remember {
        mutableStateOf(true)
    }

    Column(
        Modifier.verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val imageBitmap = presentation.image.takeIf { it.isNotBlank() }?.let {
            ImageUtils.getBitmapFromBase64(presentation.image)?.asImageBitmap()
        }
        if (imageBitmap != null) {
            Image(
                modifier = Modifier
                    .height(200.dp)
                    .wrapContentWidth()
                    .align(alignment = Alignment.CenterHorizontally),
                bitmap = imageBitmap,
                contentDescription = null
            )

        } else {
            Image(
                modifier = Modifier
                    .padding(10.dp)
                    .height(200.dp)
                    .wrapContentWidth()
                    .align(alignment = Alignment.CenterHorizontally),
                painter = painterResource(id = R.drawable.dictionary_placeholder),
                contentDescription = null
            )
        }


        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            options.forEach { text ->
                val isSelected = (text == selectedOption)
                val isCorrect = (text == correctAnswer)
                val isWrong = (text == wrongAnswer)

                SelectableOption(text = text,
                    isSelected = isSelected,
                    isEnabled = isSelectionEnabled,
                    correct = isCorrect,
                    wrong = isWrong,
                    onOptionSelected = {
                        selectedOption = it
                    })
            }
        }

        if (selectedOption != null) {
            Button(onClick = {
                if (selectedOption == presentation.translation) {
                    wasAnswerCorrect = true
                    correctAnswer = presentation.translation
                } else {
                    wrongAnswer = selectedOption
                    correctAnswer = presentation.translation
                }
                isSelectionEnabled = false
                showNextButton = true
                selectedOption = null
            }) {
                Text(text = "Answer")
            }
        }

        if (showNextButton) {
            Button(onClick = {
                onNextClick.invoke(wasAnswerCorrect)
            }) {
                Text(text = if (isLastPage) "Finish" else "Next word")
            }
        }
    }
}


@Composable
fun SelectableOption(
    modifier: Modifier = Modifier,
    text: String,
    isEnabled: Boolean,
    isSelected: Boolean,
    correct: Boolean = false,
    wrong: Boolean = false,
    onOptionSelected: (String) -> Unit
) {

    Card(
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp)
            .clip(RoundedCornerShape(8.dp))
            .selectable(
                selected = (isSelected),
                enabled = isEnabled,
                onClick = {
                    onOptionSelected.invoke(text)
                }
            ),
        border = if (isSelected) {
            BorderStroke(1.dp, Color.Black)
        } else if (correct) {
            BorderStroke(1.dp, Color.Green)
        } else if (wrong) {
            BorderStroke(1.dp, Color.Red)
        } else {
            null
        }
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(all = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier
                    .padding(start = 10.dp)
                    .weight(1f)
            )

            if (isSelected) {
                Icon(
                    modifier = Modifier.size(32.dp),
                    painter = rememberVectorPainter(image = Icons.Default.KeyboardArrowLeft),
                    contentDescription = null
                )
            } else if (correct) {
                Icon(
                    modifier = Modifier.size(32.dp),
                    painter = rememberVectorPainter(image = Icons.Default.Check),
                    contentDescription = null,
                    tint = Color.Green
                )
            } else if (wrong) {
                Icon(
                    modifier = Modifier.size(32.dp),
                    painter = rememberVectorPainter(image = Icons.Default.Close),
                    contentDescription = null,
                    tint = Color.Red
                )
            }
        }
    }
}

@Composable
fun PageCouner(
    modifier: Modifier = Modifier,
    currentPage: Int,
    size: Int,
) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        AnimatedContent(
            targetState = currentPage,
            transitionSpec = {
                if (targetState > initialState) {
                    slideInVertically { -it } togetherWith slideOutVertically { it }
                } else {
                    slideInVertically { it } togetherWith slideOutVertically { -it }
                }
            }, label = ""
        ) { count ->
            Text(
                text = "$count",
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center,
            )
        }
        Text(
            modifier = Modifier.padding(horizontal = 10.dp),
            text = "of",
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center,
        )

        Text(
            text = "$size",
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center,
        )
    }

}

@Preview
@Composable
fun PageCounterPreview() {
    PageCouner(currentPage = 1, size = 10)
}

@Preview
@Composable
fun WordSelectionTestItemPreview() {
    WordSelectionTestItem(
        presentation = WordPresentation(
            id = "1",
            name = "test",
            image = "",
            translation = "test2",
            language = "en"
        ),
        options = listOf("test1", "test2", "test3", "test4"),
        onNextClick = {},
        isLastPage = false
    )
}