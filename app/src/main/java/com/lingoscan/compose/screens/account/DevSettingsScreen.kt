package com.lingoscan.compose.screens.account

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.lingoscan.viewmodels.MainViewModel

@Composable fun DevSettingsScreen(
    navController: NavController
) {

    val mainViewModel = hiltViewModel<MainViewModel>()

    var textFieldValue by remember {
        mutableStateOf("")
    }

    Column {
        Button(onClick = {
            mainViewModel.createDictionary(textFieldValue)
        }) {
            Text(text = "add dictionary")
        }

        TextField(value = textFieldValue, onValueChange = {
            textFieldValue = it
        } )


        Button(onClick = { mainViewModel.deleteAllWordsFromDicitionary() }) {
            Text(text = "Delete all words")
        }


        Button(onClick = { mainViewModel.deleteAllDictionaries() }) {
            Text(text = "Delete all dicts")
        }
    }

}
