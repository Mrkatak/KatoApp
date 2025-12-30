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
                "UsageCount" to 0,
                "TotalRatingValue" to 0.0,
                "RatingCount" to 0
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

    //fun get latest prompt
    suspend fun getLatestPrompts(): List<Prompt> {
        return try {
            val snapshot = firestore.collection("admin")
                .document(adminDocId)
                .collection("SharingPrompt")
                .orderBy("Tanggal", Query.Direction.DESCENDING)
                .limit(50)
                .get()
                .await()
            mapSnapshotToPromptList(snapshot)
        } catch (e: Exception) {
            emptyList()
        }
    }

    //function search prompt
    suspend fun searchPrompts(rawQuery: String): List<Prompt> {
        return try {
            //get data
            val snapshot = firestore.collection("admin")
                .document(adminDocId)
                .collection("SharingPrompt")
                .orderBy("Tanggal", Query.Direction.DESCENDING)
                .limit(100)
                .get()
                .await()

            val allPrompts = mapSnapshotToPromptList(snapshot)
            // parsing query
            val lowerQuery = rawQuery.lowercase().trim()

            var targetMainCategory = ""
            var contentQuery = lowerQuery

            //deteksi keyword (mapping manual id -> en)
            if (lowerQuery.contains("gambar") || lowerQuery.contains("image") || lowerQuery.contains("lukisan")) {
                targetMainCategory = "Text to Image"
                contentQuery = lowerQuery.replace("gambar", "").replace("image", "").replace("lukisan", "").trim()
            } else if (lowerQuery.contains("video") || lowerQuery.contains("film")) {
                targetMainCategory = "Text to Video"
                contentQuery = lowerQuery.replace("video", "").replace("film", "").trim()
            } else if (lowerQuery.contains("suara") || lowerQuery.contains("audio") || lowerQuery.contains("musik")) {
                targetMainCategory = "Text to Speech"
                contentQuery = lowerQuery.replace("suara", "").replace("audio", "").replace("musik", "").trim()
            } else if (lowerQuery.contains("teks") || lowerQuery.contains("text") || lowerQuery.contains("tulisan")) {
                targetMainCategory = "Text to Text"
                contentQuery = lowerQuery.replace("teks", "").replace("text", "").replace("tulisan", "").trim()
            }

            //filtering
            allPrompts.filter { prompt ->
                //cek kategori di query
                val matchCategory = if (targetMainCategory.isNotEmpty()) {
                    prompt.category == targetMainCategory
                } else {
                    true
                }

                //cek judul (fuzzy matching)
                val title = prompt.title.lowercase()
                val matchTitle = if (contentQuery.isNotEmpty()) {
                    val isMatch = title.contains(contentQuery)
                    val isFuzzy = if (!isMatch && contentQuery.length > 3) hasTypoMatch(title, contentQuery) else false
                    isMatch || isFuzzy
                } else {
                    true
                }

                //cek general category
                val tags = prompt.subCategories.map { it.lowercase() }
                val matchTags = if (contentQuery.isNotEmpty()) {
                    tags.any { it.contains(contentQuery) }
                } else {
                    false
                }

                matchCategory && (matchTitle || matchTags)
            }

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
                    savedRef.delete().await() //hapus dari bookmark
                    "Dihapus dari simpanan"
                } else {
                    savedRef.set(prompt).await() //tambahkan ke bookmark
                    "Berhasil disimpan"
                }
            } catch (e: Exception) {
                e.printStackTrace()
                "Gagal mengubah status simpan"
            }
        }
    }

    //fun update prompt rating
    suspend fun updatePromptRating(prompt: Prompt, userRating: Int) {
        withContext(Dispatchers.IO) {
            firestore.runTransaction { transaction -> //transaction untuk hitung rating
                //dari admin
                val adminRef = firestore.collection("admin")
                    .document(adminDocId)
                    .collection("SharingPrompt")
                    .document(prompt.id)

                val snapshot = transaction.get(adminRef)
                //ambil data lama atau 0
                val currentTotalValue = snapshot.getDouble("TotalRatingValue") ?: 0.0
                val currentCount = snapshot.getLong("RatingCount") ?: 0

                //rumus rata - rata
                val newTotalValue = currentTotalValue + userRating
                val newCount = currentCount + 1
                val newAverage = newTotalValue / newCount
                //format to string
                val newAverageString = String.format("%.1f", newAverage)

                //update ke admin
                transaction.update(adminRef, "TotalRatingValue", newTotalValue)
                transaction.update(adminRef, "RatingCount", newCount)
                transaction.update(adminRef, "Rating", newAverageString)

                // update ke pengguna
                val userRef = firestore.collection("pengguna")
                    .document(prompt.userId)
                    .collection("PrivatePrompt")
                    .document(prompt.id)

                transaction.update(userRef, "Rating", newAverageString)
                transaction.update(userRef, "TotalRatingValue", newTotalValue)
                transaction.update(userRef, "RatingCount", newCount)

            }.await()
        }
    }

    //function update prompt usage count
    suspend fun incrementUsageCount(prompt: Prompt) {
        withContext(Dispatchers.IO) {
            val batch = firestore.batch()

            // Update Admin
            if (prompt.status == "sharing") {
                val adminRef = firestore.collection("admin")
                    .document(adminDocId)
                    .collection("SharingPrompt")
                    .document(prompt.id)
                // Atomic Increment
                batch.update(
                    adminRef,
                    "UsageCount",
                    FieldValue.increment(1)
                )
            }

            // Update User
            val userRef = firestore.collection("pengguna")
                .document(prompt.userId)
                .collection("PrivatePrompt")
                .document(prompt.id)

            batch.update(
                userRef,
                "UsageCount",
                FieldValue.increment(1)
            )
            batch.commit().await()
        }
    }

    //fun update prompt
    suspend fun updatePrompt(
        promptId: String,
        title: String, content: String, mainCategory: String,
        subCategories: List<String>, aiModel: String, modelVersion: String,
        imageUri: Uri?, isSharing: Boolean,
        currentImageUrl: String
    ) {
        withContext(Dispatchers.IO) {
            val currentUser = auth.currentUser ?: throw Exception("User belum login")

            //jika update gambar
            var finalImageUrl = currentImageUrl
            if (imageUri != null) {
                //upload URI to claudinary
                val uploadResult = cloudinaryHelper.uploadImage(imageUri).first { it !is ResourceCloudinary.Loading }
                if (uploadResult is ResourceCloudinary.Success) {
                    finalImageUrl = uploadResult.data
                }
            }

            val updateData = hashMapOf<String, Any>(
                "Judul" to title,
                "Prompt" to content,
                "KategoriUtama" to mainCategory,
                "KategoriUmum" to subCategories,
                "ModelAi" to aiModel,
                "VersiModelAi" to modelVersion,
                "LinkGambar" to finalImageUrl,
                "Status" to if (isSharing) "sharing" else "private",
            )

            val batch = firestore.batch()

            //update di pengguna
            val privateRef = firestore.collection("pengguna")
                .document(currentUser.uid)
                .collection("PrivatePrompt")
                .document(promptId)

            batch.update(privateRef, updateData)

            // B. Logic Sharing (Admin)
            val adminRef = firestore.collection("admin")
                .document(adminDocId)
                .collection("SharingPrompt")
                .document(promptId)

            if (isSharing) {
                //jika sharing tambahkan prompt ke admin
                val fullData = updateData.toMutableMap()
                fullData["UserId"] = currentUser.uid
                fullData["Username"] = currentUser.displayName ?: "User"
                fullData["UserEmail"] = currentUser.email ?: ""
                fullData["Rating"] = "0.0"
                fullData["UsageCount"] = 0
                fullData["Tanggal"] = FieldValue.serverTimestamp()
                batch.set(adminRef, fullData, com.google.firebase.firestore.SetOptions.merge())
            } else {
                batch.delete(adminRef)
            }

            batch.commit().await()
        }
    }

    //function get reported prompt
    suspend fun getReportedPrompts(): List<Prompt> {
        return try {
            val snapshot = firestore.collection("admin")
                .document(adminDocId)
                .collection("ReportedPrompt")
                .orderBy("TanggalLaporan", Query.Direction.ASCENDING)
                .get()
                .await()
            mapSnapshotToPromptList(snapshot)
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    //function get all prompt in admin
    suspend fun getAllSharingPrompts(): List<Prompt> {
        return try {
            val snapshot = firestore.collection("admin")
                .document(adminDocId)
                .collection("SharingPrompt")
                .orderBy("Tanggal", Query.Direction.DESCENDING)
                .get()
                .await()
            mapSnapshotToPromptList(snapshot)
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    //function report prompt
    suspend fun reportPrompt(prompt: Prompt, reason: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val currentUser = auth.currentUser ?: return@withContext false

                // UPDATE: Gunakan nama field yang SAMA dengan Prompt biasa
                // agar bisa dibaca oleh mapDocumentToPrompt nanti
                val reportData = hashMapOf(
                    // Info Prompt Asli
                    "id" to prompt.id, // Simpan ID Asli di dalam field
                    "Judul" to prompt.title,
                    "Prompt" to prompt.content,
                    "LinkGambar" to prompt.imageUrl, // Samakan nama field gambar
                    "KategoriUtama" to prompt.category,
                    "Rating" to prompt.rating,

                    // Info Pemilik Prompt
                    "UserId" to prompt.userId, // ID Pemilik

                    // Info Pelapor
                    "ReporterId" to currentUser.uid,
                    "ReporterName" to (currentUser.displayName ?: "User"),
                    "Reason" to reason,

                    "StatusLaporan" to "Pending",
                    "TanggalLaporan" to FieldValue.serverTimestamp() // Untuk sorting laporan
                )

                // Simpan ke Admin -> ReportedPrompt
                firestore.collection("admin")
                    .document(adminDocId)
                    .collection("ReportedPrompt")
                    .add(reportData)
                    .await()

                true
            } catch (e: Exception) {
                e.printStackTrace()
                false
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
        val originalId = doc.getString("id") ?: doc.getString("PromptId") ?: doc.id
        return Prompt(
//            id = doc.id,
            id = originalId,
            title = getField("Judul"),
            imageUrl = getField("LinkGambar"),
            category = getField("KategoriUtama", "MainKategori"),
            subCategories = getListField("KategoriUmum", "SubKategori"),
            aiModel = getField("ModelAi", "ModelAI"),
            modelVersion = getField("VersiModelAi", "VersiModelAI"),
            content = getField("Prompt"),
            rating = getField("Rating").ifEmpty { "New" },
            status = getField("Status"),
//            createdAt = doc.getDate("Tanggal"),
            createdAt = doc.getDate("Tanggal") ?: doc.getDate("TanggalLaporan"),
            usageCount = doc.getLong("UsageCount")?.toInt() ?: 0,
            userId = getField("UserId"),
            username = getField("Username", "UserEmail")
        )
    }






}