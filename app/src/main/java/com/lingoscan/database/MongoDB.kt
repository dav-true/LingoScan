package com.lingoscan.database

import android.util.Log
import com.lingoscan.Constants
import com.lingoscan.model.Dictionary
//import com.lingoscan.model.User
import com.lingoscan.model.Word
import io.realm.kotlin.MutableRealm
import io.realm.kotlin.Realm
import io.realm.kotlin.UpdatePolicy
import io.realm.kotlin.ext.copyFromRealm
import io.realm.kotlin.ext.query
import io.realm.kotlin.log.LogLevel
import io.realm.kotlin.mongodb.App
import io.realm.kotlin.mongodb.sync.SyncConfiguration
import io.realm.kotlin.query.find
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.forEach
import kotlinx.coroutines.flow.map
import org.mongodb.kbson.ObjectId
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class MongoDB @Inject constructor() : MongoRepository {

    private val app = App.create(Constants.ATLAS_APP_ID)
    private val user = app.currentUser
    private lateinit var realm: Realm

    init {
        configureTheRealm()
    }

    override fun configureTheRealm() {
        if (user != null) {
            val config = SyncConfiguration.Builder(
                user, setOf(Dictionary::class, Word::class)
            ).initialSubscriptions { sub ->
                add(query = sub.query<Dictionary>(query = "owner_id == $0", user.id))
                add(query = sub.query<Word>(query = "owner_id == $0", user.id))
            }.log(LogLevel.ALL).build()
            realm = Realm.open(config)
        }
    }

    override suspend fun getWords(dictionaryId: String): Flow<List<Word>> {
        return realm.query<Dictionary>().query("_id == $0", ObjectId(dictionaryId)).first().asFlow()
            .map {
                it.obj?.words.orEmpty()
            }
    }

    override fun getDictionaries(currentLanguage: String): Flow<List<Dictionary>> {
        return realm.query<Dictionary>().query("language == $0", currentLanguage).asFlow()
            .map { it.list }
    }

    override suspend fun addWord(
        word: Word, dictionaryId: String
    ) {
        if (user != null) {
            realm.write {
                try {
                    addWordToDictionary(ObjectId(dictionaryId), word)
                } catch (e: Exception) {
                    Log.d("MongoRepository", e.message.toString())
                }
            }
        }
    }

    override suspend fun deleteAllWordsFromDictionary() {
        realm.write {
            try {
                realm.query<Word>().find { results ->
                    results.forEach { word ->
                        findLatest(word)?.let {
                            delete(it)
                        }
                    }
                }
            } catch (e: Exception) {
                Log.w("MongoRepository", e.message.toString())
            }
        }
    }

    override suspend fun deleteAllDictionaries() {
        realm.write {
            try {
                realm.query<Dictionary>().find { results ->
                    results.forEach { dictionary ->
                        findLatest(dictionary)?.let {
                            delete(it)
                        }
                    }
                }
            } catch (e: Exception) {
                Log.w("MongoRepository", e.message.toString())
            }
        }
    }

    override suspend fun renameDictionary(dictionaryId: String, name: String) {
        if (user != null) {
            realm.write {
                try {
                    realm.query<Dictionary>().query(
                        "_id == $0",
                        ObjectId(dictionaryId)
                    ).first().find()?.let {
                        findLatest(it)?.apply {
                            this.name = name
                        }?.let { latest ->
                            copyToRealm(latest)
                        }
                    }
                } catch (e: Exception) {
                    Log.d("MongoRepository", e.message.toString())
                }
            }
        }
    }


    override suspend fun addDictionary(dictionary: Dictionary) {
        if (user != null) {
            realm.write {
                try {
                    copyToRealm(dictionary.apply { owner_id = user.id })
                } catch (e: Exception) {
                    Log.d("MongoRepository", e.message.toString())
                }
            }

        }
    }

    override suspend fun updateDictionary(dictionary: Dictionary) {
    }

    override suspend fun deleteDictionary(id: String) {
        if (user != null) {
            realm.write {
                try {
                    realm.query<Dictionary>().query("_id == $0", ObjectId(id)).first().find()
                        ?.let { dictionary ->
                            dictionary.words.forEach {
                                findLatest(it)?.let { latest ->
                                    delete(latest)
                                }
                            }

                            findLatest(dictionary)?.let { latest ->
                                delete(latest)
                            }
                        }
                } catch (e: Exception) {
                    Log.d("MongoRepository", e.message.toString())
                }
            }
        }
    }

    override suspend fun moveWord(
        sourceDictionaryId: String,
        targetDictionaryId: String,
        wordId: String
    ) {
        if (user != null) {
            realm.writeBlocking {
                try {
                    val word =
                        realm.query<Word>().query("_id == $0", ObjectId(wordId)).first().find()
                            ?.let { word ->
                                findLatest(word)
                            }

                    word?.let {
                        addWordToDictionary(ObjectId(targetDictionaryId), it)

                        realm.query<Dictionary>().query("_id == $0", ObjectId(sourceDictionaryId))
                            .first()
                            .find()
                            ?.let { dictionary ->
                                findLatest(dictionary)?.apply {
                                    words.remove(word)
                                }?.let { latest ->
                                    copyToRealm(latest)
                                }
                            }
                    }
                } catch (e: Exception) {
                    Log.d("MongoRepository", e.message.toString())
                }
            }
        }
    }

    override suspend fun deleteWord(dictionaryId: String, wordId: String) {
        if (user != null) {
            realm.write {
                try {
                    realm.query<Dictionary>().query("_id == $0", ObjectId(dictionaryId)).first()
                        .find()
                        ?.let { dictionary ->
                            dictionary.words.firstOrNull { it._id == ObjectId(wordId) }?.let {
                                findLatest(it)?.let { latest ->
                                    delete(latest)
                                }
                            }
                        }

                    realm.query<Word>().query("_id == $0", ObjectId(wordId)).first().find()
                        ?.let { word ->
                            findLatest(word)?.let { latest ->
                                delete(latest)
                            }
                        }
                } catch (e: Exception) {
                    Log.d("MongoRepository", e.message.toString())
                }
            }
        }
    }

    private fun MutableRealm.addWordToDictionary(dictionaryId: ObjectId, word: Word) {
        if (user != null) {
            realm.query<Dictionary>()
                .query("_id == $0", dictionaryId)
                .first()
                .find()?.let {
                    findLatest(it)?.apply {
                        words.add(word.apply { owner_id = user.id })
                    }?.let { latest ->
                        copyToRealm(latest)
                    }
                }
        }
    }
}