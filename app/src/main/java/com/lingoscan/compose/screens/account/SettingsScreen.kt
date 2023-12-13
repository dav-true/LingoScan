package com.lingoscan.compose.screens.account

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.lingoscan.compose.navigation.AuthNavigationRoutes
import com.lingoscan.compose.navigation.Routes
import com.lingoscan.viewmodels.MainViewModel

@Composable
fun SettingsScreen(
    navController: NavController,
    authNavController: NavHostController
) {
    val mainViewModel = hiltViewModel<MainViewModel>()

    Column {
        ListTextItem(modifier = Modifier.clickable {
            navController.navigate(Routes.Settings.DevSettings)
        }, text = "Dev settings")
        ListTextItem(
            modifier = Modifier.clickable { navController.navigate(Routes.Settings.LanguageSettings) },
            text = "Language settings"
        )

        ListTextItem(modifier = Modifier.clickable {
            mainViewModel.logoutUser()
            authNavController.navigate(AuthNavigationRoutes.AuthScreen) {
                popUpTo(AuthNavigationRoutes.MainScreen) {
                    inclusive = true
                }
            }
        }, text = "Logout", color = Color.Red, showDivider = false, icon = Icons.Default.ExitToApp)
    }
}


@Composable
fun ListTextItem(
    modifier: Modifier = Modifier,
    text: String,
    showDivider: Boolean = true,
    color: Color = Color.Black,
    icon: ImageVector? = null
) {
    Column(modifier = modifier) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = Modifier,
                text = text,
                style = MaterialTheme.typography.bodyLarge,
                color = color
            )

            if (icon != null) {
                Icon(painter = rememberVectorPainter(image = icon), contentDescription = null)
            }

        }
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