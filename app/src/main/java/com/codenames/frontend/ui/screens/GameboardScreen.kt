package com.codenames.frontend.ui.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.codenames.frontend.data.model.ChatDomainModel
import com.codenames.frontend.data.model.ChatLists
import com.codenames.frontend.data.model.GameCard
import com.codenames.frontend.data.model.GameState
import com.codenames.frontend.data.model.enums.CardType
import com.codenames.frontend.data.model.enums.ChatTab
import com.codenames.frontend.data.model.enums.Team
import com.codenames.frontend.ui.buttons.AppButton
import com.codenames.frontend.ui.buttons.AppButtonStyle
import com.codenames.frontend.ui.buttons.AppButtonType
import com.codenames.frontend.ui.buttons.SettingsCornerButton
import com.codenames.frontend.ui.composables.GameBoardGrid
import com.codenames.frontend.ui.inputs.AppTextField
import com.codenames.frontend.ui.inputs.AppTextFieldKeyboard
import com.codenames.frontend.ui.inputs.AppTextFieldState
import com.codenames.frontend.ui.inputs.AppTextFieldStyle
import com.codenames.frontend.ui.roles.PlayerRoles
import com.codenames.frontend.ui.theme.AppBackground
import com.codenames.frontend.ui.theme.AppBlack
import com.codenames.frontend.ui.theme.AppBlue
import com.codenames.frontend.ui.theme.AppBlueTeamBackground
import com.codenames.frontend.ui.theme.AppGreen
import com.codenames.frontend.ui.theme.AppInk
import com.codenames.frontend.ui.theme.AppInkOverlay
import com.codenames.frontend.ui.theme.AppLightGray
import com.codenames.frontend.ui.theme.AppMutedDark
import com.codenames.frontend.ui.theme.AppRed
import com.codenames.frontend.ui.theme.AppRedTeamBackground
import com.codenames.frontend.ui.theme.AppSurface
import com.codenames.frontend.ui.theme.AppSurfaceOverlay
import com.codenames.frontend.ui.theme.AppWhite
import com.codenames.frontend.ui.theme.LocalResponsiveDimensions
import com.codenames.frontend.ui.theme.blueGradient
import com.codenames.frontend.ui.theme.greenGradient
import com.codenames.frontend.ui.theme.redGradient

private data class BoardSectionState(
    val userRole: PlayerRoles,
    val cards: List<GameCard>,
    val currentBlueFound: Int,
    val currentRedFound: Int,
    val isSpymaster: Boolean,
)

private data class BoardSelectionState(
    val canSelectCards: Boolean,
    val selectedCardPositions: List<Int>,
    val remainingGuesses: Int,
)

private data class BoardTransformState(
    val scale: Float,
    val offset: Offset,
)

private data class ChatOverlayState(
    val isVisible: Boolean,
    val input: String,
    val messages: ChatLists,
    val selectedTab: ChatTab,
    val availableTabs: List<ChatTab>,
)

private data class ChatOverlayActions(
    val onTabSelected: (ChatTab) -> Unit,
    val onInputChange: (String) -> Unit,
    val onSendMessage: (ChatTab, String) -> Unit,
)

@Suppress("ktlint:standard:function-naming")
@Composable
fun GameboardScreen(
    userRole: PlayerRoles,
    gameState: GameState,
    onHintChange: (String, Int) -> Unit,
    onReveal: (List<Int>) -> Unit,
    modifier: Modifier = Modifier,
    onSendChatMessage: (ChatTab, String) -> Unit = { _, _ -> },
    onSettingsClick: (() -> Unit)? = null,
) {
    val dimensions = LocalResponsiveDimensions.current

    val currentHint = gameState.currentHint
    val cards = gameState.cards
    val currentTurn = gameState.currentTurn
    val winner = gameState.winner
    val remainingGuesses = gameState.remainingGuesses
    val numGuesses = gameState.numGuesses
    val chatLists = gameState.chatLists
    val currentRedFound = gameState.currentRedFound
    val currentBlueFound = gameState.currentBlueFound
    val availableChatTabs = gameState.availableChatTabs

    var hintInput by rememberSaveable { mutableStateOf("") }
    var countInput by rememberSaveable { mutableStateOf("") }
    var chatInput by rememberSaveable { mutableStateOf("") }
    var isChatOpen by rememberSaveable { mutableStateOf(false) }
    var selectedChatTab by rememberSaveable { mutableStateOf(ChatTab.GLOBAL) }
    var selectedCardPositions by remember { mutableStateOf(emptyList<Int>()) }

    val isSpymaster =
        userRole == PlayerRoles.BLUE_SPYMASTER || userRole == PlayerRoles.RED_SPYMASTER
    val isActiveSpymaster = userRole == currentTurn && isSpymaster
    val canSelectCards = userRole == currentTurn && !isSpymaster && remainingGuesses > 0
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    var scale by remember { mutableFloatStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }

    val onInputChange: (String) -> Unit = { hintInput = it }

    LaunchedEffect(cards, currentTurn, remainingGuesses) {
        selectedCardPositions =
            selectedCardPositions
                .filter { position -> cards.getOrNull(position)?.revealed == false }
                .take(remainingGuesses.coerceAtLeast(0))
    }

    Box(
        modifier =
            modifier
                .fillMaxSize()
                .background(getTeamBackgroundColor(currentTurn)),
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(
                        top = dimensions.gameTopPadding,
                        start = dimensions.screenPadding,
                        end = dimensions.screenPadding,
                        bottom = dimensions.screenPadding,
                    ),
        ) {
            GameStatusBar(
                currentTurn = currentTurn,
                winner = winner,
                remainingGuesses = remainingGuesses,
                numGuesses = numGuesses,
            )

            ChatToggle(
                isVisible = availableChatTabs.isNotEmpty(),
                isChatOpen = isChatOpen,
                onClick = { isChatOpen = !isChatOpen },
            )

            Spacer(modifier = Modifier.height(dimensions.gameBoardTopSpacing))

            GameBoardSection(
                state =
                    BoardSectionState(
                        userRole = userRole,
                        cards = cards,
                        currentBlueFound = currentBlueFound,
                        currentRedFound = currentRedFound,
                        isSpymaster = isSpymaster,
                    ),
                selectionState =
                    BoardSelectionState(
                        canSelectCards = canSelectCards,
                        selectedCardPositions = selectedCardPositions,
                        remainingGuesses = remainingGuesses,
                    ),
                transformState =
                    BoardTransformState(
                        scale = scale,
                        offset = offset,
                    ),
                onTransform = { pan, zoom ->
                    scale = (scale * zoom).coerceIn(0.5f, 3f)
                    offset += pan
                },
                onSelectionChange = { selectedCardPositions = it },
                onReveal = onReveal,
                modifier =
                    Modifier
                        .weight(1f)
                        .fillMaxWidth(),
            )

            HintSection(
                isActiveSpymaster,
                currentHint,
                hintInput,
                countInput,
                onHintChange = onHintChange,
                onInputChange,
                onCountChange = { countInput = it },
                keyboardController,
                focusManager,
            )
        }

        GameChatOverlay(
            state =
                ChatOverlayState(
                    isVisible = availableChatTabs.isNotEmpty() && isChatOpen,
                    input = chatInput,
                    messages = chatLists,
                    selectedTab = selectedChatTab,
                    availableTabs = availableChatTabs,
                ),
            actions =
                ChatOverlayActions(
                    onTabSelected = { selectedChatTab = it },
                    onInputChange = { chatInput = it },
                    onSendMessage = onSendChatMessage,
                ),
            modifier =
                Modifier
                    .align(Alignment.Center)
                    .padding(end = dimensions.itemSpacing, bottom = dimensions.itemSpacing)
                    .width(dimensions.gameChatWidth)
                    .fillMaxHeight(dimensions.gameChatHeightFraction),
        )

        DeselectAllButton(
            isVisible = canSelectCards && selectedCardPositions.isNotEmpty(),
            onClick = { selectedCardPositions = emptyList() },
            modifier =
                Modifier
                    .align(Alignment.BottomCenter)
                    .padding(
                        start = dimensions.screenPadding,
                        end = dimensions.screenPadding,
                        bottom = dimensions.screenPadding,
                    ),
        )

        onSettingsClick?.let { openSettings ->
            SettingsCornerButton(
                onClick = openSettings,
            )
        }
    }
}

private fun updateSelectedCardPositions(
    position: Int,
    selectedCardPositions: List<Int>,
    remainingGuesses: Int,
    onReveal: (List<Int>) -> Unit,
): List<Int> =
    when {
        position in selectedCardPositions -> {
            onReveal(listOf(position))
            selectedCardPositions - position
        }
        selectedCardPositions.size < remainingGuesses -> selectedCardPositions + position
        else -> selectedCardPositions
    }

@Suppress("ktlint:standard:function-naming")
@Composable
private fun ChatToggle(
    isVisible: Boolean,
    isChatOpen: Boolean,
    onClick: () -> Unit,
) {
    val dimensions = LocalResponsiveDimensions.current

    if (isVisible) {
        ChatToggleButton(
            isChatOpen = isChatOpen,
            onClick = onClick,
            modifier =
                Modifier
                    .padding(end = dimensions.itemSpacing, bottom = dimensions.itemSpacing),
        )
    }
}

@Suppress("ktlint:standard:function-naming")
@Composable
private fun GameBoardSection(
    state: BoardSectionState,
    selectionState: BoardSelectionState,
    transformState: BoardTransformState,
    onTransform: (Offset, Float) -> Unit,
    onSelectionChange: (List<Int>) -> Unit,
    onReveal: (List<Int>) -> Unit,
    modifier: Modifier = Modifier,
) {
    val dimensions = LocalResponsiveDimensions.current

    Row(modifier = modifier) {
        TeamSidebar(
            state.userRole,
            color = Team.BLUE,
            teamFound = state.currentBlueFound,
            textColor = AppBlue,
            gradient = blueGradient,
        )

        BoardContent(
            state = state,
            selectionState = selectionState,
            transformState = transformState,
            onCardClick = { position ->
                if (selectionState.canSelectCards) {
                    onSelectionChange(
                        updateSelectedCardPositions(
                            position = position,
                            selectedCardPositions = selectionState.selectedCardPositions,
                            remainingGuesses = selectionState.remainingGuesses,
                            onReveal = onReveal,
                        ),
                    )
                }
            },
            onTransform = onTransform,
            modifier =
                Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .padding(horizontal = dimensions.smallSpacing),
        )

        TeamSidebar(
            state.userRole,
            color = Team.RED,
            teamFound = state.currentRedFound,
            textColor = AppRed,
            gradient = redGradient,
        )
    }
}

@Suppress("ktlint:standard:function-naming")
@Composable
private fun BoardContent(
    state: BoardSectionState,
    selectionState: BoardSelectionState,
    transformState: BoardTransformState,
    onCardClick: (Int) -> Unit,
    onTransform: (Offset, Float) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (state.cards.isEmpty()) {
        WaitingForGameState(modifier = modifier)
    } else {
        GameBoardGrid(
            cards = state.cards,
            scale = transformState.scale,
            offset = transformState.offset,
            isSpymaster = state.isSpymaster,
            selectedCardPositions = selectionState.selectedCardPositions.toSet(),
            onCardClick = onCardClick,
            modifier =
                modifier
                    .clipToBounds()
                    .pointerInput(Unit) {
                        detectTransformGestures { _, pan, zoom, _ ->
                            onTransform(pan, zoom)
                        }
                    },
        )
    }
}

@Suppress("ktlint:standard:function-naming")
@Composable
private fun WaitingForGameState(modifier: Modifier = Modifier) {
    val dimensions = LocalResponsiveDimensions.current

    Box(
        modifier =
            modifier
                .background(AppSurface, RoundedCornerShape(12.dp)),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "Waiting for game state...",
            color = AppInk,
            fontSize = dimensions.bodyFontSize,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Suppress("ktlint:standard:function-naming")
@Composable
private fun DeselectAllButton(
    isVisible: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val dimensions = LocalResponsiveDimensions.current

    if (isVisible) {
        AppButton(
            text = "Deselect all",
            onClick = onClick,
            modifier =
                modifier
                    .fillMaxWidth()
                    .height(dimensions.secondaryButtonHeight),
            style =
                AppButtonStyle(
                    containerColor = AppGreen,
                    contentColor = AppWhite,
                    fontSize = dimensions.bodyFontSize,
                ),
        )
    }
}

@Suppress("ktlint:standard:function-naming")
@Composable
private fun GameChatOverlay(
    state: ChatOverlayState,
    actions: ChatOverlayActions,
    modifier: Modifier = Modifier,
) {
    if (state.isVisible) {
        ChatWindow(
            chatInput = state.input,
            messages = state.messages,
            selectedTab = getActiveChatTab(state.selectedTab, state.availableTabs),
            availableTabs = state.availableTabs,
            onTabSelected = actions.onTabSelected,
            onChatInputChange = actions.onInputChange,
            onSendClick = actions.onSendMessage,
            modifier = modifier,
        )
    }
}

private fun getActiveChatTab(
    selectedChatTab: ChatTab,
    availableChatTabs: List<ChatTab>,
): ChatTab =
    if (selectedChatTab in availableChatTabs) {
        selectedChatTab
    } else {
        availableChatTabs.firstOrNull() ?: ChatTab.GLOBAL
    }

@Suppress("ktlint:standard:function-naming")
@Composable
fun GameStatusBar(
    currentTurn: PlayerRoles?,
    winner: Team?,
    remainingGuesses: Int,
    numGuesses: Int,
) {
    val dimensions = LocalResponsiveDimensions.current

    Log.d("GameboardScreen", "GameStatusBar: Updated guesses. Remaining guesses: $remainingGuesses")

    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .height(dimensions.gameStatusBarHeight),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        val statusText =
            when {
                winner != null -> "Winner: $winner"
                currentTurn != null -> "Turn: ${currentTurn.name} | Remaining Guesses: $remainingGuesses/$numGuesses"
                else -> "Waiting for turn..."
            }

        Text(
            text = statusText,
            color = AppInk,
            fontSize = dimensions.bodyFontSize,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Suppress("ktlint:standard:function-naming")
@Composable
fun ChatToggleButton(
    isChatOpen: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val dimensions = LocalResponsiveDimensions.current

    AppButton(
        text = "Chat",
        onClick = onClick,
        modifier =
            modifier
                .width(dimensions.returnButtonWidth)
                .height(dimensions.secondaryButtonHeight),
        style =
            AppButtonStyle(
                containerColor = if (isChatOpen) AppMutedDark else AppInk,
                contentColor = AppWhite,
                fontSize = dimensions.bodyFontSize,
                lineHeight = dimensions.buttonLineHeight,
            ),
    )
}

@Suppress("ktlint:standard:function-naming")
@Composable
fun ChatWindow(
    chatInput: String,
    messages: ChatLists,
    selectedTab: ChatTab,
    availableTabs: List<ChatTab>,
    onTabSelected: (ChatTab) -> Unit,
    onChatInputChange: (String) -> Unit,
    onSendClick: (ChatTab, String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val dimensions = LocalResponsiveDimensions.current

    Column(
        modifier =
            modifier
                .background(
                    color = AppInkOverlay,
                    shape = RoundedCornerShape(12.dp),
                ).padding(dimensions.itemSpacing),
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(dimensions.smallSpacing),
        ) {
            availableTabs.forEach { tab ->
                AppButton(
                    text = tab.title,
                    onClick = { onTabSelected(tab) },
                    modifier =
                        Modifier
                            .weight(1f)
                            .height(dimensions.secondaryButtonHeight),
                    style =
                        AppButtonStyle(
                            type = AppButtonType.PRIMARY,
                            containerColor = if (selectedTab == tab) Color.Unspecified else Color.Transparent,
                            contentColor = if (selectedTab == tab) Color.Unspecified else AppLightGray,
                            fontSize = dimensions.smallFontSize,
                            lineHeight = dimensions.bodyFontSize,
                            contentPadding =
                                PaddingValues(
                                    horizontal = dimensions.smallSpacing,
                                    vertical = dimensions.smallSpacing,
                                ),
                        ),
                )
            }
        }

        Spacer(modifier = Modifier.height(dimensions.itemSpacing))

        ChatMessagesArea(
            messages = messages,
            selectedTab = selectedTab,
            modifier = Modifier.weight(1f).fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(dimensions.itemSpacing))

        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(dimensions.primaryButtonHeight),
            horizontalArrangement = Arrangement.spacedBy(dimensions.smallSpacing),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            AppTextField(
                value = chatInput,
                onValueChange = onChatInputChange,
                modifier =
                    Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                state =
                    AppTextFieldState(
                        label = "Message",
                        placeholder = "Type message...",
                    ),
                style =
                    AppTextFieldStyle(
                        containerColor = AppSurface,
                        contentColor = AppInk,
                        fontSize = dimensions.smallFontSize,
                        lineHeight = dimensions.bodyFontSize,
                    ),
            )

            AppButton(
                text = "Send",
                onClick = {
                    val trimmedMessage = chatInput.trim()
                    if (trimmedMessage.isNotBlank()) {
                        onSendClick(selectedTab, trimmedMessage)
                        onChatInputChange("")
                    }
                },
                modifier =
                    Modifier
                        .width(dimensions.returnButtonWidth)
                        .fillMaxHeight(),
                style =
                    AppButtonStyle(
                        backgroundBrush = greenGradient,
                        fontSize = dimensions.smallFontSize,
                        lineHeight = dimensions.bodyFontSize,
                    ),
            )
        }
    }
}

@Suppress("ktlint:standard:function-naming")
@Composable
fun ChatMessagesArea(
    messages: ChatLists,
    selectedTab: ChatTab,
    modifier: Modifier = Modifier,
) {
    val dimensions = LocalResponsiveDimensions.current

    val visibleMessages =
        when (selectedTab) {
            ChatTab.GLOBAL -> messages.lobbyMessages
            ChatTab.TEAM -> messages.teamMessages
            ChatTab.OPERATIVES -> messages.operativeMessages
        }

    Column(
        modifier =
            modifier
                .background(
                    color = AppSurfaceOverlay,
                    shape = RoundedCornerShape(8.dp),
                ).padding(dimensions.itemSpacing),
        verticalArrangement = Arrangement.Top,
    ) {
        Text(
            text = "${selectedTab.title} Chat",
            color = AppInk,
            fontSize = dimensions.bodyFontSize,
            fontWeight = FontWeight.Bold,
        )

        Spacer(modifier = Modifier.height(dimensions.smallSpacing))

        if (visibleMessages.isEmpty()) {
            Text(
                text = "No messages yet.",
                color = AppInk,
                fontSize = dimensions.smallFontSize,
            )
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(dimensions.smallSpacing),
            ) {
                items(visibleMessages) { message ->
                    ChatMessageBubble(message = message)
                }
            }
        }
    }
}

@Suppress("ktlint:standard:function-naming")
@Composable
fun ChatMessageBubble(message: ChatDomainModel) {
    val dimensions = LocalResponsiveDimensions.current

    val alignment = if (message.isFromMe) Alignment.End else Alignment.Start
    val bubbleColor = if (message.isFromMe) AppGreen else AppSurface
    val textColor = if (message.isFromMe) AppWhite else AppInk

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = alignment,
    ) {
        Text(
            text = message.sender,
            color = AppInk,
            fontSize = dimensions.smallFontSize,
            fontWeight = FontWeight.Bold,
        )

        Box(
            modifier =
                Modifier
                    .fillMaxWidth(0.78f)
                    .background(bubbleColor, RoundedCornerShape(8.dp))
                    .padding(dimensions.smallSpacing),
        ) {
            Text(
                text = message.text,
                color = textColor,
                fontSize = dimensions.smallFontSize,
            )
        }
    }
}

@Suppress("ktlint:standard:function-naming")
@Composable
fun TeamSidebar(
    userRole: PlayerRoles,
    color: Team,
    teamFound: Int,
    textColor: Color,
    gradient: Brush,
) {
    val dimensions = LocalResponsiveDimensions.current

    val isRed = color == Team.RED
    val operative = if (isRed) PlayerRoles.RED_OPERATIVE else PlayerRoles.BLUE_OPERATIVE
    val spymaster = if (isRed) PlayerRoles.RED_SPYMASTER else PlayerRoles.BLUE_SPYMASTER
    val title = if (isRed) "RED TEAM" else "BLUE TEAM"

    Column(
        modifier =
            Modifier
                .width(dimensions.gameSidebarWidth)
                .fillMaxHeight(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            title,
            fontWeight = FontWeight.Bold,
            color = textColor,
            fontSize = dimensions.smallFontSize,
        )

        Spacer(modifier = Modifier.height(dimensions.smallSpacing))

        TeamRoleBox(
            title = "OPERATIVES",
            gradient = gradient,
            isCurrentUser = userRole == operative,
        )

        Spacer(modifier = Modifier.height(dimensions.smallSpacing))

        TeamRoleBox(
            title = "SPYMASTERS",
            gradient = gradient,
            isCurrentUser = userRole == spymaster,
        )

        Spacer(modifier = Modifier.height(dimensions.itemSpacing))

        Text(
            text = "$teamFound FOUND",
            color = textColor,
            fontWeight = FontWeight.ExtraBold,
            fontSize = dimensions.bodyFontSize,
        )
    }
}

@Suppress("ktlint:standard:function-naming")
@Composable
fun HintSection(
    isSpymaster: Boolean,
    currentHint: String,
    hintInput: String,
    countInput: String,
    onHintChange: (String, Int) -> Unit,
    onInputChange: (String) -> Unit,
    onCountChange: (String) -> Unit,
    keyboardController: SoftwareKeyboardController?,
    focusManager: FocusManager,
) {
    val dimensions = LocalResponsiveDimensions.current

    if (isSpymaster) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(dimensions.smallSpacing),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            AppTextField(
                value = hintInput,
                onValueChange = onInputChange,
                modifier =
                    Modifier
                        .weight(dimensions.gameHintInputWeight)
                        .height(dimensions.gameHintInputHeight),
                state =
                    AppTextFieldState(
                        label = "HINT",
                        placeholder = "Enter word...",
                    ),
                style =
                    AppTextFieldStyle(
                        fontSize = dimensions.bodyFontSize,
                        lineHeight = dimensions.buttonLineHeight,
                    ),
                keyboard =
                    AppTextFieldKeyboard(
                        actions =
                            KeyboardActions(
                                onSend = {
                                    val count = countInput.toIntOrNull() ?: 0
                                    if (hintInput.isNotBlank()) {
                                        onHintChange(hintInput.uppercase(), count)
                                        onInputChange("")
                                        onCountChange("")
                                        focusManager.clearFocus()
                                        keyboardController?.hide()
                                    }
                                },
                            ),
                    ),
            )

            AppTextField(
                value = countInput,
                onValueChange = onCountChange,
                modifier =
                    Modifier
                        .width(dimensions.gameHintCountWidth)
                        .height(dimensions.gameHintInputHeight),
                state = AppTextFieldState(label = "COUNT", placeholder = "0"),
                style =
                    AppTextFieldStyle(
                        fontSize = dimensions.bodyFontSize,
                        lineHeight = dimensions.buttonLineHeight,
                    ),
            )

            AppButton(
                text = "SEND",
                onClick = {
                    val count = countInput.toIntOrNull() ?: 0
                    if (hintInput.isNotBlank()) {
                        onHintChange(hintInput.uppercase(), count)
                        onInputChange("")
                        onCountChange("")
                        focusManager.clearFocus()
                        keyboardController?.hide()
                    }
                },
                modifier =
                    Modifier
                        .width(dimensions.gameHintSendButtonWidth)
                        .height(dimensions.gameHintInputHeight),
                style =
                    AppButtonStyle(
                        fontSize = dimensions.bodyFontSize,
                        lineHeight = dimensions.buttonLineHeight,
                    ),
            )
        }
    } else {
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            Text(
                text = "Hint: $currentHint",
                fontSize = dimensions.bodyFontSize,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

@Composable
@Suppress("ktlint:standard:function-naming")
fun TeamRoleBox(
    title: String,
    gradient: Brush,
    isCurrentUser: Boolean,
    modifier: Modifier = Modifier,
) {
    val dimensions = LocalResponsiveDimensions.current

    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .background(gradient, RoundedCornerShape(8.dp))
                .padding(dimensions.smallSpacing),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = title,
            color = AppWhite,
            fontWeight = FontWeight.Bold,
            fontSize = dimensions.smallFontSize,
        )

        if (isCurrentUser) {
            Spacer(modifier = Modifier.height(dimensions.smallSpacing))
            Text(
                text = "You",
                color = AppWhite,
                fontSize = dimensions.smallFontSize,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

@Suppress("ktlint:standard:function-naming")
@Composable
fun CodenamesCard(
    card: GameCard,
    isSpymaster: Boolean,
    isSelected: Boolean = false,
    onClick: () -> Unit,
) {
    val dimensions = LocalResponsiveDimensions.current
    val cardShape = RoundedCornerShape(12.dp)

    val backgroundColor =
        when {
            card.revealed && card.type == CardType.NEUTRAL -> AppLightGray
            card.revealed -> getColor(card.type)
            isSpymaster -> getColor(card.type)
            else -> AppSurface
        }

    val contentColor =
        if (backgroundColor == AppSurface) {
            AppInk
        } else {
            AppWhite
        }

    AppButton(
        text = card.word,
        onClick = onClick,
        modifier =
            Modifier
                .aspectRatio(2f)
                .then(
                    if (isSelected) {
                        Modifier.border(3.dp, AppGreen, cardShape)
                    } else {
                        Modifier
                    },
                ).then(
                    if (card.revealed) {
                        Modifier.alpha(0.75f)
                    } else {
                        Modifier
                    },
                ),
        style =
            AppButtonStyle(
                containerColor = backgroundColor,
                contentColor = contentColor,
                fontSize = dimensions.cardFontSize,
                shape = cardShape,
            ),
    )
}

fun getColor(type: CardType): Color =
    when (type) {
        CardType.BLUE -> AppBlue
        CardType.RED -> AppRed
        CardType.NEUTRAL -> AppSurface
        CardType.ASSASSIN -> AppBlack
    }

fun getTeamBackgroundColor(currentTurn: PlayerRoles): Color =
    when (currentTurn) {
        PlayerRoles.BLUE_OPERATIVE, PlayerRoles.BLUE_SPYMASTER -> AppBlueTeamBackground
        PlayerRoles.RED_OPERATIVE, PlayerRoles.RED_SPYMASTER -> AppRedTeamBackground
        PlayerRoles.NONE -> AppBackground
    }
