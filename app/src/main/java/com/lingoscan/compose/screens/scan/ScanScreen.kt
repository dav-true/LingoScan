@file:OptIn(ExperimentalPermissionsApi::class)

package com.lingoscan.compose.screens.scan

import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.lingoscan.compose.navigation.Routes
import com.lingoscan.utils.scan.ImageUtils
import com.lingoscan.ui.theme.Pink80
import com.lingoscan.ui.theme.PinkRed80
import com.lingoscan.viewmodels.MainViewModel
import java.io.File
import java.net.URLEncoder
import java.nio.charset.StandardCharsets


@Composable
fun ScanScreen(
    navController: NavHostController
) {
    val mainViewModel = hiltViewModel<MainViewModel>()
    val cameraPermissionState = rememberPermissionState(android.Manifest.permission.CAMERA)
    var uploadedImageUri by remember { mutableStateOf<Uri?>(null) }

    val context = LocalContext.current

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
             ImageUtils.getBitmap(context, uri)?.let { bitmap ->
                 val bitmapCopyUri = ImageUtils.writeBitmapToFile(bitmap, context.filesDir)
                 uploadedImageUri = bitmapCopyUri
             }
        }
    }

    LaunchedEffect(Unit) {
        if (!cameraPermissionState.status.isGranted) {
            cameraPermissionState.launchPermissionRequest()
        }
    }

    LaunchedEffect(uploadedImageUri){
        uploadedImageUri?.let {
            val imageUri = URLEncoder.encode(it.toString(), StandardCharsets.UTF_8.toString())
            navController.navigate("${Routes.ScanScreen.UploadedImageScreen}/${imageUri}")
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Card(
            modifier = Modifier
                .padding(10.dp)
                .fillMaxWidth()
                .weight(1f)
                .clickable {
                    if (!cameraPermissionState.status.isGranted) {
                        cameraPermissionState.launchPermissionRequest()
                        return@clickable
                    }
                    navController.navigate(Routes.ScanScreen.CameraView)
                },
            shape = RoundedCornerShape(10.dp),
            colors = CardDefaults.cardColors(containerColor = Pink80)

        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "Open Camera View", style = MaterialTheme.typography.headlineLarge)
            }
        }

        Card(
            modifier = Modifier
                .padding(10.dp)
                .fillMaxWidth()
                .weight(1f)
                .clickable {
                    launcher.launch("image/*")
                },
            shape = RoundedCornerShape(10.dp),
            colors = CardDefaults.cardColors(containerColor = PinkRed80)

        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "Upload Image", style = MaterialTheme.typography.headlineLarge)
            }
        }
    }
}

@Composable
@Preview
fun ScanScreenPreview() {
    ScanScreen(navController = NavHostController(LocalContext.current))
}