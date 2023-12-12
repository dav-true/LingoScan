package com.lingoscan.model

import io.realm.kotlin.ext.realmListOf
import io.realm.kotlin.types.RealmInstant
import io.realm.kotlin.types.RealmList
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.ObjectId

//class User : RealmObject {
//    @PrimaryKey var _id: ObjectId = ObjectId.invoke()
//    var owner_id: String = ""
//    var name: String = ""
//    var email: String = ""
//    var dictionaries: RealmList<Dictionary> = realmListOf()
//}


class Dictionary : RealmObject {
    @PrimaryKey var _id: ObjectId = ObjectId.invoke()
    var owner_id: String = ""
    var name: String = ""
    var language: String = ""
    var image: String = ""
    var words: RealmList<Word> = realmListOf()
}

class Word : RealmObject {
    @PrimaryKey var _id: ObjectId = ObjectId.invoke()
    var owner_id: String = ""
    var language: String = ""
    var name: String = ""
    var image: String = ""
    var translation: String = ""
}

class Statistic : RealmObject {
    @PrimaryKey var _id: ObjectId = ObjectId.invoke()
    var owner_id: String = ""
    var dictionary_name: String = ""
    var wordsCorrect: Int = 0
    var wordsTotal: Int = 0
    var test_type: String = ""
    var language: String = ""
    var timestamp: RealmInstant = RealmInstant.now()
}

