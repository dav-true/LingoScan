package com.lingoscan.database

import com.lingoscan.model.Dictionary
import com.lingoscan.model.Statistic
import com.lingoscan.model.Word
//import com.lingoscan.model.User
import kotlinx.coroutines.flow.Flow
import org.mongodb.kbson.ObjectId

interface MongoRepository {
    fun configureTheRealm()
    fun getDictionaries(currentLanguage: String): Flow<List<Dictionary>>
    suspend fun addDictionary(dictionary: Dictionary)
    suspend fun addStatistic(dictionaryId: String, statistic: Statistic)
    suspend fun getStatistics(currentLanguage: String): Flow<List<Statistic>>
    suspend fun deleteDictionary(id: String)

    suspend fun addWord(word: Word, dictionaryId: String)

    suspend fun updateWord(updatedWord: Word)

    suspend fun moveWord(sourceDictionaryId: String, targetDictionaryId: String, wordId: String)

    suspend fun deleteWord(dictionaryId: String, wordId: String)

    suspend fun getWords(dictionaryId: String): Flow<List<Word>>

    suspend fun getWord(wordId: String): Flow<Word?>

    suspend fun deleteAllWordsFromDictionary()

    suspend fun deleteAllDictionaries()

    suspend fun renameDictionary(dictionaryId: String, name: String)
}