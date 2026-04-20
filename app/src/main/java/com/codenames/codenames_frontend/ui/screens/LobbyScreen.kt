package com.codenames.codenames_frontend.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.codenames.codenames_frontend.ui.buttons.AppButton
import com.codenames.codenames_frontend.ui.buttons.AppButtonStyle
import com.codenames.codenames_frontend.ui.navigation.Screen
import com.codenames.codenames_frontend.ui.roles.PlayerRole

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

    var currentRole by remember { mutableStateOf(PlayerRole.NONE) }

    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 64.dp, start = 16.dp, end = 16.dp, bottom = 16.dp),
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
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .align(Alignment.Start)
                    .padding(start = 6.dp)
                    .padding(bottom = 6.dp)
            )

            Column(
                modifier = Modifier
                    .align(Alignment.Start)
                    .width(200.dp)
                    .height(150.dp)
                    .fillMaxWidth(0.5f)
                    .padding(start = 6.dp, bottom = 12.dp)
                    .background(blueGradient, RoundedCornerShape(12.dp))
                    .padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text("OPERATIVES", color = Color.White, fontWeight = FontWeight.Bold)

                if (currentRole == PlayerRole.BLUE_OPERATIVE) {
                    Text(
                        text = "👤 1 joined",
                        color = Color.White,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }

                AppButton(
                    text = "JOIN TEAM",
                    onClick = { currentRole = PlayerRole.BLUE_OPERATIVE },
                    style = AppButtonStyle(
                        backgroundBrush = greenGradient,
                        fontSize = 16.sp
                    )
                )
            }

            Column(
                modifier = Modifier
                    .align(Alignment.Start)
                    .width(200.dp)
                    .height(150.dp)
                    .fillMaxWidth(0.5f)
                    .padding(start = 6.dp, bottom = 12.dp)
                    .background(blueGradient, RoundedCornerShape(12.dp))
                    .padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text("SPYMASTERS", color = Color.White, fontWeight = FontWeight.Bold)

                if (currentRole == PlayerRole.BLUE_SPYMASTER) {
                    Text(
                        text = "👤 1 joined",
                        color = Color.White,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }

                AppButton(
                    text = "JOIN TEAM",
                    onClick = { currentRole = PlayerRole.BLUE_SPYMASTER },
                    style = AppButtonStyle(
                        backgroundBrush = greenGradient,
                        fontSize = 16.sp
                    )
                )
            }
        }

        Column(
            modifier = Modifier.padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .width(400.dp)
                    .height(250.dp)
                    .fillMaxWidth(0.5f)
                    .background(brownGradient, RoundedCornerShape(12.dp))
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                Text(
                    text = "GAME SETTINGS",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                AppButton(
                    text = "TIMER: OFF",
                    onClick = { /* TODO: Timer Logik */ },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    style = AppButtonStyle(
                        containerColor = Color(0xFF555555),
                        contentColor = Color.White,
                        fontSize = 18.sp
                    )
                )
            }

            AppButton(
                text = "START GAME",
                onClick = {
                    navController.navigate("${Screen.Gameboard.route}/${currentRole.name}")
                },
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
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
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(end = 6.dp)
                    .padding(bottom = 6.dp)
            )

            Column(
                modifier = Modifier
                    .align(Alignment.End)
                    .width(200.dp)
                    .height(150.dp)
                    .fillMaxWidth(0.5f)
                    .padding(end = 6.dp, bottom = 12.dp)
                    .background(redGradient, RoundedCornerShape(12.dp))
                    .padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text("OPERATIVES", color = Color.White, fontWeight = FontWeight.Bold)

                if (currentRole == PlayerRole.RED_OPERATIVE) {
                    Text(
                        text = "👤 1 joined",
                        color = Color.White,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }

                AppButton(
                    text = "JOIN TEAM",
                    onClick = { currentRole = PlayerRole.RED_OPERATIVE },
                    style = AppButtonStyle(
                        backgroundBrush = greenGradient,
                        fontSize = 16.sp
                    )
                )
            }

            Column(
                modifier = Modifier
                    .align(Alignment.End)
                    .width(200.dp)
                    .height(150.dp)
                    .fillMaxWidth(0.5f)
                    .padding(end = 6.dp, bottom = 12.dp)
                    .background(redGradient, RoundedCornerShape(12.dp))
                    .padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text("SPYMASTERS", color = Color.White, fontWeight = FontWeight.Bold)

                if (currentRole == PlayerRole.RED_SPYMASTER) {
                    Text(
                        text = "👤 1 joined",
                        color = Color.White,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }

                AppButton(
                    text = "JOIN TEAM",
                    onClick = { currentRole = PlayerRole.RED_SPYMASTER },
                    style = AppButtonStyle(
                        backgroundBrush = greenGradient,
                        fontSize = 16.sp
                    )
                )
            }
        }
    }
}