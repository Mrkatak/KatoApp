package com.example.katoapp.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import java.io.ByteArrayOutputStream
import java.io.InputStream

object ImageUtils {

    // Fungsi untuk mengompres URI Gambar menjadi ByteArray
    fun compressImage(context: Context, imageUri: Uri): ByteArray? {
        return try {
            // 1. Baca gambar dari URI
            val inputStream: InputStream? = context.contentResolver.openInputStream(imageUri)
            val originalBitmap = BitmapFactory.decodeStream(inputStream)
            inputStream?.close()

            if (originalBitmap == null) return null

            // 2. Resize Gambar (Opsional tapi sangat disarankan)
            // Jika gambar terlalu lebar (misal > 1080px), kita kecilkan biar ringan
            val maxDimension = 1080
            val ratio = originalBitmap.width.toFloat() / originalBitmap.height.toFloat()

            val width: Int
            val height: Int

            if (originalBitmap.width > originalBitmap.height) {
                // Landscape
                width = if (originalBitmap.width > maxDimension) maxDimension else originalBitmap.width
                height = (width / ratio).toInt()
            } else {
                // Portrait
                height = if (originalBitmap.height > maxDimension) maxDimension else originalBitmap.height
                width = (height * ratio).toInt()
            }

            val resizedBitmap = Bitmap.createScaledBitmap(originalBitmap, width, height, true)

            // 3. Kompresi ke JPEG dengan Kualitas 60-70%
            val outputStream = ByteArrayOutputStream()
            // CompressFormat.JPEG membuat ukuran file jauh lebih kecil
            // Quality 60 sudah sangat cukup untuk layar HP
            resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 60, outputStream)

            val result = outputStream.toByteArray()

            // Bersihkan memori
            if (originalBitmap != resizedBitmap) {
                originalBitmap.recycle()
            }
            resizedBitmap.recycle()

            result
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}