package com.lingoscan.compose.screens.library

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import com.lingoscan.R
import com.lingoscan.compose.components.library.DictionaryItem
import com.lingoscan.compose.navigation.Routes
import com.lingoscan.presentations.DictionaryPresentation
import com.lingoscan.viewmodels.MainViewModel


@Composable
fun LibraryScreen(
    navController: NavHostController
) {
    val viewModel = hiltViewModel<MainViewModel>()

    val data by viewModel.dictionaries.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.getDictionaries()
    }

    data?.let {
        LazyVerticalStaggeredGrid(columns = StaggeredGridCells.Fixed(2)) {
            items(items = it) {
                DictionaryItem(presentation = it,
                    onClick = {
                        navController.navigate(route = "${Routes.LibraryScreen.Dictionary}/${it.id}")
                    }, onLongClick = {

                    })
            }
        }
    }
}


