package com.codenames.frontend.network.websocket

import android.util.Log
import jakarta.inject.Inject
import jakarta.inject.Singleton
import org.hildan.krossbow.stomp.StompClient
import org.hildan.krossbow.stomp.conversions.kxserialization.StompSessionWithKxSerialization
import org.hildan.krossbow.stomp.conversions.kxserialization.json.withJsonConversions

const val URL = "ws://192.168.0.134:8080/ws-fallback"

@Singleton
class WebSocketSessionManager
    @Inject
    constructor(
        val client: StompClient,
    ) {
        private var session: StompSessionWithKxSerialization? = null

        suspend fun connectStomp() {
            if (isConnected()) return
            try {
                session = client.connect(URL).withJsonConversions()
            } catch (e: Exception) {
                Log.e("WebSocket", "Failed to connect to Websocket", e)
                return
            }
            Log.d("WebSocket", "Connected to Websocket, session: $session")
        }

        fun isConnected(): Boolean = session != null

        fun getSession(): StompSessionWithKxSerialization =
            requireNotNull(session) {
                "WebSocket is not connected"
            }

        suspend fun disconnect() {
            session?.disconnect()
            session = null
        }
    }
