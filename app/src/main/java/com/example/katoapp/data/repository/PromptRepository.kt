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
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.math.min

class PromptRepository @Inject constructor(
    private val firestore: FirebaseFirestore ,
    private val auth: FirebaseAuth,
    private val cloudinaryHelper: CloudinaryHelper
) {

    private val adminDocId = "R7LaZTfFIzlePgo8crgK"

    fun getCurrentUserUid(): String? {
        return auth.currentUser?.uid
    }

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
                "Rating" to "0.0",
                "UsageCount" to 0
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

    //function get saved prompt
    suspend fun getSavedPrompts(): List<Prompt> {
        return try {
            val uid = auth.currentUser?.uid ?: return emptyList()

            val snapshot = firestore.collection("pengguna")
                .document(uid)
                .collection("SavedPrompt")
                .orderBy("Tanggal", Query.Direction.DESCENDING) // Urutkan dari kapan user menyimpannya
                .get()
                .await()

            mapSnapshotToPromptList(snapshot)
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    //function check if prompt is saved
    suspend fun isPromptSaved(promptId: String): Boolean {
        return try {
            val uid = auth.currentUser?.uid ?: return false
            val doc = firestore.collection("pengguna")
                .document(uid)
                .collection("SavedPrompt")
                .document(promptId)
                .get()
                .await()

            doc.exists()
        } catch (e: Exception) {
            false
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
                .orderBy("UsageCount", Query.Direction.DESCENDING)
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

    //function getAllPopularPrompts
    suspend fun getAllPopularPrompts(): List<Prompt> {
        return try {
            val snapshot = firestore.collection("admin")
                .document(adminDocId)
                .collection("SharingPrompt")
                .orderBy("UsageCount", Query.Direction.DESCENDING)
                .limit(50) //utk sementara batas 50
                .get()
                .await()

            mapSnapshotToPromptList(snapshot)
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    // function get all top rated prompts
    suspend fun getAllTopRatedPrompts(): List<Prompt> {
        return try {
            val snapshot = firestore.collection("admin")
                .document(adminDocId)
                .collection("SharingPrompt")
                .orderBy("Rating", Query.Direction.DESCENDING)
                .limit(50) // utk sementara 50
                .get()
                .await()
            mapSnapshotToPromptList(snapshot)
        } catch (e: Exception){
            e.printStackTrace()
            emptyList()
        }
    }

    //function get prompt by main category
    suspend fun getPromptsByCategory(category: String): List<Prompt> {
        return try {
            val snapshot = firestore.collection("admin")
                .document(adminDocId)
                .collection("SharingPrompt")
                .whereEqualTo("MainKategori", category)
                .orderBy("Tanggal", Query.Direction.DESCENDING)
                .get()
                .await()

            mapSnapshotToPromptList(snapshot)
        } catch (e: Exception) {
            e.printStackTrace()
            android.util.Log.e("PromptRepository", "Error getting category prompts: ${e.message}")
            emptyList()
        }
    }

    // Fungsi untuk menghitung jumlah prompt yang dibagikan (sharing)
    suspend fun getSharedPromptCount(): Int {
        return try {
            val uid = auth.currentUser?.uid ?: return 0

            // Kita query ke koleksi PrivatePrompt milik user
            // Dimana field "Status" == "sharing"
            val snapshot = firestore.collection("pengguna")
                .document(uid)
                .collection("PrivatePrompt")
                .whereEqualTo("Status", "sharing")
                .get()
                .await()

            // Kembalikan jumlah dokumen yang ditemukan
            snapshot.size()
        } catch (e: Exception) {
            e.printStackTrace()
            0
        }
    }

    // Fungsi untuk menghitung jumlah prompt yang disimpan (saved)
    suspend fun getSavedPromptCount(): Int {
        return try {
            val uid = auth.currentUser?.uid ?: return 0

            // Query ke collection "SavedPrompt" milik user
            val snapshot = firestore.collection("pengguna")
                .document(uid)
                .collection("SavedPrompt") // Pastikan nama collection di DB sesuai ini
                .get()
                .await()

            snapshot.size() // Kembalikan jumlah dokumen
        } catch (e: Exception) {
            e.printStackTrace()
            0
        }
    }

    //function search prompt
    suspend fun searchPrompts(query: String, filters: List<String>): List<Prompt> {
        return try {
            val collectionRef = firestore.collection("admin")
                .document(adminDocId)
                .collection("SharingPrompt")
            //get data
            val snapshot = collectionRef
                .orderBy("Tanggal", Query.Direction.DESCENDING)
                .limit(100) //ambil 100 terbaru
                .get()
                .await()

            var results = mapSnapshotToPromptList(snapshot)
            if (query.isNotEmpty()) {
                val cleanQuery = query.trim().lowercase() //huruf kecil

                results = results.filter { prompt ->
                    val title = prompt.title.lowercase()
                    val isMatch = title.contains(cleanQuery)//Cek Case Insensitive
                    //cek typo fuzzy
                    val isFuzzy = if (!isMatch && cleanQuery.length > 3) {
                        hasTypoMatch(title, cleanQuery)
                    } else {
                        false
                    }

                    isMatch || isFuzzy
                }
            }

            if (filters.isNotEmpty()) { // filter kategori
                results = results.filter { prompt ->
                    prompt.subCategories.any { it in filters }
                }
            }

            results
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    //algoritma deteksi typo
    private fun hasTypoMatch(text: String, query: String): Boolean {
        // cek kata dalam judul
        val words = text.split(" ")
        for (word in words) {
            if (calculateLevenshteinDistance(word, query) <= 2) { // Jika perbedaan huruf antar judul dan query <=2
                return true
            }
        }
        return false
    }

    //looping utk hitung jarak levenshtein
    private fun calculateLevenshteinDistance(s1: String, s2: String): Int {
        val dp = Array(s1.length + 1) { IntArray(s2.length + 1) }

        for (i in 0..s1.length) {
            for (j in 0..s2.length) {
                if (i == 0) {
                    dp[i][j] = j
                } else if (j == 0) {
                    dp[i][j] = i
                } else {
                    dp[i][j] = min(
                        dp[i - 1][j - 1] + costOfSubstitution(s1[i - 1], s2[j - 1]),
                        min(dp[i - 1][j] + 1, dp[i][j - 1] + 1)
                    )
                }
            }
        }
        return dp[s1.length][s2.length]
    }

    private fun costOfSubstitution(a: Char, b: Char): Int {
        return if (a == b) 0 else 1
    }

    //function toggle bookmark
    suspend fun toggleBookmark(prompt: Prompt): String {
        return withContext(Dispatchers.IO) {
            try {
                val uid = auth.currentUser?.uid ?: throw Exception("User belum login")

                val savedRef = firestore.collection("pengguna")
                    .document(uid)
                    .collection("SavedPrompt") // Collection Khusus Bookmark
                    .document(prompt.id) // Pakai ID yang sama dengan aslinya

                val snapshot = savedRef.get().await()

                if (snapshot.exists()) {
                    // KASUS 1: Sudah disimpan -> HAPUS (Unbookmark)
                    savedRef.delete().await()
                    "Dihapus dari simpanan"
                } else {
                    // KASUS 2: Belum disimpan -> SIMPAN (Copy Data)
                    // Kita simpan objek prompt apa adanya
                    savedRef.set(prompt).await()
                    "Berhasil disimpan"
                }
            } catch (e: Exception) {
                e.printStackTrace()
                "Gagal mengubah status simpan"
            }
        }
    }



    //map helper
    private fun mapSnapshotToPromptList(snapshot: com.google.firebase.firestore.QuerySnapshot): List<Prompt> {
        return snapshot.documents.map { doc -> mapDocumentToPrompt(doc) }
    }

    private fun mapDocumentToPrompt(doc: com.google.firebase.firestore.DocumentSnapshot): Prompt {
        fun getField(vararg keys: String): String {
            for (key in keys) {
                val value = doc.get(key)
                if (value != null) return value.toString()
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
            category = getField("KategoriUtama", "MainKategori"),
            subCategories = getListField("KategoriUmum", "SubKategori"),
            aiModel = getField("ModelAi", "ModelAI"),
            modelVersion = getField("VersiModelAi", "VersiModelAI"),
            content = getField("Prompt"),
            rating = getField("Rating").ifEmpty { "New" },
            status = getField("Status"),
            createdAt = doc.getDate("Tanggal"),
            usageCount = doc.getLong("UsageCount")?.toInt() ?: 0,
            userId = getField("UserId"),
            username = getField("Username", "UserEmail")
        )
    }






}