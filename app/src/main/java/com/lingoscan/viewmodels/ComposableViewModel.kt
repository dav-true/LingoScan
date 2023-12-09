package com.lingoscan.viewmodels

import androidx.lifecycle.ViewModel
import com.lingoscan.scan.utils.ImageClassifierHelper
import com.lingoscan.translate.utils.TranslatorHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
open class ComposableViewModel @Inject constructor(
    val imageClassifierHelper: ImageClassifierHelper,
    val translatorHelper: TranslatorHelper
) : ViewModel(){

}