package com.lingoscan.compose.components.library

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.lingoscan.R
import com.lingoscan.presentations.WordPresentation
import com.lingoscan.utils.scan.ImageUtils

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun WordItem(
    modifier: Modifier = Modifier,
    presentation: WordPresentation,
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {

    Card(
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(1.dp, Color.Black),
        modifier = modifier
            .padding(10.dp)
            .clip(RoundedCornerShape(10.dp))
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            )
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
        ) {

            val imageBitmap = presentation.image.takeIf { it.isNotBlank() }?.let {
                ImageUtils.getBitmapFromBase64(presentation.image)?.asImageBitmap()
            }
            if (imageBitmap != null) {
                Image(
                    modifier = Modifier
                        .height(150.dp)
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
            Text(
                modifier = Modifier.padding(10.dp),
                text = "en: ${presentation.name}",
                style = MaterialTheme.typography.headlineSmall
            )
            Text(
                modifier = Modifier.padding(bottom = 10.dp, start = 10.dp, end = 10.dp),
                text = "${presentation.language}: ${presentation.translation}",
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Composable
@Preview
fun WordItemPreview() {
    WordItem(
        modifier = Modifier.width(200.dp),
        presentation = WordPresentation(
            id = "",
            name = "Hello",
            translation = "Привіт",
            language = "UA",
            image = ""
        ),
        onClick = {},
        onLongClick = {}
    )
}