package com.codenames.frontend.network.provider

import com.codenames.frontend.network.api.LobbyApi
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit

const val BASE_URL = "http://localhost:8080/lobby"

@Module
@InstallIn(SingletonComponent::class)
object RetrofitProvider {
    @Provides
    fun provideJson(): Json =
        Json {
            ignoreUnknownKeys = true
        }

    @Provides
    fun provideRetrofit(json: Json): Retrofit =
        Retrofit
            .Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(
                json.asConverterFactory("application/json".toMediaType()),
            ).build()

    @Provides
    fun provideLobbyApi(retrofit: Retrofit): LobbyApi = retrofit.create(LobbyApi::class.java)
}
