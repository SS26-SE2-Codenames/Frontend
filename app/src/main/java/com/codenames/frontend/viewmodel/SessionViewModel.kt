package com.codenames.frontend.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.codenames.frontend.data.model.SessionState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class SessionViewModel @Inject constructor(): ViewModel() {
    private val _username = MutableStateFlow(SessionState(""))
    val username: StateFlow<SessionState> = _username

    fun setUsername(username: String) {
        _username.update { SessionState(username) }
    }
}
