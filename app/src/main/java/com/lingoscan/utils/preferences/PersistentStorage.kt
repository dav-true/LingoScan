package com.lingoscan.utils.preferences

import android.content.Context
import android.content.SharedPreferences
import com.google.mlkit.nl.translate.TranslateLanguage
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class PersistentStorage @Inject constructor(
    @ApplicationContext private val context: Context
) {

    companion object {
        const val LINGO_SCAN_PREFERENCES = "LINGO_SCAN_PREFS"
        const val PREF_TARGET_LANGUAGE = "TARGET_LANGUAGE_PREF"
    }

    private val sharePreferences: SharedPreferences by lazy {
        context.getSharedPreferences(LINGO_SCAN_PREFERENCES, Context.MODE_PRIVATE)
    }

    val targetLanguage: String
        get() = sharePreferences.getString(PREF_TARGET_LANGUAGE, TranslateLanguage.UKRAINIAN)
            ?: TranslateLanguage.UKRAINIAN

    fun setTargetLanguage(targetLanguage: String) {
        sharePreferences.edit().putString(PREF_TARGET_LANGUAGE, targetLanguage).apply()
    }
}