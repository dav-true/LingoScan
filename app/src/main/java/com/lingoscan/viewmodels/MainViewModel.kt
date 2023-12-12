package com.lingoscan.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lingoscan.database.MongoDB
import com.lingoscan.model.Dictionary
import com.lingoscan.model.Word
import com.lingoscan.presentations.DictionaryPresentation
import com.lingoscan.presentations.WordPresentation
import com.lingoscan.presentations.mapper.toPresentation
import com.lingoscan.utils.preferences.PersistentStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.mongodb.kbson.ObjectId
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    val mongoDB: MongoDB,
    val persistentStorage: PersistentStorage
) : ViewModel() {

    private val _dictionaries: MutableStateFlow<List<DictionaryPresentation>?> =
        MutableStateFlow(null)
    val dictionaries = _dictionaries.asStateFlow()

    private val _words: MutableStateFlow<List<WordPresentation>?> =
        MutableStateFlow(null)

    val words = _words.asStateFlow()

    private val _selectedWord = MutableStateFlow<WordPresentation?>(null)
    val selectedWord = _selectedWord.asStateFlow()

    fun getDictionaries() {
        viewModelScope.launch {
            mongoDB.getDictionaries(persistentStorage.targetLanguage).collectLatest {
                _dictionaries.value = it.map { it.toPresentation() }
            }
        }
    }

    fun createDictionary(name: String) {
        viewModelScope.launch {
            mongoDB.addDictionary(
                Dictionary().apply {
                    this.name = name
                    language = persistentStorage.targetLanguage
                }
            )

            getDictionaries()
        }
    }

    fun getWords(dictionaryId: String) {
        viewModelScope.launch {
            mongoDB.getWords(dictionaryId).collectLatest {
                _words.value = it.map { it.toPresentation() }
            }
        }
    }

    fun addWordToDictionary(
        name: String,
        translation: String,
        image: String,
        dictionaryId: String
    ) {
        viewModelScope.launch {
            mongoDB.addWord(
                Word().apply {
                    this.language = persistentStorage.targetLanguage
                    this.name = name
                    this.translation = translation
                    this.image = image
                },
                dictionaryId = dictionaryId
            )
        }
    }

    fun deleteAllWordsFromDicitionary() {
        viewModelScope.launch {
            mongoDB.deleteAllWordsFromDictionary()
        }
    }

    fun deleteAllDictionaries() {
        viewModelScope.launch {
            mongoDB.deleteAllDictionaries()
        }
    }

    fun deleteDictionary(id: String) {
        viewModelScope.launch {
            mongoDB.deleteDictionary(id)
            getDictionaries()
        }
    }

    fun renameDictionary(id: String, name: String) {
        viewModelScope.launch {
            mongoDB.renameDictionary(id, name)
            getDictionaries()
        }
    }

    fun removeWord(dictionaryId: String, wordId: String) {
        viewModelScope.launch {
            mongoDB.deleteWord(dictionaryId, wordId)
            getWords(dictionaryId)
        }
    }

    fun moveWord(sourceDictionaryId: String, targetDictionaryId: String, wordId: String) {
        viewModelScope.launch {
            mongoDB.moveWord(sourceDictionaryId, targetDictionaryId, wordId)
            getWords(sourceDictionaryId)
        }
    }

    fun getWordById(wordId: String) {
        viewModelScope.launch {
            mongoDB.getWord(wordId).collectLatest {
                _selectedWord.value = it?.toPresentation()
            }
        }
    }

    fun updateWord(wordText: String, translationText: String, wordId: String) {
        viewModelScope.launch {
            mongoDB.updateWord(
                Word().apply {
                    this._id = ObjectId(wordId)
                    this.name = wordText
                    this.translation = translationText
                }
            )
        }
    }
}
