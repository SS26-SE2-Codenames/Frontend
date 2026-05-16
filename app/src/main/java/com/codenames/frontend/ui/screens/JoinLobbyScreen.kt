package com.codenames.frontend.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.codenames.frontend.ui.buttons.AppButton
import com.codenames.frontend.ui.buttons.AppButtonStyle
import com.codenames.frontend.ui.buttons.SettingsCornerButton
import com.codenames.frontend.ui.inputs.AppTextField
import com.codenames.frontend.ui.inputs.AppTextFieldKeyboard
import com.codenames.frontend.ui.inputs.AppTextFieldState
import com.codenames.frontend.ui.inputs.AppTextFieldStyle
import com.codenames.frontend.ui.inputs.AppTextFieldType
import com.codenames.frontend.ui.navigation.Screen
import com.codenames.frontend.ui.theme.blueGradient
import com.codenames.frontend.viewmodel.LobbyViewModel
import com.codenames.frontend.viewmodel.SessionViewModel

internal const val JOIN_LOBBY_INPUT_TAG = "join_lobby_input"
internal const val JOIN_LOBBY_BUTTON_TAG = "join_lobby_button"
private const val LOBBY_ID_LENGTH = 5 // lobby id hat genau 5 Zeichen

private fun sanitizeLobbyIdInput(input: String): String =
    input
        .uppercase()
        .filter { it.isLetterOrDigit() }
        .take(LOBBY_ID_LENGTH)

fun isLobbyIdValid(lobbyId: String): Boolean =
    lobbyId.isNotBlank() &&
        lobbyId.length == LOBBY_ID_LENGTH &&
        lobbyId.matches("^[ABCDEFGHJKLMNPQRSTUVWXYZ23456789]+$".toRegex())

@Suppress("ktlint:standard:function-naming")
@Composable
fun JoinlobbyScreen(
    navController: NavHostController,
    viewModel: LobbyViewModel,
    sessionViewModel: SessionViewModel,
) {
    ForceLandscape()

    var lobbyId by rememberSaveable { mutableStateOf("") }

    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    val state by viewModel.state.collectAsState()
    val username by sessionViewModel.username.collectAsState()

    val joinEnabled = isLobbyIdValid(lobbyId)

    // Navigation wird ausgeführt, wenn alle notwendigen states im Lobby UI state gesetzt sind. Wird bei jeder rekomposition der UI durchlaufen
    LaunchedEffect(state.lobbyCode, state.error, state.isLoading) {
        if (!state.isLoading && state.error == null && state.lobbyCode != null) {
            navController.navigate(Screen.Lobby.route)
        }
    }

    fun submitJoin() {
        if (!joinEnabled) return

        keyboardController?.hide()
        focusManager.clearFocus()

        viewModel.joinLobby(username.username, lobbyId)
    }

    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .background(Color(0xFFf0d8ce)),
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
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

            if (state.isLoading) {
                Text(
                    text = "Joining...",
                    color = Color(0xFF383330),
                    fontSize = 20.sp,
                    modifier = Modifier.padding(top = 12.dp),
                )
            }

            state.error?.let { error ->
                Text(
                    text = error,
                    color = Color(0xFFCF5530),
                    fontSize = 18.sp,
                    modifier = Modifier.padding(top = 12.dp),
                )
            }
        }

        SettingsCornerButton(
            onClick = { navController.navigate(Screen.Settings.route) },
        )
    }
}
