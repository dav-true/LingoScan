package com.lingoscan.compose.screens.library

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
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


    words?.let {
        Column(modifier = Modifier.fillMaxSize()) {
            LazyVerticalStaggeredGrid(columns = StaggeredGridCells.Fixed(2)) {
                items(items = it) { wordPresentation ->
                    WordItem(
                        presentation = wordPresentation,
                        onClick = {  },
                        onLongClick = {})
                }
            }
        }
    }
}