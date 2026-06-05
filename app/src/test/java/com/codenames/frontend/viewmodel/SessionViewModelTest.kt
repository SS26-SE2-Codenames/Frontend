package com.codenames.frontend.viewmodel

import com.codenames.frontend.data.model.SessionState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SessionViewModelTest {
    private lateinit var viewModel: SessionViewModel

    @Before
    fun setup() {
        viewModel = SessionViewModel()
    }

    @Test
    fun `initial username is empty`() {
        val result = viewModel.username.value

        assertEquals(SessionState(""), result)
    }

    @Test
    fun `setUsername updates username state`() {
        viewModel.setUsername("Max")

        val result = viewModel.username.value

        assertEquals(SessionState("Max"), result)
    }
}
