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
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.codenames.frontend.data.model.enums.Team
import com.codenames.frontend.ui.buttons.AppButton
import com.codenames.frontend.ui.buttons.AppButtonStyle
import com.codenames.frontend.ui.navigation.Screen
import com.codenames.frontend.ui.roles.PlayerRoles

val blueGradient =
    Brush.verticalGradient(
        colors =
            listOf(
                Color(0xFF42A5F5),
                Color(0xFF1565C0),
            ),
    )

val redGradient =
    Brush.verticalGradient(
        colors =
            listOf(
                Color(0xFFCF5530),
                Color(0xFFDE8468),
            ),
    )

val brownGradient =
    Brush.verticalGradient(
        colors =
            listOf(
                Color(0xFF383330),
                Color(0xFF1A1513),
            ),
    )

val greenGradient =
    Brush.verticalGradient(
        colors =
            listOf(
                Color(0xFF4CAF50),
                Color(0xFF2E7D32),
            ),
    )

private const val JOIN_TEAM: String = "JOIN TEAM"
private const val TEAM_JOINED: String = "👤 1 joined"

@Suppress("ktlint:standard:function-naming")
@Composable
fun LobbyScreen(navController: NavHostController) {
    var currentRole by remember { mutableStateOf(PlayerRoles.NONE) }

    Row(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(top = 64.dp, start = 16.dp, end = 16.dp, bottom = 16.dp)
                .background(Color(0xFFf0d8ce)),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        TeamColumn(
            modifier = Modifier.weight(1f),
            color = Team.BLUE,
            gradient = blueGradient,
            textColor = Color(0xFF42A5F5),
            title = "BLUE TEAM",
            currentRole = currentRole,
            onRoleSelect = { currentRole = it },
        )

        GameSettingsColumn(
            currentRole = currentRole,
            navController = navController,
        )

        TeamColumn(
            modifier = Modifier.weight(1f),
            color = Team.RED,
            gradient = redGradient,
            textColor = Color(0xFFDE8468),
            title = "RED TEAM",
            currentRole = currentRole,
            onRoleSelect = { currentRole = it },
        )
    }
}

@Suppress("ktlint:standard:function-naming")
@Composable
fun TeamColumn(
    modifier: Modifier,
    color: Team,
    gradient: Brush,
    textColor: Color,
    title: String,
    currentRole: PlayerRoles,
    onRoleSelect: (PlayerRoles) -> Unit,
) {
    val align = if (color == Team.RED) Alignment.End else Alignment.Start
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        val cardModifier =
            Modifier
                .align(align)
                .width(200.dp)
                .height(150.dp)
                .fillMaxWidth(0.5f)
                .padding(start = 6.dp, bottom = 12.dp)
                .background(gradient, RoundedCornerShape(12.dp))
                .padding(12.dp)

        Text(
            text = title,
            color = textColor,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier =
                Modifier
                    .align(align)
                    .padding(start = 6.dp)
                    .padding(end = 6.dp)
                    .padding(bottom = 6.dp),
        )

        RoleCard(
            role = if (color == Team.RED) PlayerRoles.RED_OPERATIVE else PlayerRoles.BLUE_OPERATIVE,
            currentRole = currentRole,
            onRoleSelect = onRoleSelect,
            modifier = cardModifier,
            title = "OPERATIVES",
        )

        RoleCard(
            role = if (color == Team.RED) PlayerRoles.RED_SPYMASTER else PlayerRoles.BLUE_SPYMASTER,
            currentRole = currentRole,
            onRoleSelect = onRoleSelect,
            modifier = cardModifier,
            title = "SPYMASTERS",
        )
    }
}

@Suppress("ktlint:standard:function-naming")
@Composable
fun RoleCard(
    role: PlayerRoles,
    currentRole: PlayerRoles,
    onRoleSelect: (PlayerRoles) -> Unit,
    modifier: Modifier,
    title: String,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(title, color = Color.White, fontWeight = FontWeight.Bold)

        if (currentRole == role) {
            Text(
                text = TEAM_JOINED,
                color = Color.White,
                modifier = Modifier.padding(vertical = 8.dp),
            )
        }

        AppButton(
            text = JOIN_TEAM,
            onClick = { onRoleSelect(role) },
            style =
                AppButtonStyle(
                    backgroundBrush = greenGradient,
                    fontSize = 16.sp,
                ),
        )
    }
}

@Suppress("ktlint:standard:function-naming")
@Composable
fun GameSettingsColumn(
    currentRole: PlayerRoles,
    navController: NavController,
) {
    Column(
        modifier = Modifier.padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Column(
            modifier =
                Modifier
                    .align(Alignment.CenterHorizontally)
                    .width(400.dp)
                    .height(250.dp)
                    .fillMaxWidth(0.5f)
                    .background(brownGradient, RoundedCornerShape(12.dp))
                    .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top,
        ) {
            Text(
                text = "GAME SETTINGS",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp),
            )

            AppButton(
                text = "TIMER: OFF",
                onClick = { /* TODO: Timer Logik */ },
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                style =
                    AppButtonStyle(
                        containerColor = Color(0xFF555555),
                        contentColor = Color.White,
                        fontSize = 18.sp,
                    ),
            )
        }

        AppButton(
            text = "START GAME",
            onClick = {
                navController.navigate("${Screen.Gameboard.route}/${currentRole.name}")
            },
            modifier =
                Modifier
                    .align(Alignment.CenterHorizontally)
                    .width(400.dp)
                    .height(70.dp)
                    .fillMaxWidth(0.5f)
                    .padding(top = 12.dp),
            style =
                AppButtonStyle(
                    backgroundBrush = greenGradient,
                    fontSize = 28.sp,
                    lineHeight = 30.sp,
                ),
        )
    }
}
