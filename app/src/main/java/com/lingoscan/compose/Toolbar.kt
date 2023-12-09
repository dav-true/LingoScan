package com.lingoscan.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp


//@Composable fun Toolbar(
//    modifier: Modifier = Modifier,
//    title: String? = null,
//    showActionButton: Boolean = false,
//    showActionTextButton: Boolean = false,
//    actionButtonText: String? = null,
//    showBackButton: Boolean = false,
//    backgroundColor: Color = Color.Unspecified,
//    textColor: Color = Color.Black,
//    onBackPress: () -> Unit = {},
//    onActionButton: (() -> Unit) = {},
//) {
//    Row(
//        modifier = modifier.fillMaxWidth().background(backgroundColor),
//        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
//    ) {
//        if (showBackButton) {
//            IconButton(onClick = onBackPress) {
//                Icon(
//                    modifier = Modifier
//                        .background(
//                            color = MaterialTheme.colorScheme.primary, shape = CircleShape
//                        )
//                        .padding(5.dp),
//                    painter = rememberVectorPainter(image = Icons.Default.ArrowBack),
//                    tint = Color.White,
//                    contentDescription = "BackButton"
//                )
//            }
//        }
//
//        if (title != null) {
//            Text(
//                modifier = Modifier.weight(1f),
//                text = title,
//                style = MaterialTheme.typography.titleMedium,
//                color = textColor
//            )
//        } else {
//            Spacer(modifier = Modifier.weight(1f))
//
//        }
//        if (showActionTextButton) {
//            Text(
//                modifier = Modifier.padding(end = 16.dp),
//                text = actionButtonText ?: "",
//                style = MaterialTheme.typography.titleMedium,
//                color = textColor
//            )
//        }
//
//        if (showActionButton) {
//            IconButton(onClick = onActionButton) {
//                Icon(
//                    modifier = Modifier
//                        .background(
//                            color = MaterialTheme.colorScheme.primary, shape = CircleShape
//                        )
//                        .padding(5.dp),
//                    painter = rememberVectorPainter(image = Icons.Default.MoreVert),
//                    tint = Color.White,
//                    contentDescription = "ActionButton"
//                )
//            }
//        }
//    }
//}
//
//@Preview @Composable fun ToolbarPreview() {
//    Toolbar(
//        title = "Toolbar",
//        showActionButton = true,
//        showActionTextButton = true,
//        actionButtonText = "Action",
//        showBackButton = true
//    )
//}