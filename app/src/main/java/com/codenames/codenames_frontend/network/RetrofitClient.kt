package com.codenames.codenames_frontend.network

import com.codenames.codenames_frontend.network.api.LobbyApi
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit

object RetrofitClient {

    private val json = Json {
        ignoreUnknownKeys = true
    }

    //TODO: adapt URL and put in .env file
    val api: LobbyApi = Retrofit.Builder()
        .baseUrl("http://localhost:8080")
        .addConverterFactory(
            json.asConverterFactory("application/json".toMediaType())
        )
        .build()
        .create(LobbyApi::class.java)
}