package com.lingoscan.compose.components.common

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.lingoscan.utils.translate.LanguageModel


@Composable fun SelectLanguageDialog(
    languages: List<LanguageModel>,
    onLanguageSelected: (LanguageModel) -> Unit,
    onDismissRequest: () -> Unit
) {
    Dialog(onDismissRequest = onDismissRequest) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 300.dp, max = 600.dp)
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState())
            ) {
                languages.forEach {
                    LanguageSelectItem(
                        languageModel = it,
                        onLanguageSelected = onLanguageSelected,
                        onDismissRequest = onDismissRequest
                    )
                }
            }
        }
    }
}

@Composable fun LanguageSelectItem(
    languageModel: LanguageModel,
    onLanguageSelected: (LanguageModel) -> Unit,
    onDismissRequest: () -> Unit
) {
    Text(modifier = Modifier
        .fillMaxWidth()
        .clickable {
            onLanguageSelected.invoke(languageModel)
            onDismissRequest.invoke()
        }
        .padding(all = 10.dp),
        text = languageModel.language,
        style = MaterialTheme.typography.headlineSmall)
    Divider(
        thickness = 1.dp, color = Color.Gray, modifier = Modifier.padding(horizontal = 10.dp)
    )
}