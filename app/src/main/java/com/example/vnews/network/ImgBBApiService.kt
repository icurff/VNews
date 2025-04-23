package com.example.vnews.network

import com.squareup.moshi.Json
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

// API Response data classes
data class ImgBBResponse(
    val data: ImgBBData,
    val success: Boolean,
    val status: Int
)

data class ImgBBData(
    val id: String,
    val title: String,
    @Json(name = "url_viewer") val urlViewer: String,
    val url: String,
    @Json(name = "display_url") val displayUrl: String,
    val width: String,
    val height: String,
    val size: String,
    val time: String,
    val expiration: String,
    val image: ImgBBImage,
    val thumb: ImgBBImage,
    val medium: ImgBBImage,
    @Json(name = "delete_url") val deleteUrl: String
)

data class ImgBBImage(
    val filename: String,
    val name: String,
    val mime: String,
    val extension: String,
    val url: String
)

// API Service interface
interface ImgBBApiService {
    @Multipart
    @POST("1/upload")
    suspend fun uploadImage(
        @Query("key") apiKey: String = "secretkey",
        @Part image: MultipartBody.Part
    ): Response<ImgBBResponse>
}

// Singleton object to provide the API service
object ImgBBApi {
    private const val BASE_URL = "https://api.imgbb.com/"

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()

    val service: ImgBBApiService by lazy {
        retrofit.create(ImgBBApiService::class.java)
    }
} 