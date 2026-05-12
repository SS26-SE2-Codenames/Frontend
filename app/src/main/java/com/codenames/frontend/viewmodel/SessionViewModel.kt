package com.codenames.frontend.viewmodel

import androidx.lifecycle.ViewModel
import com.codenames.frontend.data.model.SessionState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class SessionViewModel : ViewModel() {
    private val _username = MutableStateFlow(SessionState(""))
    val username: StateFlow<SessionState> = _username

    fun setUsername(username: String) {
        _username.update { SessionState(username) }
    }
}
