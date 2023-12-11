package com.lingoscan.database

import com.lingoscan.model.Dictionary
import com.lingoscan.model.Word
//import com.lingoscan.model.User
import kotlinx.coroutines.flow.Flow
import org.mongodb.kbson.ObjectId

interface MongoRepository {
    fun configureTheRealm()
    fun getDictionaries(currentLanguage: String): Flow<List<Dictionary>>
    suspend fun addDictionary(dictionary: Dictionary)
    suspend fun updateDictionary(dictionary: Dictionary)
    suspend fun deleteDictionary(id: String)

    suspend fun addWord(word: Word, dictionaryId: String)

    suspend fun moveWord(word: Word)

    suspend fun deleteWord(word: Word)

    suspend fun getWords(dictionaryId: String): Flow<List<Word>>

    suspend fun deleteAllWordsFromDictionary()

    suspend fun createDictionaryWithWord(dictionary: Dictionary, word: Word)

    suspend fun deleteAllDictionaries()
}