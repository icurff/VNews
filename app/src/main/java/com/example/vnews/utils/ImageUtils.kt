package com.example.vnews.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import com.example.vnews.service.ImgBBApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

object ImageUtils {
    private const val IMAGE_QUALITY = 80
    private const val MAX_IMAGE_SIZE = 2048 // Max dimension for resize


    suspend fun uploadImage(context: Context, imageUri: Uri): String? =
        withContext(Dispatchers.IO) {
            try {
                val tempFile = processImage(context, imageUri)

                // Create a multipart request
                val requestFile = tempFile.asRequestBody("image/*".toMediaTypeOrNull())
                val imagePart =
                    MultipartBody.Part.createFormData("image", tempFile.name, requestFile)

                // Upload to ImgBB
                val response = ImgBBApi.service.uploadImage(image = imagePart)

                // Clean up
                tempFile.delete()

                if (response.isSuccessful && response.body() != null) {
                    val imgResponse = response.body()!!
                    if (imgResponse.success) {
                        return@withContext imgResponse.data.url
                    }
                }
                return@withContext null
            } catch (e: Exception) {
                return@withContext null
            }
        }

    private suspend fun processImage(context: Context, imageUri: Uri): File =
        withContext(Dispatchers.IO) {
            // temp file with random name
            val tempFile = File.createTempFile("upload_", ".jpg", context.cacheDir)

            // Load the bitmap
            val inputStream: InputStream = context.contentResolver.openInputStream(imageUri)!!
            val bitmap = BitmapFactory.decodeStream(inputStream)
            inputStream.close()
            val resizedBitmap = resizeBitmap(bitmap)

            // Compress
            FileOutputStream(tempFile).use { output ->
                resizedBitmap.compress(Bitmap.CompressFormat.JPEG, IMAGE_QUALITY, output)
            }

            // release memory
            if (bitmap != resizedBitmap) {
                resizedBitmap.recycle()
            }
            bitmap.recycle()

            return@withContext tempFile
        }

        private fun resizeBitmap(bitmap: Bitmap): Bitmap {
        val width = bitmap.width
        val height = bitmap.height

        if (width <= MAX_IMAGE_SIZE && height <= MAX_IMAGE_SIZE) {
            return bitmap
        }

        val ratio = width.toFloat() / height.toFloat()
        val newWidth: Int
        val newHeight: Int

        if (width > height) {
            newWidth = MAX_IMAGE_SIZE
            newHeight = (newWidth / ratio).toInt()
        } else {
            newHeight = MAX_IMAGE_SIZE
            newWidth = (newHeight * ratio).toInt()
        }

        return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
    }
} 