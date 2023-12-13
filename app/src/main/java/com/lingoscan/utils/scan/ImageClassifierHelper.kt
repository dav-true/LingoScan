package com.lingoscan.utils.scan

import android.content.Context
import android.graphics.Bitmap
import android.os.SystemClock
import android.util.Log
import android.view.Surface
import androidx.camera.core.ImageProxy
import dagger.hilt.android.qualifiers.ApplicationContext
import org.tensorflow.lite.gpu.CompatibilityList
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import org.tensorflow.lite.support.image.ops.ResizeWithCropOrPadOp
import org.tensorflow.lite.task.core.BaseOptions
import org.tensorflow.lite.task.core.vision.ImageProcessingOptions
import org.tensorflow.lite.task.vision.classifier.Classifications
import org.tensorflow.lite.task.vision.classifier.ImageClassifier
import javax.inject.Inject


class ImageClassifierHelper @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private var imageClassifier: ImageClassifier? = null

    var imageClassifierListener: ClassifierListener? = null

    var threshold: Float = 0.5f
    var numThreads: Int = 4
    var maxResults: Int = 1
    var currentModel: Int = MODEL_MOBILENETV3

    init {
        setupImageClassifier()
    }

    private fun setupImageClassifier() {
        val optionsBuilder = ImageClassifier.ImageClassifierOptions.builder()
            .setScoreThreshold(threshold)
            .setMaxResults(maxResults)

        val baseOptionsBuilder = BaseOptions.builder().setNumThreads(numThreads)

        if (CompatibilityList().isDelegateSupportedOnThisDevice) {
            baseOptionsBuilder.useGpu()
        }

        optionsBuilder.setBaseOptions(baseOptionsBuilder.build())

        val modelName =
            when (currentModel) {
                MODEL_MOBILENETV1 -> "mobilenetv1.tflite"
                MODEL_MOBILENETV2 -> "mobilenetv2.tflite"
                MODEL_MOBILENETV3 -> "mobilenetv3.tflite"
                else -> "mobilenetv3.tflite"
            }

        try {
            imageClassifier =
                ImageClassifier.createFromFileAndOptions(context, modelName, optionsBuilder.build())
        } catch (e: IllegalStateException) {
            imageClassifierListener?.onError(
                "Image classifier failed to initialize. See error logs for details"
            )
        }
    }

    fun classify(bitmap: Bitmap) {
        classify(null, bitmap, Surface.ROTATION_90)
    }

    fun classify(imageProxy: ImageProxy?, bitmapBuffer: Bitmap, rotation: Int) {
        imageProxy?.use { bitmapBuffer.copyPixelsFromBuffer(imageProxy.planes[0].buffer) }

        if (imageClassifier == null) {
            setupImageClassifier()
        }

        // Inference time is the difference between the system time at the start and finish of the
        // process
        var inferenceTime = SystemClock.uptimeMillis()

        val width: Int = bitmapBuffer.width
        val height: Int = bitmapBuffer.height

        val size = if (height > width) width else height

        val imageProcessor =
            ImageProcessor.Builder()
                .add(ResizeWithCropOrPadOp(size, size))
                .add(
                    ResizeOp(224, 224, ResizeOp.ResizeMethod.BILINEAR)
                )
                .build()

        // Preprocess the image and convert it into a TensorImage for classification.
        val tensorImage = imageProcessor.process(TensorImage.fromBitmap(bitmapBuffer))

        val imageProcessingOptions = ImageProcessingOptions.builder()
            .setOrientation(getOrientationFromRotation(rotation))
            .build()

        val results = imageClassifier?.classify(tensorImage, imageProcessingOptions)
        inferenceTime = SystemClock.uptimeMillis() - inferenceTime
        imageClassifierListener?.onResults(
            results,
            inferenceTime
        )
    }

    private fun getOrientationFromRotation(rotation: Int): ImageProcessingOptions.Orientation {
        return when (rotation) {
            Surface.ROTATION_270 ->
                ImageProcessingOptions.Orientation.BOTTOM_RIGHT

            Surface.ROTATION_180 ->
                ImageProcessingOptions.Orientation.RIGHT_BOTTOM

            Surface.ROTATION_90 ->
                ImageProcessingOptions.Orientation.TOP_LEFT

            else ->
                ImageProcessingOptions.Orientation.RIGHT_TOP
        }
    }

    interface ClassifierListener {
        fun onError(error: String)
        fun onResults(
            results: List<Classifications>?,
            inferenceTime: Long
        )
    }

    companion object {
        const val MODEL_MOBILENETV1 = 1
        const val MODEL_MOBILENETV2 = 2
        const val MODEL_MOBILENETV3 = 3

        private const val TAG = "ImageClassifierHelper"
    }
}
