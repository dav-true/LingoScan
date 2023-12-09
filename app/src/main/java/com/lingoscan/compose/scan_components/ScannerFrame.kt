package com.lingoscan.compose.scan_components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview


@Composable
fun ScannerFrame(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier, onDraw = {

        val strokeWidth = 4f
        val color = Color.White
        // top left
        drawLine(
            start = Offset(x = 0f, y = 0f),
            end = Offset(x = size.width /10 , y = 0f),
            color = color,
            strokeWidth = strokeWidth
        )

        drawLine(
            start = Offset(x = 0f, y = 0f),
            end = Offset(x = 0f, y = size.height / 10),
            color = color,
            strokeWidth = strokeWidth
        )

        // top right
        drawLine(
            start = Offset(x = size.width - size.width / 10, y = 0f),
            end = Offset(x = size.width , y = 0f),
            color = color,
            strokeWidth = strokeWidth
        )

        drawLine(
            start = Offset(x = size.width, y = 0f),
            end = Offset(x = size.width, y = size.height / 10),
            color = color,
            strokeWidth = strokeWidth
        )

        // bottom left
        drawLine(
            start = Offset(x = 0f, y = size.height - size.height /10),
            end = Offset(x = 0f , y = size.height),
            color = color,
            strokeWidth = strokeWidth
        )

        drawLine(
            start = Offset(x = 0f, y = size.height),
            end = Offset(x = size.width / 10, y = size.height),
            color = color,
            strokeWidth = strokeWidth
        )

        // bottom right
        drawLine(
            start = Offset(x = size.width - size.width / 10, y = size.height),
            end = Offset(x = size.width , y = size.height),
            color = color,
            strokeWidth = strokeWidth
        )

        drawLine(
            start = Offset(x = size.width, y = size.height - size.height / 10),
            end = Offset(x = size.width, y = size.height),
            color = color,
            strokeWidth = strokeWidth
        )

        // cross

        drawLine(
            start = Offset(x = size.width / 2 - size.width / 10, y = size.height / 2),
            end = Offset(x = size.width / 2 + size.width / 10, y = size.height / 2),
            color = color,
            strokeWidth = strokeWidth
        )

        drawLine(
            start = Offset(x = size.width / 2, y = size.height / 2 - size.height / 10),
            end = Offset(x = size.width / 2, y = size.height / 2 + size.height / 10),
            color = color,
            strokeWidth = strokeWidth
        )
    })
}

@Preview
@Composable
fun ScannerFramePreview() {
    ScannerFrame(
        modifier = Modifier
            .fillMaxWidth(0.8f)
            .fillMaxHeight(0.5f)
    )
}