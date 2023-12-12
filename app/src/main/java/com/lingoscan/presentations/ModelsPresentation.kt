package com.lingoscan.presentations

data class DictionaryPresentation(
    val id: String,
    val name: String,
    val language: String,
    val image: String,
    val words: List<WordPresentation>
)


data class WordPresentation(
    val id: String,
    val language: String,
    val name: String,
    val image: String,
    val translation: String
)

data class StatisticPresentation(
    val id: String,
    val dictionary_name: String,
    val wordsCorrect: Int,
    val wordsTotal: Int,
    val date: String,
    val test_type: String,
    val language: String
)