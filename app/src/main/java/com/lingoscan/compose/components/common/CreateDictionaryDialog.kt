package com.lingoscan.compose.components.common

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

@Composable
fun CreateDictionaryDialog(
    onDismissRequest: () -> Unit,
    onCreateDictionary: (String) -> Unit
) {

    var textFieldValue by remember {
        mutableStateOf("")
    }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = {
            Text(text = "Create dictionary")
        },
        text = {
            TextField(
                value = textFieldValue,
                onValueChange = {
                    textFieldValue = it
                },
                label = {
                    Text(text = "Dictionary name")
                }
            )
        },
        confirmButton = {
            Button(
                onClick = {
                    onCreateDictionary(textFieldValue)
                    onDismissRequest()
                }
            ) {
                Text(text = "Create")
            }
        },
        dismissButton = {
            Button(
                onClick = onDismissRequest
            ) {
                Text(text = "Cancel")
            }
        }
    )
}