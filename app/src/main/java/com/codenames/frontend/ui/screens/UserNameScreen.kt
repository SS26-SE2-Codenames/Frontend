package com.codenames.frontend.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
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
import androidx.compose.ui.platform.testTag
import androidx.navigation.NavController
import com.codenames.frontend.data.model.RejoinState
import com.codenames.frontend.data.model.RejoinUiState
import com.codenames.frontend.data.model.enums.ConnectionState
import com.codenames.frontend.ui.buttons.AppButton
import com.codenames.frontend.ui.buttons.AppButtonStyle
import com.codenames.frontend.ui.buttons.SettingsCornerButton
import com.codenames.frontend.ui.inputs.AppTextField
import com.codenames.frontend.ui.inputs.AppTextFieldState
import com.codenames.frontend.ui.inputs.InputFilters
import com.codenames.frontend.ui.navigation.Screen
import com.codenames.frontend.ui.roles.PlayerRoles
import com.codenames.frontend.ui.theme.AppBackground
import com.codenames.frontend.ui.theme.LocalResponsiveDimensions
import com.codenames.frontend.ui.theme.blueGradient
import com.codenames.frontend.viewmodel.ChatViewModel
import com.codenames.frontend.viewmodel.GameViewModel
import com.codenames.frontend.viewmodel.LobbyViewModel
import com.codenames.frontend.viewmodel.SessionViewModel

@Suppress("ktlint:standard:function-naming")
@Composable
fun UserNameScreen(
    navController: NavController,
    viewModel: SessionViewModel,
    gameViewModel: GameViewModel,
    lobbyViewModel: LobbyViewModel,
    chatViewModel: ChatViewModel,
) {
    val userState by viewModel.userState.collectAsState()
    val rejoinState by viewModel.rejoinSessionState.collectAsState()
    val connectionState by gameViewModel.connectionState.collectAsState()
    val gameState by gameViewModel.uiState.collectAsState()

    val dimensions = LocalResponsiveDimensions.current
    var username by rememberSaveable { mutableStateOf("") }

    val availableRejoinState = rejoinState as? RejoinState.Available
    val lobbyCode = availableRejoinState?.sessionState?.lobbyCode
    val userId = userState.userId

    if (availableRejoinState != null && lobbyCode != null) {
        HandleRejoinEffects(
            navController,
            viewModel,
            gameViewModel,
            lobbyViewModel,
            chatViewModel,
            RejoinUiState(
                availableRejoinState = availableRejoinState,
                connectionState = connectionState,
                gameState = gameState,
                userId = userId,
                lobbyCode = lobbyCode,
                username = username,
            ),
        )
    }

    LaunchedEffect(userState.username) {
        if (username.isBlank()) {
            username = userState.username
        }
    }

    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .background(AppBackground),
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(dimensions.screenPadding),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = "Codenames",
                fontSize = dimensions.titleFontSize,
                modifier = Modifier.padding(bottom = dimensions.sectionSpacing * 3),
            )

            AppTextField(
                value = username,
                onValueChange = { username = it },
                state = AppTextFieldState(label = "Enter username", placeholder = "user"),
                modifier = Modifier.fillMaxWidth(if (dimensions.isNarrowWidth) 0.7f else 0.5f),
                inputFilter = InputFilters.ALPHANUMERIC,
            )

            Spacer(modifier = Modifier.height(dimensions.itemSpacing))

            AppButton(
                text = "Continue",
                onClick = {
                    if (username.isBlank()) return@AppButton
                    viewModel.setUsername(username)
                    navController.navigate(Screen.Start.route)
                },
                modifier =
                    Modifier
                        .width(dimensions.primaryButtonWidth)
                        .height(dimensions.primaryButtonHeight)
                        .testTag(JOIN_LOBBY_BUTTON_TAG),
                style =
                    AppButtonStyle(
                        enabled = username.isNotBlank(),
                        backgroundBrush = blueGradient,
                        fontSize = dimensions.buttonFontSize,
                        lineHeight = dimensions.buttonLineHeight,
                    ),
            )
        }

        SettingsCornerButton(
            onClick = { navController.navigate(Screen.Settings.route) },
        )
    }

    if (availableRejoinState != null) {
        RejoinDialog(
            onRejoinGame = {
                lobbyCode?.let {
                    gameViewModel.connect(lobbyCode, false)
                }
            },
            onDiscardGame = {
                viewModel.clearLobby()
            },
        )
    }
}

@Suppress("ktlint:standard:function-naming")
@Composable
fun RejoinDialog(
    onRejoinGame: () -> Unit,
    onDiscardGame: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = {},
        title = {
            Text("Spiel fortsetzen?")
        },
        text = {
            Text("Es wurde ein gespeichertes Spiel gefunden. Möchtest du wieder beitreten?")
        },
        confirmButton = {
            AppButton(
                text = "Beitreten",
                onClick = { onRejoinGame() },
            )
        },
        dismissButton = {
            AppButton(
                text = "Verwerfen",
                onClick = { onDiscardGame() },
            )
        },
    )
}

@Suppress("ktlint:standard:function-naming")
@Composable
fun HandleRejoinEffects(
    navController: NavController,
    viewModel: SessionViewModel,
    gameViewModel: GameViewModel,
    lobbyViewModel: LobbyViewModel,
    chatViewModel: ChatViewModel,
    rejoinUiState: RejoinUiState,
) {
    val connectionState = rejoinUiState.connectionState
    val userId = rejoinUiState.userId
    val lobbyCode = rejoinUiState.lobbyCode
    val username = rejoinUiState.username
    val availableRejoinState = rejoinUiState.availableRejoinState
    val gameState = rejoinUiState.gameState

    val canRejoin =
        connectionState is ConnectionState.CONNECTED &&
            userId != null

    LaunchedEffect(canRejoin) {
        if (!canRejoin) return@LaunchedEffect
        if (availableRejoinState.sessionState.lobbyRole != null && availableRejoinState.sessionState.lobbyTeam != null) {
            chatViewModel.subscribeToChats(
                username,
                lobbyCode,
                availableRejoinState.sessionState.lobbyTeam.name,
                availableRejoinState.sessionState.lobbyRole.name,
            )
        }

        gameViewModel.rejoinGame(username, userId, availableRejoinState)
        lobbyViewModel.startUpdateAfterRejoin(lobbyCode)
    }

    LaunchedEffect(availableRejoinState) {
        if (availableRejoinState.sessionState.consumed) {
            navController.navigate(Screen.Gameboard.route)
        }
    }

    LaunchedEffect(gameState) {
        if (gameState.currentTurn != PlayerRoles.NONE) {
            viewModel.setRejoinStateConsumed()
        }
    }
}
