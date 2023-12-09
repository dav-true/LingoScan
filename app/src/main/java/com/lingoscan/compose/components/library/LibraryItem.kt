package com.lingoscan.compose.components.library

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import com.lingoscan.viewmodels.ComposableViewModel

@Composable
fun LibraryItem(
    modifier: Modifier = Modifier,
    presentation: LibraryItemPresentation,
) {
    val composableViewModel = hiltViewModel<ComposableViewModel>()

    var resultTranslatedText by remember {
        mutableStateOf("")
    }

    LaunchedEffect(Unit) {
        composableViewModel.translatorHelper.translate(presentation.text, onSuccess = {
            resultTranslatedText = it
        })
    }

    Card {
        Column {
            AsyncImage(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                model = rememberAsyncImagePainter(model = presentation.imageUrl),
                contentDescription = null
            )

            Text(text = presentation.text, style = MaterialTheme.typography.bodyMedium)
            Text(text = presentation.description, style = MaterialTheme.typography.bodyMedium)
        }
    }
}


data class LibraryItemPresentation(
    val imageUrl: String,
    val text: String,
    val description: String
) {

}
