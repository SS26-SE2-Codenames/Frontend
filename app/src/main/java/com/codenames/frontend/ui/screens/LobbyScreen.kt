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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.codenames.frontend.data.model.LobbyUiState
import com.codenames.frontend.data.model.enums.ConnectionState
import com.codenames.frontend.data.model.enums.Team
import com.codenames.frontend.ui.buttons.AppButton
import com.codenames.frontend.ui.buttons.AppButtonStyle
import com.codenames.frontend.ui.buttons.AppButtonType
import com.codenames.frontend.ui.buttons.SettingsCornerButton
import com.codenames.frontend.ui.navigation.Screen
import com.codenames.frontend.ui.roles.PlayerRoles
import com.codenames.frontend.ui.theme.AppBackground
import com.codenames.frontend.ui.theme.AppBlack
import com.codenames.frontend.ui.theme.AppBlueLight
import com.codenames.frontend.ui.theme.AppMutedDark
import com.codenames.frontend.ui.theme.AppRed
import com.codenames.frontend.ui.theme.AppRedLight
import com.codenames.frontend.ui.theme.AppWhite
import com.codenames.frontend.ui.theme.LocalResponsiveDimensions
import com.codenames.frontend.ui.theme.blueGradient
import com.codenames.frontend.ui.theme.brownGradient
import com.codenames.frontend.ui.theme.greenGradient
import com.codenames.frontend.ui.theme.redGradient
import com.codenames.frontend.ui.toPlayerRole
import com.codenames.frontend.ui.toTeamAndRole
import com.codenames.frontend.viewmodel.ChatViewModel
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
    chatViewModel: ChatViewModel,
) {
    val dimensions = LocalResponsiveDimensions.current
    val userState by sessionViewModel.userState.collectAsState()
    val lobbyUiState by viewModel.state.collectAsState()
    val currentPlayer = lobbyUiState.players.firstOrNull { it.name == userState.username }
    val currentRole = currentPlayer?.toPlayerRole() ?: PlayerRoles.NONE
    val connectionState by gameViewModel.connectionState.collectAsState()

    val onStartGame = {
        viewModel.sendStartGame(userState.username)
    }

    LaunchedEffect(userState.userId) {
        if (userState.userId != null) {
            sessionViewModel.persistUserState()
        }
    }

    LaunchedEffect(lobbyUiState.isGameStarted) {
        if (lobbyUiState.isGameStarted) {
            val lobbyCode = lobbyUiState.lobbyCode.orEmpty()
            val teamAndRole = currentRole.toTeamAndRole()

            Log.d("LobbyScreen", "Lobby UI state is started, recomposing")

            if (lobbyCode.isNotBlank() && teamAndRole != null) {
                gameViewModel.connect(
                    lobbyCode = lobbyCode,
                    isHost = viewModel.getIsHost(userState.username),
                )
            }
        }
    }

    LaunchedEffect(connectionState) {
        val lobbyCode = lobbyUiState.lobbyCode.orEmpty()
        val teamAndRole = currentRole.toTeamAndRole()

        if (connectionState == ConnectionState.CONNECTED) {
            navController.navigate(Screen.Gameboard.route)

            if (lobbyCode.isNotBlank() && teamAndRole != null) {
                val (team, role) = teamAndRole

                chatViewModel.subscribeToChats(
                    username = userState.username,
                    lobbyCode = lobbyCode,
                    team = team.name,
                    role = role.name,
                )

                sessionViewModel.persistLobbyState(
                    lobbyCode,
                    role,
                    team,
                )
            }
        }
    }

    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .background(AppBackground),
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(
                        top = dimensions.gameTopPadding,
                        start = dimensions.screenPadding,
                        end = dimensions.screenPadding,
                        bottom = dimensions.screenPadding,
                    ),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            TeamColumn(
                modifier = Modifier.weight(1f),
                color = Team.BLUE,
                gradient = blueGradient,
                textColor = AppBlueLight,
                title = "BLUE TEAM",
                onRoleSelect = { viewModel.changeRole(it, userState.username) },
                lobbyUiState = lobbyUiState,
            )

            GameSettingsColumn(
                modifier =
                    Modifier
                        .padding(horizontal = dimensions.sectionSpacing)
                        .fillMaxHeight(),
                navController = navController,
                lobbyCode = lobbyUiState.lobbyCode ?: "",
                currentRole = currentRole,
                onStartGame = onStartGame,
                viewModel = viewModel,
                sessionViewModel = sessionViewModel,
            )

            TeamColumn(
                modifier = Modifier.weight(1f),
                color = Team.RED,
                gradient = redGradient,
                textColor = AppRedLight,
                title = "RED TEAM",
                onRoleSelect = { viewModel.changeRole(it, userState.username) },
                lobbyUiState = lobbyUiState,
            )
        }

        lobbyUiState.error?.let { error ->
            Text(
                text = error,
                color = AppRed,
                fontSize = dimensions.bodyFontSize,
                modifier =
                    Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = dimensions.screenPadding),
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
    val dimensions = LocalResponsiveDimensions.current
    val align = if (color == Team.RED) Alignment.End else Alignment.Start

    Column(
        modifier = modifier.fillMaxWidth(0.5f),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        val cardModifier =
            Modifier
                .align(align)
                .width(dimensions.lobbyRoleCardWidth)
                .height(dimensions.lobbyRoleCardHeight)
                .padding(start = dimensions.smallSpacing, bottom = dimensions.itemSpacing)
                .background(gradient, RoundedCornerShape(12.dp))
                .padding(dimensions.itemSpacing)

        Text(
            text = title,
            color = textColor,
            fontSize = dimensions.bodyFontSize,
            fontWeight = FontWeight.Bold,
            modifier =
                Modifier
                    .align(align)
                    .padding(horizontal = dimensions.smallSpacing)
                    .padding(bottom = dimensions.smallSpacing),
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
    val dimensions = LocalResponsiveDimensions.current

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = title,
            color = AppWhite,
            fontWeight = FontWeight.Bold,
            fontSize = dimensions.smallFontSize,
        )

        if (players.isEmpty()) {
            Text(
                text = "No players",
                color = AppWhite.copy(alpha = 0.7f),
                fontSize = dimensions.smallFontSize,
            )
        } else {
            for (player in players) {
                Text(
                    text = player,
                    color = AppWhite,
                    fontSize = dimensions.smallFontSize,
                )
            }
        }

        AppButton(
            text = JOIN_TEAM,
            onClick = { onRoleSelect(role) },
            style =
                AppButtonStyle(
                    backgroundBrush = greenGradient,
                    fontSize = dimensions.smallFontSize,
                    lineHeight = dimensions.bodyFontSize,
                    contentPadding =
                        PaddingValues(
                            horizontal = dimensions.itemSpacing,
                            vertical = dimensions.smallSpacing,
                        ),
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
    viewModel: LobbyViewModel,
    sessionViewModel: SessionViewModel,
    currentRole: PlayerRoles,
    onStartGame: () -> Unit,
) {
    val dimensions = LocalResponsiveDimensions.current
    val userState by sessionViewModel.userState.collectAsState()
    val canStart =
        userState.username.isNotBlank() &&
            lobbyCode.isNotBlank() &&
            currentRole != PlayerRoles.NONE &&
            viewModel.getIsHost(userState.username)

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "LOBBY CODE: $lobbyCode",
            fontSize = dimensions.bodyFontSize,
            fontWeight = FontWeight.Bold,
            modifier =
                Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = dimensions.smallSpacing),
        )

        Spacer(modifier = Modifier.weight(1f))

        Column(
            modifier =
                Modifier
                    .align(Alignment.CenterHorizontally)
                    .fillMaxWidth(dimensions.lobbyCenterColumnWidthFraction)
                    .background(brownGradient, RoundedCornerShape(12.dp))
                    .padding(dimensions.itemSpacing),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top,
        ) {
            Text(
                text = "GAME SETTINGS",
                color = AppWhite,
                fontWeight = FontWeight.Bold,
                fontSize = dimensions.smallFontSize,
                modifier = Modifier.padding(bottom = dimensions.itemSpacing),
            )

            AppButton(
                text = "TIMER: OFF",
                onClick = { /* TODO: Timer Logik */ },
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(dimensions.secondaryButtonHeight)
                        .padding(bottom = dimensions.smallSpacing),
                style =
                    AppButtonStyle(
                        containerColor = AppMutedDark,
                        contentColor = AppWhite,
                        fontSize = dimensions.smallFontSize,
                        lineHeight = dimensions.bodyFontSize,
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
                    .fillMaxWidth(dimensions.lobbyCenterColumnWidthFraction)
                    .height(dimensions.secondaryButtonHeight)
                    .padding(top = dimensions.itemSpacing),
            style =
                AppButtonStyle(
                    enabled = canStart,
                    backgroundBrush = greenGradient,
                    fontSize = dimensions.bodyFontSize,
                    lineHeight = dimensions.buttonLineHeight,
                    type = AppButtonType.PRIMARY,
                    contentPadding =
                        PaddingValues(
                            horizontal = dimensions.itemSpacing,
                            vertical = dimensions.smallSpacing,
                        ),
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
                viewModel.leaveLobby(username = userState.username, onResult = onResult)
            },
            modifier =
                Modifier
                    .align(Alignment.CenterHorizontally)
                    .fillMaxWidth(dimensions.lobbyCenterColumnWidthFraction)
                    .height(dimensions.secondaryButtonHeight)
                    .padding(top = dimensions.itemSpacing)
                    .padding(bottom = dimensions.itemSpacing),
            style =
                AppButtonStyle(
                    backgroundBrush = brownGradient,
                    fontSize = dimensions.bodyFontSize,
                    lineHeight = dimensions.buttonLineHeight,
                    contentColor = AppBlack,
                    type = AppButtonType.SECONDARY,
                    contentPadding =
                        PaddingValues(
                            horizontal = dimensions.itemSpacing,
                            vertical = dimensions.smallSpacing,
                        ),
                ),
        )
    }
}
