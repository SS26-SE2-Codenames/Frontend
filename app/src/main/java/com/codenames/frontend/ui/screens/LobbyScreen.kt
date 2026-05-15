package com.codenames.frontend.ui.screens

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.codenames.frontend.data.model.LobbyUiState
import com.codenames.frontend.data.model.Player
import com.codenames.frontend.data.model.enums.Team
import com.codenames.frontend.ui.buttons.AppButton
import com.codenames.frontend.ui.buttons.AppButtonStyle
import com.codenames.frontend.ui.buttons.SettingsCornerButton
import com.codenames.frontend.ui.navigation.Screen
import com.codenames.frontend.ui.roles.PlayerRoles
import com.codenames.frontend.ui.toPlayerRole
import com.codenames.frontend.ui.toTeamAndRole
import com.codenames.frontend.viewmodel.GameViewModel
import com.codenames.frontend.viewmodel.LobbyViewModel

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

@Suppress("ktlint:standard:function-naming")
@Composable
fun LobbyScreen(
    navController: NavHostController,
    username: String,
    lobbyViewModel: LobbyViewModel,
    gameViewModel: GameViewModel,
) {
    val lobbyState by lobbyViewModel.state.collectAsState()
    val currentPlayer = lobbyState.players.firstOrNull { it.name == username }
    val currentRole = currentPlayer?.toPlayerRole() ?: PlayerRoles.NONE

    LobbyContent(
        username = username,
        lobbyState = lobbyState,
        currentRole = currentRole,
        onRoleSelect = { selectedRole ->
            selectedRole.toTeamAndRole()?.let { (team, role) ->
                lobbyViewModel.changeRole(username, role, team)
            }
        },
        onStartGame = {
            val lobbyCode = lobbyState.lobbyCode.orEmpty()
            val teamAndRole = currentRole.toTeamAndRole()

            if (lobbyCode.isNotBlank() && teamAndRole != null) {
                val (team, role) = teamAndRole

                gameViewModel.connect(
                    username = username,
                    lobbyCode = lobbyCode,
                    team = team.name,
                    role = role.name,
                )

                navController.navigate("${Screen.Gameboard.route}/$username/${currentRole.name}")
            }
        },
        onSettingsClick = {
            navController.navigate(Screen.Settings.route)
        },
    )
}

@Suppress("ktlint:standard:function-naming")
@Composable
fun LobbyContent(
    username: String,
    lobbyState: LobbyUiState,
    currentRole: PlayerRoles,
    onRoleSelect: (PlayerRoles) -> Unit,
    onStartGame: () -> Unit,
    onSettingsClick: () -> Unit,
) {
    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .background(Color(0xFFf0d8ce)),
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(top = 64.dp, start = 16.dp, end = 16.dp, bottom = 16.dp),
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
                players = lobbyState.players,
                onRoleSelect = onRoleSelect,
            )

            GameSettingsColumn(
                username = username,
                lobbyCode = lobbyState.lobbyCode,
                currentRole = currentRole,
                onStartGame = onStartGame,
            )

            TeamColumn(
                modifier = Modifier.weight(1f),
                color = Team.RED,
                gradient = redGradient,
                textColor = Color(0xFFDE8468),
                title = "RED TEAM",
                currentRole = currentRole,
                players = lobbyState.players,
                onRoleSelect = onRoleSelect,
            )
        }

        lobbyState.error?.let { error ->
            Text(
                text = error,
                color = Color(0xFFCF5530),
                fontSize = 16.sp,
                modifier =
                    Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 16.dp),
            )
        }

        SettingsCornerButton(
            onClick = onSettingsClick,
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
    players: List<Player>,
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
            players = players,
            onRoleSelect = onRoleSelect,
            modifier = cardModifier,
            title = "OPERATIVES",
        )

        RoleCard(
            role = if (color == Team.RED) PlayerRoles.RED_SPYMASTER else PlayerRoles.BLUE_SPYMASTER,
            currentRole = currentRole,
            players = players,
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
    players: List<Player>,
    onRoleSelect: (PlayerRoles) -> Unit,
    modifier: Modifier,
    title: String,
) {
    val playersInRole = players.filter { it.toPlayerRole() == role }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = title,
            color = Color.White,
            fontWeight = FontWeight.Bold,
        )

        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            if (playersInRole.isEmpty()) {
                Text(
                    text = "No players",
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 12.sp,
                )
            } else {
                playersInRole.forEach { player ->
                    Text(
                        text = player.name,
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight =
                            if (currentRole == role) {
                                FontWeight.Bold
                            } else {
                                FontWeight.Normal
                            },
                    )
                }
            }
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
    username: String,
    lobbyCode: String?,
    currentRole: PlayerRoles,
    onStartGame: () -> Unit,
) {
    val canStart =
        username.isNotBlank() &&
            !lobbyCode.isNullOrBlank() &&
            currentRole != PlayerRoles.NONE

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

            Text(
                text = "Lobby: ${lobbyCode ?: "-"}",
                color = Color.White,
                fontSize = 18.sp,
                modifier = Modifier.padding(bottom = 12.dp),
            )

            Text(
                text = "Player: ${username.ifBlank { "-" }}",
                color = Color.White,
                fontSize = 16.sp,
                modifier = Modifier.padding(bottom = 8.dp),
            )

            Text(
                text = "Role: ${currentRole.name}",
                color = Color.White,
                fontSize = 16.sp,
            )

            AppButton(
                text = "TIMER: OFF",
                onClick = { },
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(top = 24.dp),
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
            onClick = onStartGame,
            modifier =
                Modifier
                    .align(Alignment.CenterHorizontally)
                    .width(400.dp)
                    .height(70.dp)
                    .fillMaxWidth(0.5f)
                    .padding(top = 12.dp),
            style =
                AppButtonStyle(
                    enabled = canStart,
                    backgroundBrush = greenGradient,
                    fontSize = 28.sp,
                    lineHeight = 30.sp,
                ),
        )
    }
}
