package com.lingoscan.compose.scan_components

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
//import com.lingoscan.compose.Toolbar


@Composable
fun UploadedImageScreen(
    uploadedImageUri: Uri,
    uploadedImageResultText: String,
    uploadedImageTranslatedText: String,
    onBackPressed: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {

        Image(
            painter = rememberAsyncImagePainter(
                model = uploadedImageUri
            ),
            contentDescription = "Captured Image",
            modifier = Modifier
                .padding(30.dp)
                .border(
                    width = 2.dp, color = Color.White, shape = RoundedCornerShape(8.dp)
                )
        )
        ResultViewItem(
            resultText = uploadedImageResultText,
            translatedText = uploadedImageTranslatedText,
            buttonText = "Add to library",
            onButtonClick = {

            })
    }
}