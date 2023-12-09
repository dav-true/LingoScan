@file:OptIn(ExperimentalPermissionsApi::class)

package com.lingoscan.compose.screens.scan

import android.content.Intent
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
import androidx.navigation.NavHostController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.lingoscan.compose.navigation.Routes
import com.lingoscan.scan.utils.ImageUtils
import com.lingoscan.ui.theme.Pink80
import com.lingoscan.ui.theme.PinkRed80
import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.StandardCharsets


@Composable
fun ScanScreen(
    navController: NavHostController
) {

    val cameraPermissionState = rememberPermissionState(android.Manifest.permission.CAMERA)
    var uploadedImageUri by remember { mutableStateOf<Uri?>(null) }

    val context = LocalContext.current

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            uploadedImageUri = uri
            val bitmap = ImageUtils.getBitmap(context, uri)

            Log.w("mytag", bitmap.toString())
        }
    }

    LaunchedEffect(Unit) {
        if (!cameraPermissionState.status.isGranted) {
            cameraPermissionState.launchPermissionRequest()
        }
    }

    LaunchedEffect(uploadedImageUri){
//        uploadedImageUri?.let {
//            val imageUri = URLEncoder.encode(it.toString(), StandardCharsets.UTF_8.toString())
//            navController.navigate("${Routes.ScanScreen.UploadedImageScreen}/${imageUri}")
//        }
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

//@Composable @OptIn(ExperimentalPermissionsApi::class) fun ScanScreen(
//    imageClassifierHelper: ImageClassifierHelper, translatorProvider: TranslatorProvider
//) {
//    val showCameraView = remember { mutableStateOf(false) }
//    val showUploadedImage = remember { mutableStateOf(false) }
//
//    val uploadedImageUri = remember { mutableStateOf(Uri.EMPTY) }
////    val uploadedImageResultText = remember { mutableStateOf("") }
////    val uploadedImageTranslatedText = remember { mutableStateOf("") }
//
//    var classifiedText by remember { mutableStateOf("") }
//    var translatedText by remember { mutableStateOf("") }
//
//    imageClassifierHelper.imageClassifierListener =
//        object : ImageClassifierHelper.ClassifierListener {
//            override fun onError(error: String) {
//            }
//
//            override fun onResults(results: List<Classifications>?, inferenceTime: Long) {
//                results.getString().let {
//                    classifiedText = it
//                }
//            }
//        }
//
//    translatorProvider.translate(classifiedText, onSuccess = {
//        translatedText = it
//    }, onFailure = {})
//
//
//    Column {
//        //Scanner button view
//        ScannerButton(onShowCameraView = {
//            showCameraView.value = true
//        })
//
//        ImagePickerButton(imageClassifierHelper = imageClassifierHelper,
//            translatorProvider = translatorProvider,
//            onShowScannedImage = { imageUri, resultText, translatedText ->
//                uploadedImageUri.value = imageUri
////                uploadedImageResultText.value = resultText
////                uploadedImageTranslatedText.value = translatedText
////                showUploadedImage.value = true
//            })
//    }
//
//    // Scanner camera view
//    if (showCameraView.value) {
//        CameraView(
//            imageClassifierHelper = imageClassifierHelper,
//            translatorProvider = translatorProvider,
//            onBackPress = {
//                showCameraView.value = false
//            })
//    }
//
//    AnimatedVisibility(visible = showUploadedImage.value, enter = scaleIn(), exit = fadeOut()) {
////        UploadedImageScreen(
////            uploadedImageUri = uploadedImageUri.value,
////            uploadedImageResultText = uploadedImageResultText.value,
////            uploadedImageTranslatedText = uploadedImageTranslatedText.value,
////            onBackPressed = { showUploadedImage.value = false }
////        )
//    }
//}
//
//@Composable fun ImagePickerButton(
//    imageClassifierHelper: ImageClassifierHelper,
//    translatorProvider: TranslatorProvider,
//    onShowScannedImage: (imageUri: Uri, resultText: String, translatedText: String) -> Unit
//) {
//    var imageUri by remember { mutableStateOf(Uri.EMPTY) }
//    var bitmapBuffer by remember { mutableStateOf<Bitmap?>(null) }
//
////    var resultText by remember {
////        mutableStateOf("")
////    }
//
//    val context = LocalContext.current
//
////    imageClassifierHelper.imageClassifierListener =
////        object : ImageClassifierHelper.ClassifierListener {
////            override fun onError(error: String) {
////                Log.w("mytag", "onError: $error")
////            }
////
////            override fun onResults(results: List<Classifications>?, inferenceTime: Long) {
////                Log.w("mytag", "onResults: $results")
////                results.getString().let {
////                    resultText = it
////                }
////            }
////        }
//
////    LaunchedEffect(resultText) {
////        resultText.takeIf { it.isNotEmpty() }?.let {
////            translatorProvider.translate(resultText, onSuccess = { translatedText ->
////                onShowScannedImage.invoke(imageUri, resultText, translatedText)
////            }, onFailure = {})
////        }
////    }
//
//    val launcher = rememberLauncherForActivityResult(
//        contract = ActivityResultContracts.GetContent()
//    ) { uri: Uri? ->
//        uri?.let {
//            imageUri = uri
//            bitmapBuffer = ImageUtils.getBitmap(context, uri)
//            bitmapBuffer?.let {
//                imageClassifierHelper.classify(it)
//            }
//        }
//    }
//
//    Button(modifier = Modifier
//        .fillMaxWidth()
//        .padding(10.dp), onClick = {
//        launcher.launch("image/*")
//    }) {
//        Text(text = "Pick Image", fontSize = 20.sp)
//    }
//}