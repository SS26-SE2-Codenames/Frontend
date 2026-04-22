package com.codenames.frontend.network.provider

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton
import org.hildan.krossbow.stomp.StompClient
import org.hildan.krossbow.websocket.okhttp.OkHttpWebSocketClient

@Module
@InstallIn(SingletonComponent::class)
object WebSocketProvider {
    @Provides
    @Singleton
    fun provideStompClient(): StompClient = StompClient(OkHttpWebSocketClient())
}
