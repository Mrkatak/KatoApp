package com.example.katoapp.data.remote

import android.content.Context
import android.net.Uri
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.example.katoapp.utils.ImageUtils
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CloudinaryHelper @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private val myCloudName = "der6c348w"
    private val myUploadPreset = "kato_preset"

    private fun initMediaManager() {
        try {
            val config = HashMap<String, String>()
            config["cloud_name"] = myCloudName
            config["secure"] = "true"
            MediaManager.init(context, config)
        } catch (e: Exception) {

        }
    }

    fun uploadImage(imageUri: Uri): Flow<ResourceCloudinary<String>> = callbackFlow {
        initMediaManager()
        trySend(ResourceCloudinary.Loading)

        //operasi dipindahkan ke IO Thread
        //kompres 100 ke 60
        val compressedImageBytes = withContext(Dispatchers.IO) {
            ImageUtils.compressImage(context, imageUri)
        }
        //jika kompresi gagal
        if (compressedImageBytes == null) {
            trySend(ResourceCloudinary.Error("Gagal memproses gambar (Kompresi error)"))
            close()
            return@callbackFlow
        }

        //upload byte array ke Cloudinary
        val requestId = MediaManager.get().upload(compressedImageBytes)
            .unsigned(myUploadPreset)
            .option("resource_type", "image")
            .option("folder", "kato_prompts")
            .callback(object : UploadCallback {

                override fun onStart(requestId: String) {
                    //
                }

                override fun onProgress(requestId: String, bytes: Long, totalBytes: Long) {
                    //harusnya logika progresbar
                }

                override fun onSuccess(requestId: String, resultData: Map<*, *>) {
                    //get url https
                    val secureUrl = resultData["secure_url"] as? String ?: ""
                    if (secureUrl.isNotEmpty()) {
                        trySend(ResourceCloudinary.Success(secureUrl))
                    } else {
                        trySend(ResourceCloudinary.Error("Gagal mendapatkan URL gambar"))
                    }
                    close() // tutup flow
                }

                override fun onError(requestId: String, error: ErrorInfo) {
                    trySend(ResourceCloudinary.Error("Upload Gagal: ${error.description}"))
                    close()
                }

                override fun onReschedule(requestId: String, error: ErrorInfo) {
                    //Handle reschedule
                }
            })
            .dispatch()
        //bersihkan jika coroutine dibatalkan
        awaitClose {
            MediaManager.get().cancelRequest(requestId)
        }
    }.flowOn(Dispatchers.IO)


    // function Upload Gambar tanpa kompres
    //mengembalikan url gambar
//    fun uploadImage(imageUri: Uri): Flow<ResourceCloudinary<String>> = callbackFlow {
//        initMediaManager()
//        trySend(ResourceCloudinary.Loading)
//        val requestId = MediaManager.get().upload(imageUri)
//            .unsigned(myUploadPreset)
//            .option("resource_type", "image")
//            .callback(object : UploadCallback {
//
//                //saat upload dimulai
//                override fun onStart(requestId: String) {
//                }
//                //saat proses berjalan
//                override fun onProgress(requestId: String, bytes: Long, totalBytes: Long) {
//                    // val progress = (bytes.toDouble() / totalBytes) * 100
//                }
//                //upload success
//                override fun onSuccess(requestId: String, resultData: Map<*, *>) {
//                    //get url https
//                    val secureUrl = resultData["secure_url"] as? String ?: ""
//                    if (secureUrl.isNotEmpty()) {
//                        trySend(ResourceCloudinary.Success(secureUrl))
//                    } else {
//                        trySend(ResourceCloudinary.Error("Gagal mendapatkan URL gambar"))
//                    }
//                    close() //tutup flow
//                }
//                //upload gagal
//                override fun onError(requestId: String, error: ErrorInfo) {
//                    trySend(ResourceCloudinary.Error("Upload Gagal: ${error.description}"))
//                    close()
//                }
//                override fun onReschedule(requestId: String, error: ErrorInfo) {
//                }
//            })
//            .dispatch()
//
//        awaitClose {
//            MediaManager.get().cancelRequest(requestId)
//        }
//    }.flowOn(Dispatchers.IO)


}

//class Helper sebagai kamus error
sealed class ResourceCloudinary<out T> {
    data object Loading : ResourceCloudinary<Nothing>()
    data class Success<out T>(val data: T) : ResourceCloudinary<T>()
    data class Error(val message: String) : ResourceCloudinary<Nothing>()
}