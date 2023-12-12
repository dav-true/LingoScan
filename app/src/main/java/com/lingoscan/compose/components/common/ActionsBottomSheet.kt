package com.lingoscan.compose.components.common

import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActionsBottomSheet(
    onDismissSheet: () -> Unit,
    content: @Composable () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismissSheet
    ) {
        content.invoke()
    }
}


@Composable
fun DictionaryBottomSheetActions(
    onRename: () -> Unit,
    onRemove: () -> Unit
) {
    ListItem(
        modifier = Modifier.clickable {
            onRename()
        },
        headlineContent = { Text("Rename") },
        leadingContent = { Icon(Icons.Default.Create, null) }
    )
    ListItem(
        modifier = Modifier.clickable {
            onRemove()
        },
        headlineContent = { Text("Remove") },
        leadingContent = { Icon(Icons.Default.Delete, null) }
    )
}

@Composable
fun WordsBottomSheetActions(
    onMove: () -> Unit,
    onRemove: () -> Unit
) {
    ListItem(
        modifier = Modifier.clickable {
            onMove()
        },
        headlineContent = { Text("Move") },
        leadingContent = { Icon(Icons.Default.ArrowForward, null) }
    )
    ListItem(
        modifier = Modifier.clickable {
            onRemove()
        },
        headlineContent = { Text("Remove") },
        leadingContent = { Icon(Icons.Default.Delete, null) }
    )
}