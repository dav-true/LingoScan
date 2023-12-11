package com.lingoscan.compose.screens.library

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.lingoscan.compose.components.common.ActionsBottomSheet
import com.lingoscan.compose.components.common.DictionaryBottomSheetActions
import com.lingoscan.compose.components.common.RemoveConfirmationDialog
import com.lingoscan.compose.components.library.DictionaryItem
import com.lingoscan.compose.navigation.Routes
import com.lingoscan.presentations.DictionaryPresentation
import com.lingoscan.viewmodels.MainViewModel


@Composable
fun LibraryScreen(
    navController: NavHostController
) {
    val viewModel = hiltViewModel<MainViewModel>()

    val dictionaries by viewModel.dictionaries.collectAsState()

    var selectedDictionary by remember {
        mutableStateOf<DictionaryPresentation?>(null)
    }

    var removeConfirmationDialog by remember {
        mutableStateOf(false)
    }

    var renameDialog by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(Unit) {
        viewModel.getDictionaries()
    }

    Box(
        Modifier
            .background(Color.White)
            .fillMaxSize()
    ) {
        dictionaries?.let {
            LazyVerticalStaggeredGrid(columns = StaggeredGridCells.Fixed(2)) {
                items(items = it) {
                    DictionaryItem(presentation = it,
                        onClick = {
                            navController.navigate(route = "${Routes.LibraryScreen.Dictionary}/${it.id}")
                        }, onLongClick = {
                            selectedDictionary = it
                        })
                }
            }
        }

        if (dictionaries.isNullOrEmpty()) {
            Text(
                text = "You have no dictionaries yet. Create one!",
                style = MaterialTheme.typography.headlineSmall,
                color = Color.Black,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 20.dp)
            )
        }

        if (selectedDictionary != null) {
            ActionsBottomSheet(onDismissSheet = { selectedDictionary = null }, content = {
                DictionaryBottomSheetActions(
                    onRename = { }, onRemove = {
                        removeConfirmationDialog = true
                    })
            })
        }

        if (removeConfirmationDialog) {
            RemoveConfirmationDialog(
                title = "Remove ${selectedDictionary?.name.orEmpty()} dictionary",
                subtitle = "Are you sure you want to remove this dictionary?",
                confirmButtonText = "Remove",
                onDismissRequest = { removeConfirmationDialog = false },
                onConfirm = {
                    selectedDictionary?.let {
                        viewModel.deleteDictionary(it.id)
                        removeConfirmationDialog = false
                        selectedDictionary = null
                    }
                }
            )
        }
    }
}


