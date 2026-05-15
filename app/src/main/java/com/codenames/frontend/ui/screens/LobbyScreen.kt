package com.codenames.frontend.ui.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.codenames.frontend.data.model.LobbyUiState
import com.codenames.frontend.data.model.enums.Team
import com.codenames.frontend.ui.buttons.AppButton
import com.codenames.frontend.ui.buttons.AppButtonStyle
import com.codenames.frontend.ui.buttons.AppButtonType
import com.codenames.frontend.ui.buttons.SettingsCornerButton
import com.codenames.frontend.ui.navigation.Screen
import com.codenames.frontend.ui.roles.PlayerRoles
import com.codenames.frontend.ui.theme.blueGradient
import com.codenames.frontend.ui.theme.brownGradient
import com.codenames.frontend.ui.theme.greenGradient
import com.codenames.frontend.ui.theme.redGradient
import com.codenames.frontend.ui.toPlayerRole
import com.codenames.frontend.ui.toTeamAndRole
import com.codenames.frontend.viewmodel.GameViewModel
import com.codenames.frontend.viewmodel.LobbyViewModel
import com.codenames.frontend.viewmodel.SessionViewModel

private const val JOIN_TEAM: String = "JOIN TEAM"

@Suppress("ktlint:standard:function-naming")
@Composable
fun LobbyScreen(
    navController: NavHostController,
    viewModel: LobbyViewModel,
    sessionViewModel: SessionViewModel,
    gameViewModel: GameViewModel,
) {
    val usernameState by sessionViewModel.username.collectAsState()
    val lobbyUiState by viewModel.state.collectAsState()
    val currentPlayer = lobbyUiState.players.firstOrNull { it.name == usernameState.username }
    val currentRole = currentPlayer?.toPlayerRole() ?: PlayerRoles.NONE

    val onStartGame = {
        val lobbyCode = lobbyUiState.lobbyCode.orEmpty()
        val teamAndRole = currentRole.toTeamAndRole()

        if (lobbyCode.isNotBlank() && teamAndRole != null) {
            val (team, role) = teamAndRole

            gameViewModel.connect(
                username = usernameState.username,
                lobbyCode = lobbyCode,
                team = team.name,
                role = role.name,
            )

            navController.navigate("${Screen.Gameboard.route}/${usernameState.username}/${currentRole.name}")
        }
    }

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
                    .padding(top = 40.dp, start = 16.dp, end = 16.dp, bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            TeamColumn(
                modifier = Modifier.weight(1f),
                color = Team.BLUE,
                gradient = blueGradient,
                textColor = Color(0xFF42A5F5),
                title = "BLUE TEAM",
                onRoleSelect = { viewModel.changeRole(it, usernameState.username) },
                lobbyUiState = lobbyUiState,
            )

            GameSettingsColumn(
                modifier =
                    Modifier
                        .padding(horizontal = 24.dp)
                        .fillMaxHeight(),
                navController = navController,
                lobbyCode = lobbyUiState.lobbyCode ?: "",
                currentRole = currentRole,
                onStartGame = onStartGame,
            )

            TeamColumn(
                modifier = Modifier.weight(1f),
                color = Team.RED,
                gradient = redGradient,
                textColor = Color(0xFFDE8468),
                title = "RED TEAM",
                onRoleSelect = { viewModel.changeRole(it, usernameState.username) },
                lobbyUiState = lobbyUiState,
            )
        }

        lobbyUiState.error?.let { error ->
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
            onClick = {
                navController.navigate(Screen.Settings.route)
            },
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
    onRoleSelect: (PlayerRoles) -> Unit,
    lobbyUiState: LobbyUiState,
) {
    val align = if (color == Team.RED) Alignment.End else Alignment.Start

    Column(
        modifier =
            modifier
                .fillMaxWidth(0.5f),
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
            onRoleSelect = onRoleSelect,
            modifier = cardModifier,
            title = "OPERATIVES",
            players = if (color == Team.RED) lobbyUiState.redOperatives else lobbyUiState.blueOperatives,
        )

        RoleCard(
            role = if (color == Team.RED) PlayerRoles.RED_SPYMASTER else PlayerRoles.BLUE_SPYMASTER,
            onRoleSelect = onRoleSelect,
            modifier = cardModifier,
            title = "SPYMASTERS",
            players = if (color == Team.RED) lobbyUiState.redSpymasters else lobbyUiState.blueSpymasters,
        )
    }
}

@Suppress("ktlint:standard:function-naming")
@Composable
fun RoleCard(
    role: PlayerRoles,
    onRoleSelect: (PlayerRoles) -> Unit,
    modifier: Modifier,
    title: String,
    players: List<String> = emptyList(),
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(title, color = Color.White, fontWeight = FontWeight.Bold)
        Log.d("LobbyScreen", "Players: $players")
        if (players.isEmpty()) {
            Text(
                text = "No players",
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 12.sp,
            )
        } else {
            for (player in players) {
                Text(player, color = Color.White)
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
    modifier: Modifier,
    navController: NavController,
    lobbyCode: String,
    viewModel: LobbyViewModel = hiltViewModel(navController.getBackStackEntry("main_graph")),
    sessionViewModel: SessionViewModel = hiltViewModel(navController.getBackStackEntry("main_graph")),
    currentRole: PlayerRoles,
    onStartGame: () -> Unit,
) {
    val usernameState by sessionViewModel.username.collectAsState()
    val canStart =
        usernameState.username.isNotBlank() &&
            !lobbyCode.isNullOrBlank() &&
            currentRole != PlayerRoles.NONE

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "LOBBY CODE: $lobbyCode",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier =
                Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 8.dp),
        )

        Spacer(modifier = Modifier.weight(1f))

        Column(
            modifier =
                Modifier
                    .align(Alignment.CenterHorizontally)
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

        Spacer(modifier = Modifier.weight(1f))

        AppButton(
            text = "START GAME",
            onClick = onStartGame,
            modifier =
                Modifier
                    .align(Alignment.CenterHorizontally)
                    .fillMaxWidth(0.5f)
                    .padding(top = 16.dp),
            style =
                AppButtonStyle(
                    enabled = canStart,
                    backgroundBrush = greenGradient,
                    fontSize = 20.sp,
                    type = AppButtonType.PRIMARY,
                    contentPadding = PaddingValues(horizontal = 24.dp, vertical = 0.dp),
                ),
        )

        AppButton(
            text = "LEAVE LOBBY",
            onClick = {
                val onResult = { successful: Boolean ->
                    if (successful) {
                        navController.navigate(Screen.Start.route) {
                            popUpTo(Screen.Start.route) { inclusive = true }
                        }
                    }
                }
                viewModel.leaveLobby(username = usernameState.username, onResult = onResult)
            },
            modifier =
                Modifier
                    .align(Alignment.CenterHorizontally)
                    .fillMaxWidth(0.5f)
                    .padding(top = 16.dp)
                    .padding(bottom = 16.dp),
            style =
                AppButtonStyle(
                    backgroundBrush = brownGradient,
                    fontSize = 20.sp,
                    contentColor = Color.Black,
                    type = AppButtonType.SECONDARY,
                    contentPadding = PaddingValues(horizontal = 24.dp, vertical = 0.dp),
                ),
        )
    }
}
