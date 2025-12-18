package com.example.katoapp.data.repository

import android.net.Uri
import androidx.compose.animation.core.snap
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

    private val adminDocId = "R7LaZTfFIzlePgo8crgK"

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
        title: String, content: String, mainCategory: String,
        subCategories: List<String>, aiModel: String, modelVersion: String,
        imageUri: Uri?, isSharing: Boolean
    ) {
        withContext(Dispatchers.IO) {
            val currentUser = auth.currentUser ?: throw Exception("User belum login")
            //upload gambar
            var finalImageUrl = ""
            if (imageUri != null) {
                val uploadResult = cloudinaryHelper.uploadImage(imageUri).first { it !is ResourceCloudinary.Loading }
                when (uploadResult) {
                    is ResourceCloudinary.Success -> finalImageUrl = uploadResult.data
                    is ResourceCloudinary.Error -> throw Exception(uploadResult.message)
                    else -> {}
                }
            }

            val promptId = firestore.collection("pengguna").document().id

            //map (nanti pake data class utk value nya)
            val promptData = hashMapOf(
                "Judul" to title,
                "Prompt" to content,
                "MainKategori" to mainCategory,
                "SubKategori" to subCategories,
                "ModelAi" to aiModel,
                "VersiModelAi" to modelVersion,
                "LinkGambar" to finalImageUrl,
                "Status" to if (isSharing) "sharing" else "private",
                "UserId" to currentUser.uid,
                "UserEmail" to (currentUser.email ?: ""),
                "Tanggal" to FieldValue.serverTimestamp(),
                "Rating" to "New",
                "UsegeCount" to 0
            )

            val batch = firestore.batch()

            //save private
            val privateRef = firestore.collection("pengguna")
                .document(currentUser.uid)
                .collection("PrivatePrompt")
                .document(promptId)
            batch.set(privateRef, promptData)

            //save sharing to admin
            if (isSharing) {
                val adminRef = firestore.collection("admin")
                    .document(adminDocId)
                    .collection("SharingPrompt")
                    .document(promptId)
                batch.set(adminRef, promptData)
            }

            batch.commit().await()
        }
    }

    //function get private prompt
    suspend fun getPrivatePrompts(): List<Prompt> {
        return try {
            val uid = auth.currentUser?.uid ?: return emptyList()

            val snapshot = firestore.collection("pengguna")
                .document(uid)
                .collection("PrivatePrompt")
                .whereEqualTo("Status", "private") //filter
                .orderBy("Tanggal", Query.Direction.DESCENDING)
                .get()
                .await()

            mapSnapshotToPromptList(snapshot)
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    //function get sharing prompt
    suspend fun getSharingPrompts(): List<Prompt> {
        return try {
            val uid = auth.currentUser?.uid ?: return emptyList()

            val snapshot = firestore.collection("pengguna")
                .document(uid)
                .collection("PrivatePrompt")
                .whereEqualTo("Status", "sharing")// filter
                .orderBy("Tanggal", Query.Direction.DESCENDING)
                .get()
                .await()

            mapSnapshotToPromptList(snapshot)
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    //function get prompt by id
    suspend fun getPromptById(promptId: String): Prompt? {
        return try {
            val uid = auth.currentUser?.uid

            //cek di private pengguna
            if (uid != null) {
                val privateDoc = firestore.collection("pengguna")
                    .document(uid)
                    .collection("PrivatePrompt")
                    .document(promptId)
                    .get()
                    .await()

                if (privateDoc.exists()) {
                    return mapDocumentToPrompt(privateDoc)
                }
            }

            //cek di sharing admin
            val publicDoc = firestore.collection("admin")
                .document(adminDocId)
                .collection("SharingPrompt")
                .document(promptId)
                .get()
                .await()

            if (publicDoc.exists()) {
                return mapDocumentToPrompt(publicDoc)
            }
            null

        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    //function get top 5 popular prompt
    suspend fun getPopularPrompts(): List<Prompt> {
        return try {
            val snapshot = firestore.collection("admin")
                .document(adminDocId)
                .collection("SharingPrompt")
                .orderBy("UsegeCount", Query.Direction.DESCENDING)
                .limit(5)
                .get()
                .await()
            mapSnapshotToPromptList(snapshot)
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    //function get top 5 ratting prompt
    suspend fun getTopRatedPrompts(): List<Prompt> {
        return try {
            val snapshot = firestore.collection("admin")
                .document(adminDocId)
                .collection("SharingPrompt")
                .orderBy("Rating", Query.Direction.DESCENDING)
                .limit(5)
                .get()
                .await()
            mapSnapshotToPromptList(snapshot)
        } catch (e: Exception){
            e.printStackTrace()
            emptyList()
        }
    }

    private fun mapSnapshotToPromptList(snapshot: com.google.firebase.firestore.QuerySnapshot): List<Prompt> {
        return snapshot.documents.map { doc -> mapDocumentToPrompt(doc) }
    }

    private fun mapDocumentToPrompt(doc: com.google.firebase.firestore.DocumentSnapshot): Prompt {
        fun getField(vararg keys: String): String {
            for (key in keys) {
                val value = doc.getString(key)
                if (!value.isNullOrEmpty()) return value
            }
            return ""
        }
        fun getListField(vararg keys: String): List<String> {
            for (key in keys) {
                val value = doc.get(key)
                if (value is List<*>) return value as List<String>
            }
            return emptyList()
        }
        return Prompt(
            id = doc.id,
            title = getField("Judul"),
            imageUrl = getField("LinkGambar"),
            // Cek berbagai kemungkinan nama field
            category = getField("KategoriUtama", "MainKategori"),
            subCategories = getListField("KategoriUmum", "SubKategori"),
            aiModel = getField("ModelAi", "ModelAI"),
            modelVersion = getField("VersiModelAi", "VersiModelAI"),
            content = getField("Prompt"),
            rating = getField("Rating").ifEmpty { "New" },
            status = getField("Status"),
            createdAt = doc.getDate("Tanggal"),
            usageCount = doc.getLong("UsageCount")?.toInt() ?: 0
        )
    }






}