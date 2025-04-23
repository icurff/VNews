package com.example.vnews.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import com.example.vnews.network.ImgBBApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

object ImageUtils {
    private const val TAG = "ImageUtils"
    private const val IMAGE_QUALITY = 80
    private const val MAX_IMAGE_SIZE = 2048 // Max dimension for resize

    /**
     * Process and upload an image to ImgBB
     * @param context Android context
     * @param imageUri URI of the image to upload
     * @return URL of the uploaded image or null if the upload failed
     */
    suspend fun uploadImage(context: Context, imageUri: Uri): String? =
        withContext(Dispatchers.IO) {
            try {
                // Process the image
                val tempFile = processImage(context, imageUri)

                // Create a multipart request
                val requestFile = tempFile.asRequestBody("image/*".toMediaTypeOrNull())
                val imagePart =
                    MultipartBody.Part.createFormData("image", tempFile.name, requestFile)

                // Upload to ImgBB
                val response = ImgBBApi.service.uploadImage(image = imagePart)

                // Clean up the temp file
                tempFile.delete()

                if (response.isSuccessful && response.body() != null) {
                    val imgbbResponse = response.body()!!
                    if (imgbbResponse.success) {
                        // Return the direct image URL
                        return@withContext imgbbResponse.data.url
                    }
                }

                Log.e(TAG, "Upload failed: ${response.errorBody()?.string()}")
                return@withContext null
            } catch (e: Exception) {
                Log.e(TAG, "Error uploading image", e)
                return@withContext null
            }
        }

    /**
     * Process image to reduce size before upload
     */
    private suspend fun processImage(context: Context, imageUri: Uri): File =
        withContext(Dispatchers.IO) {
            val tempFile = File.createTempFile("upload_", ".jpg", context.cacheDir)

            // Load and optimize the bitmap
            val inputStream: InputStream = context.contentResolver.openInputStream(imageUri)!!
            val bitmap = BitmapFactory.decodeStream(inputStream)
            inputStream.close()

            // Resize if necessary
            val resizedBitmap = resizeBitmapIfNeeded(bitmap)

            // Compress and save to temp file
            FileOutputStream(tempFile).use { output ->
                resizedBitmap.compress(Bitmap.CompressFormat.JPEG, IMAGE_QUALITY, output)
            }

            if (bitmap != resizedBitmap) {
                resizedBitmap.recycle()
            }
            bitmap.recycle()

            return@withContext tempFile
        }

    /**
     * Resize bitmap if it's too large
     */
    private fun resizeBitmapIfNeeded(bitmap: Bitmap): Bitmap {
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