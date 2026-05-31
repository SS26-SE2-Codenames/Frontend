package com.codenames.frontend.network.provider

import com.codenames.frontend.BuildConfig

fun getHttpUrl() = "http://" + BuildConfig.SERVER_URL

fun getWsUrl() = "ws://" + BuildConfig.SERVER_URL + "ws-fallback"
