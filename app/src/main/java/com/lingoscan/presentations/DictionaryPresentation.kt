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