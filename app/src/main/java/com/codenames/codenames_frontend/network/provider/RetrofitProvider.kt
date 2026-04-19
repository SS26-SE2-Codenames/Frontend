package com.codenames.codenames_frontend.network.provider

import com.codenames.codenames_frontend.network.api.LobbyApi
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
object RetrofitProvider{

    @Provides
    fun provideJson(): Json {
        return Json {
            ignoreUnknownKeys = true
        }
    }

    @Provides
    fun provideRetrofit(json: Json) : Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(
                json.asConverterFactory("application/json".toMediaType())
            )
            .build()
    }

    @Provides
    fun provideLobbyApi(retrofit: Retrofit): LobbyApi {
        return retrofit.create(LobbyApi::class.java)
    }
}