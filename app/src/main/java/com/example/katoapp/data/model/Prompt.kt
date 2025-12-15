package com.example.katoapp.data.model

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.PropertyName
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

//data class Prompt(
//
//    @get:PropertyName("id") val id: String = "" ,
//    @get:PropertyName("Judul") val title: String = "" ,
//    @get:PropertyName("LinkGambar") val imageUrl: String = "" ,
//    @get:PropertyName("Main Kategori") val category: String = "" ,
//    @get:PropertyName("Rating") val rating: String = "0.0" ,
//    @get:PropertyName("Status") val status: String = ""
//)

//data class Prompt(
//    val id: String = "",
//    val title: String = "",
//    val imageUrl: String = "",
//    val category: String = "", // Main Kategori
//    val rating: String = "0.0",
//    val status: String = ""
//)

data class Prompt(
    @DocumentId
    val id: String = "",

    @get:PropertyName("Judul")
    val title: String = "",

    @get:PropertyName("Prompt")
    val content: String = "",

    @get:PropertyName("KategoriUtama")
    val category: String = "",

    @get:PropertyName("KategoriUmum")
    val subCategories: List<String> = emptyList(),

    @get:PropertyName("LinkGambar")
    val imageUrl: String = "",

    @get:PropertyName("ModelAi")
    val aiModel: String = "",

    @get:PropertyName("VersiModelAi")
    val modelVersion: String = "",

    @get:PropertyName("Status")
    val status: String = "",

    @get:PropertyName("Rating")
    val rating: String = "New",

    @ServerTimestamp
    @get:PropertyName("Tanggal")
    val createdAt: Date? = null
)

