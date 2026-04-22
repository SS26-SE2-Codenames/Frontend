package com.codenames.frontend.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.codenames.frontend.ui.buttons.AppButton
import com.codenames.frontend.ui.buttons.AppButtonStyle
import com.codenames.frontend.ui.inputs.AppTextField
import com.codenames.frontend.ui.inputs.AppTextFieldKeyboard
import com.codenames.frontend.ui.inputs.AppTextFieldState
import com.codenames.frontend.ui.inputs.AppTextFieldStyle
import com.codenames.frontend.ui.inputs.AppTextFieldType

internal const val JOIN_LOBBY_INPUT_TAG = "join_lobby_input"
internal const val JOIN_LOBBY_BUTTON_TAG = "join_lobby_button"
private const val LOBBY_ID_MAX_LENGTH = 12

internal fun sanitizeLobbyIdInput(input: String): String =
    input
        .uppercase()
        .filter { it.isLetterOrDigit() }
        .take(LOBBY_ID_MAX_LENGTH)

internal fun isLobbyIdValid(lobbyId: String): Boolean = lobbyId.isNotBlank()

@Suppress("ktlint:standard:function-naming")
@Composable
fun JoinlobbyScreen() {
    ForceLandscape()

    var lobbyId by rememberSaveable { mutableStateOf("") }

    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    val blueGradient =
        Brush.verticalGradient(
            colors =
                listOf(
                    Color(0xFF42A5F5),
                    Color(0xFF1565C0),
                ),
        )

    val joinEnabled = isLobbyIdValid(lobbyId)

    fun submitJoin() {
        if (!joinEnabled) return

        keyboardController?.hide()
        focusManager.clearFocus()

        // Hier später den echten Frontend-Join-Flow anschließen.
    }

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .background(Color(0xFF4A403D))
                .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        AppTextField(
            value = lobbyId,
            onValueChange = { newValue ->
                lobbyId = sanitizeLobbyIdInput(newValue)
            },
            modifier =
                Modifier
                    .fillMaxWidth(0.5f)
                    .padding(bottom = 16.dp)
                    .testTag(JOIN_LOBBY_INPUT_TAG),
            state =
                AppTextFieldState(
                    label = "Lobby ID",
                    placeholder = "Enter Lobby ID",
                ),
            style =
                AppTextFieldStyle(
                    type = AppTextFieldType.SECONDARY,
                    contentColor = Color.White,
                    fontSize = 20.sp,
                    lineHeight = 24.sp,
                ),
            keyboard =
                AppTextFieldKeyboard(
                    options =
                        KeyboardOptions(
                            capitalization = KeyboardCapitalization.Characters,
                            keyboardType = KeyboardType.Ascii,
                            imeAction = ImeAction.Done,
                        ),
                    actions =
                        KeyboardActions(
                            onDone = { submitJoin() },
                        ),
                ),
        )

        AppButton(
            text = "Join Lobby",
            onClick = { submitJoin() },
            modifier =
                Modifier
                    .width(220.dp)
                    .height(80.dp)
                    .testTag(JOIN_LOBBY_BUTTON_TAG),
            style =
                AppButtonStyle(
                    enabled = joinEnabled,
                    backgroundBrush = blueGradient,
                    fontSize = 26.sp,
                    lineHeight = 30.sp,
                ),
        )
    }
}
