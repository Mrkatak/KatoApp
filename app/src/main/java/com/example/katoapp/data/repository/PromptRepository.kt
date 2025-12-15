package com.example.katoapp.data.repository

import android.net.Uri
import com.example.katoapp.data.model.Prompt
import com.example.katoapp.data.remote.CloudinaryHelper
import com.example.katoapp.data.remote.ResourceCloudinary
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class PromptRepository @Inject constructor(
    private val firestore: FirebaseFirestore ,
    private val auth: FirebaseAuth,
    private val cloudinaryHelper: CloudinaryHelper
) {

    //function get kategori utama
    suspend fun getMainCategories(): List<String> {
        return try {
            val snapshot = firestore.collection("KategoriUtama")
                .get()
                .await()

            snapshot.documents.mapNotNull { doc ->
                doc.getString("NamaKategori") //get field Namakategori
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    //function get kategori umum
    suspend fun getGeneralCategories(): List<String> {
        return try {
            val snapshot = firestore.collection("KategoriUmum")
                .get()
                .await()
            snapshot.documents.mapNotNull { doc ->
                doc.getString("NamaKategori")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    //function save prompt
    suspend fun savePrompt(
        title: String,
        content: String,
        mainCategory: String,
        subCategories: List<String>,
        aiModel: String,
        modelVersion: String,
        imageUri: Uri?,
        isSharing: Boolean
    ) {
        //pindahkan operasi ke IO Thread
        return withContext(Dispatchers.IO) {
            val currentUser = auth.currentUser ?: throw Exception("User belum login")
            //upload image
            var finalImageUrl = ""
            if (imageUri != null) {
                val uploadResult = cloudinaryHelper.uploadImage(imageUri).first { it !is ResourceCloudinary.Loading }
                when (uploadResult) {
                    is ResourceCloudinary.Success -> finalImageUrl = uploadResult.data
                    is ResourceCloudinary.Error -> throw Exception(uploadResult.message)
                    else -> {}
                }
            }
            //data map (nanti pake data class utk value nya)
            val promptData = hashMapOf(
                "Judul" to title,
                "Prompt" to content,
                "MainKategori" to mainCategory,
                "SubKategori" to subCategories,
                "ModelAI" to aiModel,
                "VersiModelAI" to modelVersion,
                "LinkGambar" to finalImageUrl,
                "Status" to if (isSharing) "sharing" else "private",
                "Tanggal" to FieldValue.serverTimestamp()
            )
            //save to firestore
            firestore.collection("pengguna")
                .document(currentUser.uid)
                .collection("PrivatePrompt")
                .add(promptData)
                .await()
        }
    }

    //function get private prompt
    suspend fun getPrivatePrompts(): List<Prompt> {
        return try {
            val uid = auth.currentUser?.uid ?: return emptyList()

            val snapshot = firestore.collection("pengguna")
                .document(uid)
                .collection("PrivatePrompt")
                .orderBy("Tanggal", Query.Direction.DESCENDING)
                .get()
                .await()

            snapshot.documents.map { doc ->
                Prompt(
                    id = doc.id,
                    title = doc.getString("Judul") ?: "",
                    imageUrl = doc.getString("LinkGambar") ?: "",
                    category = doc.getString("MainKategori") ?: "",
                    rating = doc.getString("Rating") ?: "New",
                    status = doc.getString("Status") ?: ""
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }


}