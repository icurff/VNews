package com.example.vnews.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {

        return Retrofit.Builder()
            .baseUrl("https://raw.githubusercontent.com")
            .addConverterFactory(
                Json.asConverterFactory(
                    "application/json; charset=UTF8".toMediaType()
                )
            )
            .build()
    }

//    @Provides
//    @Singleton
//    fun provideRepoApiService(retrofit: Retrofit): RepoApiService {
//        return retrofit.create(RepoApiService::class.java)
//    }
//    @Provides
//    @Singleton
//    fun provideExtensionApiService(retrofit: Retrofit): ExtensionApiService {
//        return retrofit.create(ExtensionApiService::class.java)
//    }

}