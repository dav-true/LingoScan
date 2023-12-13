package com.lingoscan.utils.translate

import android.util.Log
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.common.model.RemoteModelManager
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.TranslateRemoteModel
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.Translator
import com.google.mlkit.nl.translate.TranslatorOptions
import com.lingoscan.utils.preferences.PersistentStorage
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TranslatorHelper @Inject constructor(
    val persistentStorage: PersistentStorage
) {

    private lateinit var translator: Translator
    private val modelManager: RemoteModelManager = RemoteModelManager.getInstance()


//    init {
//        create(onStart = {}, onSuccess = {}, onFailure = {})
//    }

//    fun get(): Translator {
//        return translator
//    }

    fun create(
        onStart: (() -> Unit)? = null,
        onSuccess: (() -> Unit)? = null,
        onFailure: ((Exception) -> Unit)? = null
    ) {
        val options = TranslatorOptions.Builder().setSourceLanguage(TranslateLanguage.ENGLISH)
            .setTargetLanguage(persistentStorage.targetLanguage).build()
        translator = Translation.getClient(options)

        val conditions = DownloadConditions.Builder().build()

        translator.downloadModelIfNeeded(conditions).also {
                onStart?.invoke()
            }.addOnSuccessListener {
                onSuccess?.invoke()
            }.addOnFailureListener { exception ->
                onFailure?.invoke(exception)
            }
    }


    fun translate(
        text: String, onSuccess: (String) -> Unit, onFailure: ((Exception) -> Unit)? = null
    ) {
        translator.translate(text).addOnSuccessListener { translatedText ->
                onSuccess(translatedText)
            }.addOnFailureListener { exception ->
                onFailure?.invoke(exception)
            }
    }


    fun getAllLanguages(
        onSuccess: (List<LanguageModel>) -> Unit,
    ) {
        TranslateLanguage.getAllLanguages().filter {
                it != TranslateLanguage.ENGLISH && it != TranslateLanguage.RUSSIAN
            }.map {
                val locale = Locale(it)
                LanguageModel(
                    language = locale.displayLanguage, tag = it
                )
            }.let { onSuccess(it) }
    }

    fun getCurrentLanguage(): LanguageModel {
        val locale = Locale(persistentStorage.targetLanguage)
        return LanguageModel(
            language = locale.displayLanguage, tag = persistentStorage.targetLanguage
        )
    }

    fun getDownloadedLanguages(
        onSuccess: (List<LanguageModel>) -> Unit, onError: ((Exception) -> Unit)? = null
    ) {
        modelManager.getDownloadedModels(TranslateRemoteModel::class.java)
            .addOnSuccessListener { models ->
                models.filter {
                    it.language != TranslateLanguage.ENGLISH
                }.map {
                    val locale = Locale(it.language)
                    LanguageModel(
                        language = locale.displayLanguage, tag = it.language
                    )
                }.sortedBy { it.language }.let { onSuccess(it) }
            }.addOnFailureListener {
                onError?.invoke(it)
            }

    }

    fun deleteDownloadedLanguage(
        languageTag: String, onSuccess: () -> Unit, onError: ((Exception) -> Unit)? = null
    ) {
        val model = TranslateRemoteModel.Builder(languageTag).build()
        modelManager.deleteDownloadedModel(model).addOnSuccessListener {
                onSuccess()
            }.addOnFailureListener {
                onError?.invoke(it)
            }
    }

    fun downloadLanguage(
        languageTag: String, onSuccess: () -> Unit, onError: ((Exception) -> Unit)? = null
    ) {
        val model = TranslateRemoteModel.Builder(languageTag).build()
        modelManager.download(model, DownloadConditions.Builder().build()).addOnSuccessListener {
                onSuccess()
            }.addOnFailureListener {
                Log.w("mytag", it.toString())
                onError?.invoke(it)
            }
    }
}


data class LanguageModel(
    val language: String, val tag: String
)