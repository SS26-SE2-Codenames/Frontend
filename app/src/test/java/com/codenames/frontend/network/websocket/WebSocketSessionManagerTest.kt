package com.codenames.frontend.network.websocket

import android.util.Log
import com.codenames.frontend.network.dto.GameMessage
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.test.runTest
import org.hildan.krossbow.stomp.StompClient
import org.hildan.krossbow.stomp.StompSession
import org.hildan.krossbow.stomp.conversions.kxserialization.StompSessionWithKxSerialization
import org.hildan.krossbow.stomp.conversions.kxserialization.json.withJsonConversions
import org.junit.Assert.assertThrows
import org.junit.Before
import org.junit.Test
import kotlin.test.assertNotNull

class WebSocketSessionManagerTest {

    private lateinit var client: StompClient
    private lateinit var session: StompSessionWithKxSerialization
    private lateinit var rawSession: StompSession

    @Before
    fun setup() {
        client = mockk()
        session = mockk(relaxed = true)
        rawSession = mockk(relaxed = true)

        mockkStatic(Log::class)
        every { Log.d(any(), any()) } returns 0
        every { Log.e(any(), any()) } returns 0
        every { Log.e(any(), any(), any()) } returns 0
    }

    @Test
    fun testConnectStomp() =
        runTest {
            val sessionWithJson = mockk<StompSessionWithKxSerialization>()

            coEvery { client.connect(URL) } returns session

            coEvery {
                sessionWithJson.subscribe<GameMessage>(any(), any())
            } returns emptyFlow()

            val wsClient = WebSocketSessionManager(client)

            wsClient.connectStomp()

            coVerify {
                client.connect(BASE_URL)
                session.withJsonConversions()
            }
        }

    @Test
    fun testConnectStomp_throwsExceptionWhenConnectionFails() =
        runTest {
            coEvery { client.connect(URL) } throws Exception("Connection failed")

            val wsClient = WebSocketSessionManager(client)

            assertThrows(Exception::class.java) {
                runTest {
                    wsClient.connectStomp()
                }
            }
        }

    @Test
    fun testConnectStomp_DoesNotConnectWhenSessionExists() =
        runTest {
            val wsClient = WebSocketSessionManager(client)
            coEvery { client.connect(URL) } returns session
            wsClient.connectStomp()
            wsClient.connectStomp()
            coVerify(exactly = 1) { client.connect(BASE_URL) }
        }

    @Test
    fun testDisconnectStomp() =
        runTest {
            val wsClient = WebSocketSessionManager(client)

            coEvery { client.connect(URL) } returns session

            wsClient.connectStomp()
            wsClient.disconnect()

            coVerify {
                session.disconnect()
            }
        }

    @Test
    fun testDisconnectStomp_doesNothingWhenSessionDoesNotExist() =
        runTest {
            val wsClient = WebSocketSessionManager(client)

            wsClient.disconnect()

            coVerify(exactly = 0) {
                session.disconnect()
            }
        }

    @Test
    fun testIsConnected_returnsTrueWhenSessionExists() =
        runTest {
            val wsClient = WebSocketSessionManager(client)

            coEvery { client.connect(URL) } returns session

            wsClient.connectStomp()

            assert(wsClient.isConnected())
        }

    @Test
    fun testIsConnected_returnsFalseWhenSessionDoesNotExist() =
        runTest {
            val wsClient = WebSocketSessionManager(client)

            assert(!wsClient.isConnected())
        }

    @Test
    fun testGetSession_returnsSession() = runTest {
        val wsClient = WebSocketSessionManager(client)

        coEvery { client.connect(URL) } returns session

        wsClient.connectStomp()

        assertNotNull(wsClient.getSession())
    }

    @Test
    fun testGetSession_throwsWhenSessionIsNull() =
        runTest {
            val wsClient = WebSocketSessionManager(client)

            assertThrows(IllegalArgumentException::class.java) { wsClient.getSession() }
        }
}