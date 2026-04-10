package com.codenames.codenames_frontend.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.codenames.codenames_frontend.ui.buttons.AppButton
import com.codenames.codenames_frontend.ui.inputs.AppTextField
import com.codenames.codenames_frontend.ui.inputs.AppTextFieldType

@Composable
fun JoinlobbyScreen(navController: NavHostController) {
    ForceLandscape()

    var lobbyId by rememberSaveable { mutableStateOf("") }

    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    val blueGradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF42A5F5),
            Color(0xFF1565C0)
        )
    )

    val sanitizedLobbyId = lobbyId.trim()
    val isLobbyIdValid = sanitizedLobbyId.isNotEmpty()

    fun submitJoin() {
        if (!isLobbyIdValid) return

        keyboardController?.hide()
        focusManager.clearFocus()

        // Hier später den echten Frontend-Join-Flow anschließen,
        // z. B. ViewModel-Funktion oder Navigation.
        // Beispiel:
        // viewModel.joinLobby(sanitizedLobbyId)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF4A403D))
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AppTextField(
            value = lobbyId,
            onValueChange = { newValue ->
                lobbyId = newValue
                    .uppercase()
                    .take(12)
            },
            label = "Lobby ID",
            placeholder = "Enter Lobby ID",
            type = AppTextFieldType.SECONDARY,
            contentColor = Color.White,
            modifier = Modifier
                .fillMaxWidth(0.5f)
                .padding(bottom = 16.dp),
            fontSize = 20.sp,
            lineHeight = 24.sp,
            singleLine = true,
            isError = lobbyId.isNotEmpty() && !isLobbyIdValid,
            supportingText = if (lobbyId.isNotEmpty() && !isLobbyIdValid) {
                "Please enter a valid lobby ID."
            } else {
                null
            },
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Characters,
                keyboardType = KeyboardType.Ascii,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    submitJoin()
                }
            )
        )

        AppButton(
            text = "Join Lobby",
            onClick = {
                submitJoin()
            },
            enabled = isLobbyIdValid,
            backgroundBrush = blueGradient,
            fontSize = 26.sp,
            lineHeight = 30.sp,
            modifier = Modifier
                .width(220.dp)
                .height(80.dp)
        )
    }
}