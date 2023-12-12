package com.lingoscan.viewmodels

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.lingoscan.presentations.WordPresentation
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LearningViewModel @Inject constructor() : ViewModel() {

    val options = mutableStateOf<List<String>>(emptyList())

    val currentScore = mutableStateOf(0)
    val totalWordsCount = mutableStateOf(0)

    fun getTranslationOptions(
        words: List<WordPresentation>,
        correctWord: WordPresentation
    ){
        options.value =  words.filter { it.id != correctWord.id }.map { it.translation }.asSequence()
            .shuffled()
            .take(3)
            .distinct()
            .toMutableList().apply {
                add(correctWord.translation)
            }.shuffled()
    }

}