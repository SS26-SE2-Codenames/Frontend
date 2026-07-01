package com.codenames.frontend.network.provider

import com.codenames.frontend.BuildConfig

fun getHttpUrl() = "${BuildConfig.HTTP_SERVER_URL}/"

fun getWsUrl() = "${BuildConfig.SERVER_URL}/ws-fallback"
