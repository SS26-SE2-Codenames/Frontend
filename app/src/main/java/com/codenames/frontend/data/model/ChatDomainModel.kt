package com.codenames.frontend.data.model

import com.codenames.frontend.data.model.enums.CheatExposureResult

data class ChatDomainModel(
    val sender: String,
    val text: String,
    val isFromMe: Boolean,
    val cheatExposureResult: CheatExposureResult? = null,
)
