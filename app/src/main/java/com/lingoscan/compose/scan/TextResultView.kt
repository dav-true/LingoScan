package com.lingoscan.compose.scan

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun ResultViewItem(
    resultText: String,
    translatedText: String,
    buttonText: String,
    onButtonClick: (() -> Unit)? = null
) {
    resultText.takeIf { it.isNotBlank() }?.let {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Card(
                modifier = Modifier
                    .wrapContentHeight()
                    .fillMaxWidth()
                    .padding(20.dp),
                shape = RoundedCornerShape(8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {

                    Text(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .wrapContentSize()
                            .background(Color.Black)
                            .padding(8.dp),
                        text = translatedText,
                        color = Color.White,
                        fontSize = 22.sp
                    )

                    Text(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .wrapContentSize()
                            .padding(8.dp),
                        text = resultText,
                        color = Color.Black,
                        fontSize = 18.sp
                    )
                }
            }

            onButtonClick?.let {
                Button(
                    modifier = Modifier.padding(bottom = 10.dp),
                    onClick = { onButtonClick.invoke() }
                ) {
                    Text(text = buttonText, style = MaterialTheme.typography.bodyLarge)
                }
            }
        }
    }
}