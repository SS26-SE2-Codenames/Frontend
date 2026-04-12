package com.codenames.codenames_frontend.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.codenames.codenames_frontend.ui.buttons.AppButton
import com.codenames.codenames_frontend.ui.buttons.AppButtonStyle
import com.codenames.codenames_frontend.ui.navigation.Screen

@Composable
fun StartScreen(navController: NavHostController) {
    ForceLandscape()

    val greenGradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF4CAF50),
            Color(0xFF2E7D32)
        )
    )

    val blueGradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF42A5F5),
            Color(0xFF1565C0)
        )
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF4A403D)),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AppButton(
            text = "Create Lobby",
            onClick = {
                navController.navigate(Screen.Lobby.route)
            },
            modifier = Modifier
                .width(200.dp)
                .height(100.dp)
                .fillMaxWidth(0.5f)
                .padding(bottom = 12.dp),
            style = AppButtonStyle(
                backgroundBrush = greenGradient,
                fontSize = 26.sp,
                lineHeight = 30.sp
            )
        )

        AppButton(
            text = "Join Lobby",
            onClick = {
                navController.navigate(Screen.JoinLobby.route)
            },
            modifier = Modifier
                .width(200.dp)
                .height(100.dp)
                .fillMaxWidth(0.5f)
                .padding(bottom = 12.dp),
            style = AppButtonStyle(
                backgroundBrush = blueGradient,
                fontSize = 26.sp,
                lineHeight = 30.sp
            )
        )
        AppButton(
            text = "test Mode",
            onClick = {
                navController.navigate("game_test")
            },
            modifier = Modifier
                .width(200.dp)
                .height(100.dp)
                .fillMaxWidth(0.5f)
                .padding(bottom = 12.dp),
            style = AppButtonStyle(
                backgroundBrush = greenGradient,
                fontSize = 26.sp,
                lineHeight = 30.sp
            )
        )
    }
}