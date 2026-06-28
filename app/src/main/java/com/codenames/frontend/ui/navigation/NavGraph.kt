package com.codenames.frontend.ui.navigation

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.codenames.frontend.data.model.enums.ConnectionState
import com.codenames.frontend.ui.composables.ErrorDialog
import com.codenames.frontend.ui.screens.ForceLandscape
import com.codenames.frontend.ui.screens.GameScreenWrapper
import com.codenames.frontend.ui.screens.GameSettingsScreen
import com.codenames.frontend.ui.screens.JoinlobbyScreen
import com.codenames.frontend.ui.screens.LobbyScreen
import com.codenames.frontend.ui.screens.SettingsScreen
import com.codenames.frontend.ui.screens.StartScreen
import com.codenames.frontend.ui.screens.UserNameScreen
import com.codenames.frontend.ui.theme.LocalResponsiveDimensions
import com.codenames.frontend.ui.theme.responsiveDimensionsFor
import com.codenames.frontend.viewmodel.ChatViewModel
import com.codenames.frontend.viewmodel.GameViewModel
import com.codenames.frontend.viewmodel.LobbyViewModel
import com.codenames.frontend.viewmodel.SessionViewModel

@Composable
@Suppress("ktlint:standard:function-naming")
fun NavGraph(
    lobbyViewModel: LobbyViewModel = hiltViewModel(),
    sessionViewModel: SessionViewModel = hiltViewModel(),
    gameViewModel: GameViewModel = hiltViewModel(),
    chatViewModel: ChatViewModel = hiltViewModel(),
) {
    val navController = rememberNavController()

    val lobbyState by lobbyViewModel.state.collectAsState()
    val connectionState by gameViewModel.connectionState.collectAsState()
    val chatErrorMessage by chatViewModel.errorMessage.collectAsState()
    val gameErrorMessage by gameViewModel.errorMessage.collectAsState()

    val errorMessage: String? =
        lobbyState.error
            ?: (connectionState as? ConnectionState.Error)?.message
            ?: chatErrorMessage
            ?: gameErrorMessage

    @Suppress("UnusedBoxWithConstraintsScope")
    BoxWithConstraints {
        val responsiveDimensions =
            responsiveDimensionsFor(
                maxWidth = maxWidth,
                maxHeight = maxHeight,
            )
        ForceLandscape()

        CompositionLocalProvider(LocalResponsiveDimensions provides responsiveDimensions) {
            NavHost(
                navController = navController,
                startDestination = Screen.Username.route,
            ) {
                composable(Screen.Username.route) {
                    UserNameScreen(navController, sessionViewModel, gameViewModel, lobbyViewModel, chatViewModel)
                }

                composable(Screen.Start.route) {
                    StartScreen(
                        lobbyViewModel = lobbyViewModel,
                        navController = navController,
                        sessionViewModel = sessionViewModel,
                    )
                }

                composable(Screen.Lobby.route) {
                    LobbyScreen(
                        navController = navController,
                        viewModel = lobbyViewModel,
                        gameViewModel = gameViewModel,
                        chatViewModel = chatViewModel,
                        sessionViewModel = sessionViewModel,
                    )
                }

                composable(Screen.JoinLobby.route) {
                    JoinlobbyScreen(
                        viewModel = lobbyViewModel,
                        navController = navController,
                        sessionViewModel = sessionViewModel,
                    )
                }

                composable(route = Screen.Gameboard.route) {
                    GameScreenWrapper(
                        navController = navController,
                        lobbyViewModel = lobbyViewModel,
                        gameViewModel = gameViewModel,
                        chatViewModel = chatViewModel,
                        sessionViewModel = sessionViewModel,
                    )
                }

                composable(Screen.GameSettings.route) {
                    GameSettingsScreen()
                }

                composable(Screen.Settings.route) {
                    SettingsScreen(navController)
                }
            }

            if (errorMessage != null) {
                ErrorDialog(
                    message = errorMessage,
                    onDismiss = {
                        lobbyViewModel.clearError()
                        gameViewModel.clearError()
                        gameViewModel.clearErrorState()
                        chatViewModel.clearError()
                    },
                )
            }
        }
    }
}
