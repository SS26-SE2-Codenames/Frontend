package com.codenames.frontend.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.codenames.frontend.data.model.enums.ConnectionState
import com.codenames.frontend.ui.buttons.AppButton
import com.codenames.frontend.ui.buttons.AppButtonStyle
import com.codenames.frontend.ui.navigation.Screen
import com.codenames.frontend.viewmodel.GameViewModel

@Composable
fun StartScreen(
    navController: NavHostController,
    viewModel: GameViewModel = hiltViewModel(),
) {
    val state by viewModel.connectionState.collectAsState()
    ForceLandscape()

    val greenGradient =
        Brush.verticalGradient(
            colors =
                listOf(
                    Color(0xFF4CAF50),
                    Color(0xFF2E7D32),
                ),
        )

    val blueGradient =
        Brush.verticalGradient(
            colors =
                listOf(
                    Color(0xFF42A5F5),
                    Color(0xFF1565C0),
                ),
        )

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .background(Color(0xFF4A403D)),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
        ) {
            AppButton(
                text = "Create Lobby",
                onClick = {
                    navController.navigate(Screen.Lobby.route)
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

        AppButton(
            text = "Connect to Server",
            onClick = {
                viewModel.connect("TestUser", "12345")
            },
            modifier =
                Modifier
                    .width(200.dp)
                    .height(100.dp)
                    .padding(bottom = 12.dp),
            style =
                AppButtonStyle(
                    backgroundBrush = blueGradient,
                    fontSize = 26.sp,
                    lineHeight = 30.sp,
                ),
        )

        when (state) {
            is ConnectionState.CONNECTING -> Text(text = "Connecting...", color = Color.Yellow, fontSize = 25.sp)
            is ConnectionState.CONNECTED -> Text("Connected", color = Color.Green, fontSize = 25.sp)
            is ConnectionState.Error -> {
                Text("Error while connecting: ")
                Text((state as ConnectionState.Error).message)
            }
            else -> {}
        }
    }
}
