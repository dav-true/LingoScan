package com.lingoscan.compose.components.common

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.dp
import com.lingoscan.utils.translate.LanguageModel

@Composable
fun SelectLanguageButton(
    language: String,
    onClick: () -> Unit
) {
    Row(modifier = Modifier
        .border(
            1.dp, color = Color.Black, shape = RoundedCornerShape(4.dp)
        )
        .clip(RoundedCornerShape(4.dp))
        .clickable {
            // Show dialog with all languages
            onClick.invoke()
        }
        .padding(6.dp)) {

        Text(text = language, style = MaterialTheme.typography.bodyLarge)
        Icon(
            painter = rememberVectorPainter(image = Icons.Default.KeyboardArrowDown),
            contentDescription = null
        )
    }
}