package com.lingoscan.compose.screens.library

import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.runtime.Composable
import com.lingoscan.compose.components.library.LibraryItem


@Composable
fun LibraryScreen() {
    LazyVerticalGrid(columns = GridCells.Fixed(2)) {
        items(100) {
//            LibraryItem()
        }
    }
}



