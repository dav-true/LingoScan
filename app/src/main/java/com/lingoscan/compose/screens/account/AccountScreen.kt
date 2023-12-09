package com.lingoscan.compose.screens.account

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.lingoscan.compose.navigation.Routes

@Composable
fun AccountScreen(
    navController: NavController
) {
    Column {
        ListTextItem(text = "Account settings")
        ListTextItem(
            modifier = Modifier.clickable { navController.navigate(Routes.AccountScreen.LanguageSettings)},
            text = "Language settings"
        )
    }
}


@Composable
fun ListTextItem(
    modifier: Modifier = Modifier,
    text: String,
    showDivider: Boolean = true
) {
    Column(modifier = modifier) {
        Text(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 16.dp),
            text = text,
            style = MaterialTheme.typography.bodyLarge
        )
        if (showDivider) {
            Divider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp),
                thickness = 1.dp,
                color = Color.Gray
            )
        }
    }
}