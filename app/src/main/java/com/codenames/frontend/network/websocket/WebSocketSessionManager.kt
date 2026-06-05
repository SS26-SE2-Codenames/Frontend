package com.codenames.frontend.network.websocket

import android.util.Log
import com.codenames.frontend.network.provider.getWsUrl
import jakarta.inject.Inject
import jakarta.inject.Singleton
import org.hildan.krossbow.stomp.StompClient
import org.hildan.krossbow.stomp.conversions.kxserialization.StompSessionWithKxSerialization
import org.hildan.krossbow.stomp.conversions.kxserialization.json.withJsonConversions

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
                session = client.connect(getWsUrl()).withJsonConversions()
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
