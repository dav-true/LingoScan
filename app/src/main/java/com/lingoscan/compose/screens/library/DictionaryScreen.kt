package com.lingoscan.compose.screens.library

import android.widget.Toast
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.lingoscan.compose.components.common.ActionsBottomSheet
import com.lingoscan.compose.components.common.DictionaryBottomSheetActions
import com.lingoscan.compose.components.common.RemoveConfirmationDialog
import com.lingoscan.compose.components.common.SelectDictionaryDialog
import com.lingoscan.compose.components.common.TextFieldDialog
import com.lingoscan.compose.components.common.WordsBottomSheetActions
import com.lingoscan.compose.components.library.WordItem
import com.lingoscan.compose.navigation.Routes
import com.lingoscan.presentations.WordPresentation
import com.lingoscan.viewmodels.MainViewModel

@Composable
fun DictionaryScreen(
    navController: NavHostController,
    dictionaryId: String
) {

    val viewModel = hiltViewModel<MainViewModel>()

    val words by viewModel.words.collectAsState()

    val dictionaries by viewModel.dictionaries.collectAsState()

    var selectedWord by remember {
        mutableStateOf<WordPresentation?>(null)
    }

    var showRemoveConfirmationDialog by remember {
        mutableStateOf(false)
    }

    var showMoveToDictionaryDialog by remember {
        mutableStateOf(false)
    }

    var showCreateDictionaryDialog by remember {
        mutableStateOf(false)
    }

    val context = LocalContext.current
    LaunchedEffect(Unit) {
        viewModel.getWords(dictionaryId)
        viewModel.getDictionaries()
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
                            onClick = {
                                navController.navigate("${Routes.LibraryScreen.Word}/${wordPresentation.id}")
                            },
                            onLongClick = {
                                selectedWord = wordPresentation
                            })
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

        if (selectedWord != null) {
            ActionsBottomSheet(onDismissSheet = { selectedWord = null }, content = {
                WordsBottomSheetActions(onRemove = {
                    showRemoveConfirmationDialog = true
                }, onMove = {
                    showMoveToDictionaryDialog = true
                })
            })
        }

        if (showRemoveConfirmationDialog) {
            RemoveConfirmationDialog(
                title = "Remove ${selectedWord?.name.orEmpty()} word",
                subtitle = "Are you sure you want to remove this dictionary?",
                confirmButtonText = "Remove",
                onDismissRequest = { showRemoveConfirmationDialog = false },
                onConfirm = {
                    selectedWord?.let {
                        viewModel.removeWord(dictionaryId = dictionaryId, wordId = it.id)
                        showRemoveConfirmationDialog = false
                        selectedWord = null
                        Toast.makeText(context, "Word ${it.name} removed", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            )
        }

        if (showMoveToDictionaryDialog) {
            SelectDictionaryDialog(
                dictionaries = dictionaries?.filter { it.id != dictionaryId }.orEmpty(),
                onDismissRequest = { showMoveToDictionaryDialog = false },
                onSelectDictionary = {
                    viewModel.moveWord(
                        sourceDictionaryId = dictionaryId,
                        targetDictionaryId = it.id,
                        wordId = selectedWord?.id.orEmpty()
                    )
                    showMoveToDictionaryDialog = false
                    selectedWord = null
                },
                onCreateDictionary = {
                    showMoveToDictionaryDialog = false
                    showCreateDictionaryDialog = true
                }
            )
        }

        if (showCreateDictionaryDialog) {
            TextFieldDialog(
                title = "Create new dictionary",
                textFieldPlaceholder = "Enter dictionary name",
                confirmButtonText = "Create",
                onDismissRequest = { showCreateDictionaryDialog = false },
                onConfirm = {
                    viewModel.createDictionary(it)
                    showCreateDictionaryDialog = false
                    showMoveToDictionaryDialog = true
                }
            )
        }
    }
}