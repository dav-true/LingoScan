package com.lingoscan.presentations.mapper

import com.lingoscan.model.Dictionary
import com.lingoscan.model.Statistic
import com.lingoscan.model.Word
import com.lingoscan.presentations.DictionaryPresentation
import com.lingoscan.presentations.StatisticPresentation
import com.lingoscan.presentations.WordPresentation
import com.lingoscan.utils.getShortDate

fun Dictionary.toPresentation(): DictionaryPresentation = DictionaryPresentation(
    id = _id.toHexString(),
    name = name,
    language = language,
    image = image,
    words = words.map { it.toPresentation() }
)

fun Word.toPresentation(): WordPresentation = WordPresentation(
    id = _id.toHexString(),
    language = language,
    name = name,
    image = image,
    translation = translation
)

fun Statistic.toPresentation(): StatisticPresentation {

    val date = getShortDate(timestamp.epochSeconds * 1000L)
    return StatisticPresentation(
        id = _id.toHexString(),
        dictionary_name = dictionary_name,
        wordsCorrect = wordsCorrect,
        wordsTotal = wordsTotal,
        date = date,
        test_type = test_type,
        language = language
    )
}