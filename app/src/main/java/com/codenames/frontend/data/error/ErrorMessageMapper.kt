package com.codenames.frontend.data.error

import com.codenames.frontend.network.dto.LobbyResponse
import kotlinx.serialization.json.Json
import retrofit2.HttpException
import java.io.IOException

object ErrorMessageMapper {
    private val json =
        Json {
            ignoreUnknownKeys = true
        }

    fun toUserMessage(error: Throwable): String {
        if (error is HttpException) {
            val backendMessage =
                error
                    .response()
                    ?.errorBody()
                    ?.string()
                    ?.let { body ->
                        runCatching {
                            json.decodeFromString<LobbyResponse>(body).message
                        }.getOrNull()
                    }

            if (!backendMessage.isNullOrBlank()) {
                return backendMessage
            }

            return when (error.code()) {
                400 -> "The request could not be processed. Please check your input."
                404 -> "The requested lobby could not be found."
                500 -> "The server has a problem right now. Please try again later."
                else -> "Something went wrong. Please try again."
            }
        }

        if (error is IOException) {
            return "Could not connect to the server. Please check your connection."
        }

        return error.message
            ?.takeIf { it.isNotBlank() }
            ?: "Something went wrong. Please try again."
    }
}
