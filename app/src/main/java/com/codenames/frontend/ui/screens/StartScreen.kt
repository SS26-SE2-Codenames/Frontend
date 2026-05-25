package com.codenames.frontend.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.codenames.frontend.ui.buttons.AppButton
import com.codenames.frontend.ui.buttons.AppButtonStyle
import com.codenames.frontend.ui.buttons.SettingsCornerButton
import com.codenames.frontend.ui.navigation.Screen
import com.codenames.frontend.ui.theme.AppBackground
import com.codenames.frontend.ui.theme.AppInk
import com.codenames.frontend.ui.theme.AppRed
import com.codenames.frontend.ui.theme.LocalResponsiveDimensions
import com.codenames.frontend.ui.theme.blueGradient
import com.codenames.frontend.ui.theme.greenGradient
import com.codenames.frontend.viewmodel.LobbyViewModel
import com.codenames.frontend.viewmodel.SessionViewModel

@Suppress("ktlint:standard:function-naming")
@Composable
fun StartScreen(
    navController: NavHostController,
    lobbyViewModel: LobbyViewModel,
    sessionViewModel: SessionViewModel,
) {
    ForceLandscape()

    val dimensions = LocalResponsiveDimensions.current
    val lobbyState by lobbyViewModel.state.collectAsState()
    val usernameState by sessionViewModel.username.collectAsState()

    LaunchedEffect(lobbyState.lobbyCode, lobbyState.error, lobbyState.isLoading) {
        if (!lobbyState.isLoading && lobbyState.error == null && lobbyState.lobbyCode != null) {
            navController.navigate(Screen.Lobby.route)
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
                text = "Welcome to Codenames, ${usernameState.username}!",
                fontSize = dimensions.titleFontSize,
                modifier = Modifier.padding(bottom = dimensions.sectionSpacing * 2),
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(dimensions.itemSpacing),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                AppButton(
                    text = "Create Lobby",
                    onClick = {
                        lobbyViewModel.createLobby(usernameState.username)
                    },
                    modifier =
                        Modifier
                            .width(dimensions.primaryButtonWidth)
                            .height(dimensions.primaryButtonHeight),
                    style =
                        AppButtonStyle(
                            enabled = !lobbyState.isLoading,
                            backgroundBrush = greenGradient,
                            fontSize = dimensions.buttonFontSize,
                            lineHeight = dimensions.buttonLineHeight,
                        ),
                )

                AppButton(
                    text = "Join Lobby",
                    onClick = {
                        navController.navigate(Screen.JoinLobby.route)
                    },
                    modifier =
                        Modifier
                            .width(dimensions.primaryButtonWidth)
                            .height(dimensions.primaryButtonHeight),
                    style =
                        AppButtonStyle(
                            enabled = !lobbyState.isLoading,
                            backgroundBrush = blueGradient,
                            fontSize = dimensions.buttonFontSize,
                            lineHeight = dimensions.buttonLineHeight,
                        ),
                )
            }

            if (lobbyState.isLoading) {
                Text(
                    text = "Loading...",
                    color = AppInk,
                    fontSize = dimensions.bodyFontSize,
                    modifier = Modifier.padding(top = dimensions.itemSpacing),
                )
            }

            lobbyState.error?.let { error ->
                Text(
                    text = error,
                    color = AppRed,
                    fontSize = dimensions.bodyFontSize,
                    modifier = Modifier.padding(top = dimensions.itemSpacing),
                )
            }
        }

        SettingsCornerButton(
            onClick = { navController.navigate(Screen.Settings.route) },
        )
    }
}
