package com.lingoscan.presentations.mapper

import com.lingoscan.model.Dictionary
import com.lingoscan.model.Word
import com.lingoscan.presentations.DictionaryPresentation
import com.lingoscan.presentations.WordPresentation

fun Dictionary.toPresentation() : DictionaryPresentation = DictionaryPresentation(
    id = _id.toHexString(),
    name = name,
    language = language,
    image = image,
    words = words.map { it.toPresentation() }
)

fun Word.toPresentation() : WordPresentation = WordPresentation(
    id = _id.toHexString(),
    language = language,
    name = name,
    image = image,
    translation = translation
)