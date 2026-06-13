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
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.Modifier.Companion.any
import androidx.compose.ui.platform.testTag
import androidx.navigation.NavController
import com.codenames.frontend.data.model.RejoinState
import com.codenames.frontend.data.model.enums.ConnectionState
import com.codenames.frontend.ui.buttons.AppButton
import com.codenames.frontend.ui.buttons.AppButtonStyle
import com.codenames.frontend.ui.buttons.SettingsCornerButton
import com.codenames.frontend.ui.navigation.NavGraph
import com.codenames.frontend.ui.navigation.Screen
import com.codenames.frontend.ui.roles.PlayerRoles
import com.codenames.frontend.ui.theme.AppBackground
import com.codenames.frontend.ui.theme.LocalResponsiveDimensions
import com.codenames.frontend.ui.theme.blueGradient
import com.codenames.frontend.viewmodel.GameViewModel
import com.codenames.frontend.viewmodel.LobbyViewModel
import com.codenames.frontend.viewmodel.SessionViewModel
import java.util.UUID

@Suppress("ktlint:standard:function-naming")
@Composable
fun UserNameScreen(
    navController: NavController,
    viewModel: SessionViewModel,
    gameViewModel: GameViewModel,
    lobbyViewModel: LobbyViewModel
) {
    val userState by viewModel.userState.collectAsState()
    val rejoinState by viewModel.rejoinSessionState.collectAsState()
    val connectionState by gameViewModel.connectionState.collectAsState()
    val gameState by gameViewModel.uiState.collectAsState()

    val dimensions = LocalResponsiveDimensions.current
    var username by rememberSaveable { mutableStateOf("") }

    val availableRejoinState = rejoinState as? RejoinState.Available

    LaunchedEffect(connectionState) {
        val username = userState.username
        val userId = userState.userId ?: return@LaunchedEffect
        val lobbyCode = availableRejoinState?.sessionState?.lobbyCode ?: return@LaunchedEffect

        if(connectionState !is ConnectionState.CONNECTED) return@LaunchedEffect

        gameViewModel.rejoinGame(username, userId, availableRejoinState)
        lobbyViewModel.startUpdateAfterRejoin(lobbyCode)
    }

    LaunchedEffect(userState.username) {
        if (username.isBlank()) {
            username = userState.username
        }
    }

    LaunchedEffect(availableRejoinState) {
        availableRejoinState?: return@LaunchedEffect
        if(availableRejoinState.sessionState.consumed) {
            navController.navigate(Screen.Gameboard.route)
        }
    }

    LaunchedEffect(gameState) {
        if(gameState.currentTurn != PlayerRoles.NONE) {
            viewModel.setRejoinStateConsumed()
        }
    }

    fun onRejoinGame() {
        val lobbyCode = availableRejoinState?.sessionState?.lobbyCode ?: return
        gameViewModel.connect(lobbyCode, false)
    }

    fun onDiscardGame() {
        viewModel.clearLobby()
    }

    val showRejoinDialog = rejoinState is RejoinState.Available

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

            TextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("enter username") },
                modifier = Modifier.fillMaxWidth(if (dimensions.isNarrowWidth) 0.7f else 0.5f),
            )

            Spacer(modifier = Modifier.height(dimensions.itemSpacing))

            AppButton(
                text = "Continue",
                onClick = {
                    if (username.isBlank()) return@AppButton
                    viewModel.setUsername(username)
                    viewModel.setUserId(UUID.randomUUID()) //ONLY FOR TESTING!!!!
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

    if(showRejoinDialog) {
        RejoinDialog(onRejoinGame = ::onRejoinGame, onDiscardGame = ::onDiscardGame)
    }
}

@Suppress("ktlint:standard:function-naming")
@Composable
fun RejoinDialog(
    onRejoinGame : () -> Unit,
    onDiscardGame: () -> Unit
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
                onClick = { onRejoinGame() }
            )
        },
        dismissButton = {
            AppButton(
                text = "Verwerfen",
                onClick = { onDiscardGame() }
            )
        }
    )
}