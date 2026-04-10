package com.codenames.codenames_frontend.ui.screens

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
fun LobbyScreen(navController: NavHostController) {
    val blueGradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF42A5F5),
            Color(0xFF1565C0)
        )
    )

    val redGradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFFCF5530),
            Color(0xFFDE8468)
        )
    )

    val brownGradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF383330),
            Color(0xFF1A1513)
        )
    )

    val greenGradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF4CAF50),
            Color(0xFF2E7D32)
        )
    )

    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "BLUE TEAM",
                color = Color(0xFF42A5F5),
                fontSize = 24.sp,
                modifier = Modifier
                    .align(Alignment.Start)
                    .padding(start = 6.dp)
                    .padding(bottom = 6.dp)
            )

            AppButton(
                text = "Join Operators",
                onClick = {
                    navController.navigate(Screen.Gameboard.route)
                },
                modifier = Modifier
                    .align(Alignment.Start)
                    .width(200.dp)
                    .height(150.dp)
                    .fillMaxWidth(0.5f)
                    .padding(start = 6.dp)
                    .padding(bottom = 12.dp),
                style = AppButtonStyle(
                    backgroundBrush = blueGradient,
                    fontSize = 30.sp,
                    lineHeight = 30.sp
                )
            )

            AppButton(
                text = "Join Spymasters",
                onClick = {
                    navController.navigate(Screen.Gameboard.route)
                },
                modifier = Modifier
                    .align(Alignment.Start)
                    .width(200.dp)
                    .height(150.dp)
                    .fillMaxWidth(0.5f)
                    .padding(start = 6.dp)
                    .padding(bottom = 12.dp),
                style = AppButtonStyle(
                    backgroundBrush = blueGradient,
                    fontSize = 30.sp,
                    lineHeight = 30.sp
                )
            )
        }

        Column(
            modifier = Modifier.padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AppButton(
                text = "Game Settings",
                onClick = {
                    navController.navigate(Screen.GameSettings.route)
                },
                modifier = Modifier
                    .align(Alignment.End)
                    .width(400.dp)
                    .height(250.dp)
                    .fillMaxWidth(0.5f),
                style = AppButtonStyle(
                    backgroundBrush = brownGradient,
                    fontSize = 28.sp,
                    lineHeight = 30.sp
                )
            )

            AppButton(
                text = "Start Game",
                onClick = {
                    navController.navigate(Screen.Gameboard.route)
                },
                modifier = Modifier
                    .align(Alignment.End)
                    .width(400.dp)
                    .height(70.dp)
                    .fillMaxWidth(0.5f)
                    .padding(top = 12.dp),
                style = AppButtonStyle(
                    backgroundBrush = greenGradient,
                    fontSize = 28.sp,
                    lineHeight = 30.sp
                )
            )
        }

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "RED TEAM",
                color = Color(0xFFDE8468),
                fontSize = 24.sp,
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(end = 6.dp)
                    .padding(bottom = 6.dp)
            )

            AppButton(
                text = "Join Operators",
                onClick = {
                    navController.navigate(Screen.Gameboard.route)
                },
                modifier = Modifier
                    .align(Alignment.End)
                    .width(200.dp)
                    .height(150.dp)
                    .fillMaxWidth(0.5f)
                    .padding(end = 6.dp)
                    .padding(bottom = 12.dp),
                style = AppButtonStyle(
                    backgroundBrush = redGradient,
                    fontSize = 28.sp,
                    lineHeight = 30.sp
                )
            )

            AppButton(
                text = "Join Spymasters",
                onClick = {
                    navController.navigate(Screen.Gameboard.route)
                },
                modifier = Modifier
                    .align(Alignment.End)
                    .width(200.dp)
                    .height(150.dp)
                    .fillMaxWidth(0.5f)
                    .padding(end = 6.dp)
                    .padding(bottom = 12.dp),
                style = AppButtonStyle(
                    backgroundBrush = redGradient,
                    fontSize = 28.sp,
                    lineHeight = 30.sp
                )
            )
        }
    }
}