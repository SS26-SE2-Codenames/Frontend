package com.codenames.frontend.data.error

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

class ErrorMessageMapperTest {
    @Test
    fun toUserMessage_httpExceptionWithBackendMessage_returnsBackendMessage() {
        val errorBody =
            """
            {
              "message": "Could not find lobby.",
              "lobbyCode": "ABCDE",
              "playerList": null,
              "isStarted": false
            }
            """.trimIndent()
                .toResponseBody("application/json".toMediaType())

        val exception = HttpException(Response.error<Any>(400, errorBody))

        val result = ErrorMessageMapper.toUserMessage(exception)

        assertEquals("Could not find lobby.", result)
    }

    @Test
    fun toUserMessage_badRequestWithoutBackendMessage_returnsUserFriendlyMessage() {
        val exception =
            HttpException(
                Response.error<Any>(
                    400,
                    "{}".toResponseBody("application/json".toMediaType()),
                ),
            )

        val result = ErrorMessageMapper.toUserMessage(exception)

        assertEquals(
            "The request could not be processed. Please check your input.",
            result,
        )
    }

    @Test
    fun toUserMessage_notFoundWithoutBackendMessage_returnsUserFriendlyMessage() {
        val exception =
            HttpException(
                Response.error<Any>(
                    404,
                    "{}".toResponseBody("application/json".toMediaType()),
                ),
            )

        val result = ErrorMessageMapper.toUserMessage(exception)

        assertEquals(
            "The requested lobby could not be found.",
            result,
        )
    }

    @Test
    fun toUserMessage_serverErrorWithoutBackendMessage_returnsUserFriendlyMessage() {
        val exception =
            HttpException(
                Response.error<Any>(
                    500,
                    "{}".toResponseBody("application/json".toMediaType()),
                ),
            )

        val result = ErrorMessageMapper.toUserMessage(exception)

        assertEquals(
            "The server has a problem right now. Please try again later.",
            result,
        )
    }

    @Test
    fun toUserMessage_ioException_returnsConnectionMessage() {
        val result = ErrorMessageMapper.toUserMessage(IOException())

        assertEquals(
            "Could not connect to the server. Please check your connection.",
            result,
        )
    }

    @Test
    fun toUserMessage_exceptionWithMessage_returnsMessage() {
        val result = ErrorMessageMapper.toUserMessage(RuntimeException("Network error"))

        assertEquals("Network error", result)
    }

    @Test
    fun toUserMessage_exceptionWithoutMessage_returnsGenericMessage() {
        val result = ErrorMessageMapper.toUserMessage(RuntimeException())

        assertEquals(
            "Something went wrong. Please try again.",
            result,
        )
    }
}
