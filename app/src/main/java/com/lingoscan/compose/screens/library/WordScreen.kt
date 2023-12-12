package com.lingoscan.compose.screens.library

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.lingoscan.R
import com.lingoscan.ui.theme.TextFieldBackground
import com.lingoscan.ui.theme.TextFieldIndicator
import com.lingoscan.utils.scan.ImageUtils
import com.lingoscan.viewmodels.MainViewModel
import java.util.Locale

@Composable fun WordScreen(
    navController: NavHostController, wordId: String
) {

    val mainViewModel = hiltViewModel<MainViewModel>()

    val keyboardController = LocalSoftwareKeyboardController.current

    var isEditMode by remember {
        mutableStateOf(false)
    }

    var editButtonText by remember {
        mutableStateOf("Edit")
    }

    val wordPresentation by mainViewModel.selectedWord.collectAsState()

    LaunchedEffect(Unit) {
        mainViewModel.getWordById(wordId)
    }

    LaunchedEffect(isEditMode) {
        editButtonText = if (isEditMode) "Cancel" else "Edit"
    }

    wordPresentation?.let { wordPresentation ->
        var wordText by remember {
            mutableStateOf(wordPresentation.name)
        }

        var translationText by remember {
            mutableStateOf(wordPresentation.translation)
        }

        var language by remember {
            mutableStateOf(wordPresentation.language)
        }

        Column {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                shape = RoundedCornerShape(10.dp),
                border = BorderStroke(1.dp, Color.Black),
                modifier = Modifier
                    .padding(10.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                ) {

                    val imageBitmap = wordPresentation.image.takeIf { it.isNotBlank() }?.let {
                        ImageUtils.getBitmapFromBase64(wordPresentation.image)?.asImageBitmap()
                    }
                    if (imageBitmap != null) {
                        Image(
                            modifier = Modifier
                                .height(300.dp)
                                .wrapContentWidth()
                                .align(alignment = Alignment.CenterHorizontally),
                            bitmap = imageBitmap,
                            contentDescription = null
                        )

                    } else {
                        Image(
                            modifier = Modifier
                                .padding(10.dp)
                                .height(150.dp)
                                .wrapContentWidth()
                                .align(alignment = Alignment.CenterHorizontally),
                            painter = painterResource(id = R.drawable.dictionary_placeholder),
                            contentDescription = null
                        )
                    }

                    LingoTextField(isEditMode = isEditMode,
                        keyboardController = keyboardController,
                        text = wordText,
                        prefix = "EN: ",
                        onValueChange = { wordText = it })

                    LingoTextField(isEditMode = isEditMode,
                        keyboardController = keyboardController,
                        text = translationText,
                        prefix = "${language.uppercase(Locale.getDefault())}: ",
                        onValueChange = {
                            translationText = it
                        })
                }
            }

            Row {
                Button(modifier = Modifier
                    .weight(1f)
                    .padding(10.dp), onClick = {
                    isEditMode = !isEditMode
                }) {
                    Text(text = editButtonText)
                }

                AnimatedVisibility(
                    modifier = Modifier.padding(10.dp),
                    visible = isEditMode,
                    enter = slideInHorizontally(initialOffsetX = { w -> w }) + expandHorizontally(
                        expandFrom = Alignment.End
                    ),
                    exit = slideOutHorizontally(targetOffsetX = { w -> w }) + shrinkHorizontally(
                        shrinkTowards = Alignment.End
                    )
                ) {
                    Button(modifier = Modifier.weight(1f), onClick = {
                        isEditMode = false
                        mainViewModel.updateWord(wordText, translationText, wordId)
                    }) {
                        Text(text = "Save")
                    }
                }
            }
        }
    }
}


@Composable fun LingoTextField(
    isEditMode: Boolean,
    keyboardController: SoftwareKeyboardController?,
    text: String,
    prefix: String,
    onValueChange: (String) -> Unit
) {
    TextField(maxLines = 2,
        readOnly = !isEditMode,
        modifier = Modifier
            .padding(horizontal = 10.dp, vertical = if (isEditMode) 10.dp else 0.dp)
            .fillMaxWidth(),
        value = text,
        onValueChange = { newText ->
            onValueChange.invoke(newText)
        },
        prefix = {
            Text(text = prefix, style = MaterialTheme.typography.headlineSmall)
        },
        textStyle = MaterialTheme.typography.headlineSmall,
        colors = TextFieldDefaults.colors(
            unfocusedContainerColor = if (!isEditMode) Color.Transparent else TextFieldBackground,
            focusedContainerColor = if (!isEditMode) Color.Transparent else TextFieldBackground,
            focusedIndicatorColor = if (!isEditMode) Color.Transparent else TextFieldIndicator,
            unfocusedIndicatorColor = if (!isEditMode) Color.Transparent else TextFieldIndicator,
            disabledPrefixColor = Color.Black,
        ),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
        keyboardActions = KeyboardActions(onDone = {
            keyboardController?.hide()
        }))
}