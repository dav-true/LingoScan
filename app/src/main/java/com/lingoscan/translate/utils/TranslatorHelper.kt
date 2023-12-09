package com.lingoscan.translate.utils

import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.Translator
import com.google.mlkit.nl.translate.TranslatorOptions
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TranslatorHelper @Inject constructor() {

    private lateinit var translator: Translator
    private var targetLanguage: String = TranslateLanguage.ENGLISH


    fun get() : Translator {
        return translator
    }

    fun setTargetLanguage(targetLanguage: String) {
        this.targetLanguage = targetLanguage
    }

    fun create(onStart: () -> Unit, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val options = TranslatorOptions.Builder()
            .setSourceLanguage(TranslateLanguage.ENGLISH)
            .setTargetLanguage(targetLanguage)
            .build()
        translator = Translation.getClient(options)

        val conditions = DownloadConditions.Builder()
            .build()

        translator.downloadModelIfNeeded(conditions)
            .also{
                onStart()
            }
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }


    fun translate(text: String, onSuccess: (String) -> Unit, onFailure: (Exception) -> Unit) {
        translator.translate(text)
            .addOnSuccessListener { translatedText ->
                onSuccess(translatedText)
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }

}