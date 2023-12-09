package com.lingoscan.viewmodels

import androidx.lifecycle.ViewModel
import com.lingoscan.utils.preferences.PersistentStorage
import com.lingoscan.utils.scan.ImageClassifierHelper
import com.lingoscan.utils.translate.TranslatorHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
open class ComposableViewModel @Inject constructor(
    val imageClassifierHelper: ImageClassifierHelper,
    val translatorHelper: TranslatorHelper,
    val persistentStorage: PersistentStorage
) : ViewModel(){

}