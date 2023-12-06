package com.lingoscan.scan.utils

import org.tensorflow.lite.task.vision.classifier.Classifications



// Get classified results from the list of classifications
// returned by the image classifier in the form of a string
fun List<Classifications>?.getString(): String {
    return this?.get(0)?.categories?.sortedBy { it?.index }
        ?.filter { it.label.isNotEmpty() }
        ?.takeIf { it.isNotEmpty() }
        ?.take(1)
        ?.let { results ->
            results.joinToString { it?.label.toString() }
        }.orEmpty()
}