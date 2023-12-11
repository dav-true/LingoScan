package com.lingoscan.compose.components.library

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import com.lingoscan.R
import com.lingoscan.presentations.DictionaryPresentation

@Composable
@OptIn(ExperimentalFoundationApi::class)
fun DictionaryItem(
    modifier: Modifier = Modifier,
    presentation: DictionaryPresentation,
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {

    Card(
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        border = BorderStroke(1.dp, Color.Black),
        shape = RoundedCornerShape(10.dp),
        modifier = modifier
            .padding(10.dp)
            .clip(RoundedCornerShape(10.dp))
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            )

    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            if (presentation.image.isNotBlank()) {
                AsyncImage(
                    modifier = Modifier
                        .height(130.dp)
                        .wrapContentWidth()
                        .align(alignment = androidx.compose.ui.Alignment.CenterHorizontally),
                    model = rememberAsyncImagePainter(model = presentation.image),
                    contentDescription = null
                )
            } else {
                Image(
                    modifier = Modifier
                        .padding(10.dp)
                        .height(130.dp)
                        .wrapContentWidth()
                        .align(alignment = Alignment.CenterHorizontally),
                    painter = painterResource(id = R.drawable.dictionary_placeholder),
                    contentDescription = null
                )
            }
            Text(
                modifier = Modifier.padding(10.dp),
                text = presentation.name,
                style = MaterialTheme.typography.headlineSmall
            )
            Text(
                modifier = Modifier.padding(10.dp),
                text = "Language: ${presentation.language}",
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }

}