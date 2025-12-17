package com.example.katoapp.data.model

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.PropertyName
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

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

    @get:PropertyName("UsageCount")
    val usageCount: Int = 0,

    @ServerTimestamp
    @get:PropertyName("Tanggal")
    val createdAt: Date? = null


)

