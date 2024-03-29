package com.anawajha.mylibrary.model

import com.google.firebase.Timestamp
import java.util.*
import kotlin.collections.HashMap

data class Book(
    var id: String? = null,
    var name: String? = null,
    var author: String? = null,
    var year: Timestamp? = null,
    var price: Double? = null,
    var rate: Float? = null,
    var image: String? = null,
    var imagePath: String? = null

) {

    companion object {
        fun fromMap(map: HashMap<String, Any>): Book {
            return Book(
                null,
                map["name"].toString(),
                map["author"].toString(),
                map["year"] as Timestamp,
                map["price"] as Double,
                map["rate"] as Float,
                map["image"] as String,
                map["imagePath"] as String
            )
        }
    }

    fun toMap(): Map<String, Any?> {
        return hashMapOf(
            "id" to UUID.randomUUID().toString(),
            "name" to this.name,
            "author" to this.author,
            "year" to this.year,
            "price" to this.price,
            "rate" to this.rate,
            "image" to this.image,
            "imagePath" to this.imagePath
        )
    }

    fun toMapAsUpdate(): Map<String, Any?> {
        return hashMapOf(
            "name" to this.name,
            "author" to this.author,
            "year" to this.year,
            "price" to this.price,
            "rate" to this.rate
        )
    }
}