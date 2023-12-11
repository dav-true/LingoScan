package com.lingoscan.compose.components.common

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.lingoscan.presentations.DictionaryPresentation


@Composable fun SelectDictionaryDialog(
    dictionaries: List<DictionaryPresentation>,
    onDismissRequest: () -> Unit,
    onSelectDictionary: (DictionaryPresentation) -> Unit,
    onCreateDictionary: () -> Unit
) {

    Dialog(onDismissRequest = onDismissRequest) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 300.dp, max = 450.dp)
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
            ) {
                Text(
                    text = "Select dictionary",
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color.Black
                )
                Column(
                    modifier = Modifier
                        .verticalScroll(rememberScrollState())
                        .weight(1f)
                ) {
                    if (dictionaries.isEmpty()) {
                        Text(
                            modifier = Modifier.padding(top = 10.dp, end = 10.dp),
                            text = "You have no dictionaries yet. Create one!",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Black
                        )
                    } else {
                        dictionaries.forEach {
                            SelectDictionaryItem(
                                dictionary = it,
                                onDictionarySelected = onSelectDictionary,
                                onDismissRequest = onDismissRequest
                            )
                        }
                    }

                }

                Button(
                    modifier = Modifier
                        .align(Alignment.End),
                    onClick = onCreateDictionary
                ) {
                    Text(text = "Create new dictionary")
                }
            }
        }
    }
}


@Composable fun SelectDictionaryItem(
    dictionary: DictionaryPresentation,
    onDictionarySelected: (DictionaryPresentation) -> Unit,
    onDismissRequest: () -> Unit
) {
    Text(modifier = Modifier
        .fillMaxWidth()
        .clickable {
            onDictionarySelected.invoke(dictionary)
            onDismissRequest.invoke()
        }
        .padding(all = 10.dp),
        text = dictionary.name,
        style = MaterialTheme.typography.headlineSmall,
        color = Color.Black)
    Divider(thickness = 1.dp, color = Color.Gray)
}
