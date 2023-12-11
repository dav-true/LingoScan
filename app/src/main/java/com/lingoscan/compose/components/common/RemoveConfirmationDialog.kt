package com.lingoscan.compose.components.common

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable


@Composable
fun RemoveConfirmationDialog(
    title: String,
    subtitle: String,
    confirmButtonText: String,
    onDismissRequest: () -> Unit,
    onConfirm: () -> Unit
) {
    //create a dialog for removing a dictionary
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = {
            Text(text = "Remove dictionary")
        },
        text = {
            Text(text = "Are you sure you want to remove this dictionary?")
        },
        confirmButton = {
            Button(
                onClick = {
                    onConfirm()
                    onDismissRequest()
                }
            ) {
                Text(text = "Remove")
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