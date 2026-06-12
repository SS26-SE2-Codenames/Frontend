package com.codenames.frontend.data.model

sealed class RejoinState {
    data object None: RejoinState()
    data class Available(val sessionState: SessionState): RejoinState()
    data object Consumed : RejoinState()
}
