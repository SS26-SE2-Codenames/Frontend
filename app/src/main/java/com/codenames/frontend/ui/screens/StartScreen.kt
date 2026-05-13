package com.codenames.frontend.ui.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.codenames.frontend.ui.buttons.AppButton
import com.codenames.frontend.ui.buttons.AppButtonStyle
import com.codenames.frontend.ui.buttons.SettingsCornerButton
import com.codenames.frontend.ui.navigation.Screen
import com.codenames.frontend.ui.theme.blueGradient
import com.codenames.frontend.ui.theme.greenGradient
import com.codenames.frontend.viewmodel.LobbyViewModel
import com.codenames.frontend.viewmodel.SessionViewModel

@Suppress("ktlint:standard:function-naming")
@Composable
fun StartScreen(
    navController: NavHostController,
    viewModel: LobbyViewModel = hiltViewModel(),
    sessionViewModel: SessionViewModel = hiltViewModel(navController.getBackStackEntry("main_graph")),
) {
    ForceLandscape()

    val state by viewModel.state.collectAsState()
    val usernameState by sessionViewModel.username.collectAsState()

    LaunchedEffect(state.lobbyCode, state.error, state.isLoading) {
        if (!state.isLoading && state.error == null && state.lobbyCode != null) {
            navController.navigate(Screen.Lobby.route)
        }
    }

    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .background(Color(0xFFf0d8ce)),
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text("Welcome to Codenames, ${usernameState.username}!", fontSize = 32.sp, modifier = Modifier.padding(bottom = 48.dp))

            Row(
                modifier =
                    Modifier
                        .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
            ) {
                AppButton(
                    text = "Create Lobby",
                    onClick = {
                        viewModel.createLobby(usernameState.username)
                    },
                    modifier =
                        Modifier
                            .width(200.dp)
                            .height(100.dp)
                            .fillMaxWidth(0.5f)
                            .padding(bottom = 12.dp, end = 12.dp),
                    style =
                        AppButtonStyle(
                            backgroundBrush = greenGradient,
                            fontSize = 26.sp,
                            lineHeight = 30.sp,
                        ),
                )

                AppButton(
                    text = "Join Lobby",
                    onClick = {
                        navController.navigate(Screen.JoinLobby.route)
                    },
                    modifier =
                        Modifier
                            .width(200.dp)
                            .height(100.dp)
                            .fillMaxWidth(0.5f)
                            .padding(bottom = 12.dp, start = 12.dp),
                    style =
                        AppButtonStyle(
                            backgroundBrush = blueGradient,
                            fontSize = 26.sp,
                            lineHeight = 30.sp,
                        ),
                )
            }

            AppButton(
                text = "test Mode",
                onClick = {
                    navController.navigate("game_test")
                },
                modifier =
                    Modifier
                        .width(200.dp)
                        .height(100.dp)
                        .padding(bottom = 12.dp),
                style =
                    AppButtonStyle(
                        backgroundBrush = greenGradient,
                        fontSize = 26.sp,
                        lineHeight = 30.sp,
                    ),
            )
        }

        SettingsCornerButton(
            onClick = { navController.navigate(Screen.Settings.route) },
        )
    }
}
