package com.example.vnews.data.remote

import com.example.vnews.data.remote.dto.RssSource
import retrofit2.http.GET
import retrofit2.http.Url

interface RepoApiService {
    @GET
    suspend fun getExtensionListFromRepo(@Url repoUrl: String): List<RssSource>
}