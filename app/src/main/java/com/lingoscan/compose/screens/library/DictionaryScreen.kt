package com.lingoscan.compose.screens.library

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.lingoscan.compose.components.library.WordItem
import com.lingoscan.viewmodels.MainViewModel

@Composable
fun DictionaryScreen(
    navController: NavHostController,
    dictionaryId: String
) {

    val viewModel = hiltViewModel<MainViewModel>()

    val words by viewModel.words.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.getWords(dictionaryId)
    }


    Box(
        modifier = Modifier
            .background(Color.White)
            .fillMaxSize()
    ) {

        words?.let {
            Column(modifier = Modifier.fillMaxSize()) {
                LazyVerticalStaggeredGrid(columns = StaggeredGridCells.Fixed(2)) {
                    items(items = it) { wordPresentation ->
                        WordItem(
                            presentation = wordPresentation,
                            onClick = { },
                            onLongClick = {})
                    }
                }
            }
        }

        if (words.isNullOrEmpty()) {
            Text(
                text = "You have no words in this dictionary yet",
                style = MaterialTheme.typography.headlineSmall,
                color = Color.Black,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 20.dp)
            )
        }
    }
}