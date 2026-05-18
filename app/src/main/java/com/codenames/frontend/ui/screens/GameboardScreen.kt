package com.codenames.frontend.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.codenames.frontend.data.model.ChatDomainModel
import com.codenames.frontend.data.model.ChatMessage
import com.codenames.frontend.data.model.GameCard
import com.codenames.frontend.data.model.GameState
import com.codenames.frontend.data.model.enums.CardType
import com.codenames.frontend.data.model.enums.ChatTab
import com.codenames.frontend.data.model.enums.Role
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
import com.codenames.frontend.ui.theme.blueGradient
import com.codenames.frontend.ui.theme.greenGradient
import com.codenames.frontend.ui.theme.redGradient
import com.codenames.frontend.viewmodel.ChatViewModel
import com.codenames.frontend.viewmodel.GameViewModel

@Suppress("ktlint:standard:function-naming")
@Composable
fun GameboardScreen(
    userRole: PlayerRoles,
    gameState: GameState,
    onHintChange: (String, Int) -> Unit,
    onReveal: (Int) -> Unit,
    modifier: Modifier = Modifier,
    onSendChatMessage: (String) -> Unit = {},
    onSettingsClick: (() -> Unit)? = null,
    chatViewModel: ChatViewModel = viewModel(),
    gameViewModel: GameViewModel,
) {
    val currentHint = gameState.currentHint
    val cards = gameState.cards
    val currentTurn = gameState.currentTurn
    val winner = gameState.winner
    val remainingGuesses = gameState.remainingGuesses
    val currentRedFound = gameViewModel.getCurrentFound(CardType.RED)
    val currentBlueFound = gameViewModel.getCurrentFound(CardType.BLUE)
    val chatUiState by chatViewModel.uiState.collectAsState()

    var hintInput by rememberSaveable { mutableStateOf("") }
    var countInput by rememberSaveable { mutableStateOf("") }
    var isChatOpen by rememberSaveable { mutableStateOf(false) }
    var selectedChatTab by rememberSaveable { mutableStateOf(ChatTab.GLOBAL) }

    val isSpymaster =
        userRole == PlayerRoles.BLUE_SPYMASTER || userRole == PlayerRoles.RED_SPYMASTER
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    var scale by remember { mutableFloatStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }

    val onInputChange: (String) -> Unit = { hintInput = it }

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(top = 72.dp, start = 16.dp, end = 16.dp, bottom = 16.dp),
        ) {
            GameStatusBar(
                currentTurn = currentTurn,
                winner = winner,
                remainingGuesses = remainingGuesses,
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier =
                    Modifier
                        .weight(1f)
                        .fillMaxWidth(),
            ) {
                TeamSidebar(
                    userRole,
                    color = Team.BLUE,
                    teamFound = currentBlueFound,
                    textColor = Color(0xFF1565C0),
                    gradient = blueGradient,
                )

                if (cards.isEmpty()) {
                    Box(
                        modifier =
                            Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .padding(horizontal = 8.dp)
                                .background(Color(0xFFE0D8C8), RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = "Waiting for game state...",
                            color = Color(0xFF383330),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                } else {
                    GameBoardGrid(
                        cards,
                        scale,
                        offset,
                        isSpymaster,
                        onReveal,
                        modifier =
                            Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .padding(horizontal = 8.dp)
                                .clipToBounds()
                                .pointerInput(Unit) {
                                    detectTransformGestures { _, pan, zoom, _ ->
                                        scale = (scale * zoom).coerceIn(0.5f, 3f)
                                        offset += pan
                                    }
                                },
                    )
                }

                TeamSidebar(
                    userRole,
                    color = Team.RED,
                    teamFound = currentRedFound,
                    textColor = Color(0xFFCF5530),
                    gradient = redGradient,
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            HintSection(
                isSpymaster,
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

        if (!isSpymaster && isChatOpen) {
            ChatWindow(
                chatInput = chatUiState.currentInput,
                messages = chatUiState.messages,
                selectedTab = selectedChatTab,
                onTabSelected = { selectedChatTab = it },
                onChatInputChange = { chatViewModel.updateInput(it) },
                onSendClick = { tab ->
                    chatViewModel.sendMessage(
                        username = "Player1",
                        tab = tab,
                    )
                },
                modifier =
                    Modifier
                        .align(Alignment.Center)
                        .padding(end = 24.dp, bottom = 96.dp)
                        .width(420.dp)
                        .fillMaxHeight(0.78f),
            )
        }

        if (!isSpymaster) {
            ChatToggleButton(
                isChatOpen = isChatOpen,
                onClick = { isChatOpen = !isChatOpen },
                modifier =
                    Modifier
                        .align(Alignment.BottomEnd)
                        .padding(end = 24.dp, bottom = 24.dp),
            )
        }

        onSettingsClick?.let { openSettings ->
            SettingsCornerButton(
                onClick = openSettings,
            )
        }
    }
}

@Suppress("ktlint:standard:function-naming")
@Composable
fun GameStatusBar(
    currentTurn: PlayerRoles?,
    winner: Team?,
    remainingGuesses: Int,
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .height(40.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        val statusText =
            when {
                winner != null -> "Winner: $winner"
                currentTurn != null -> "Turn: ${currentTurn.name} | Guesses: $remainingGuesses"
                else -> "Waiting for turn..."
            }

        Text(
            text = statusText,
            color = Color(0xFF383330),
            fontSize = 18.sp,
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
    AppButton(
        text = "Chat",
        onClick = onClick,
        modifier =
            modifier
                .width(140.dp)
                .height(56.dp),
        style =
            AppButtonStyle(
                containerColor = if (isChatOpen) Color(0xFF555555) else Color(0xFF383330),
                contentColor = Color.White,
                fontSize = 18.sp,
                lineHeight = 20.sp,
            ),
    )
}

@Suppress("ktlint:standard:function-naming")
@Composable
fun ChatWindow(
    chatInput: String,
    messages: List<ChatMessage>,
    selectedTab: ChatTab,
    onTabSelected: (ChatTab) -> Unit,
    onChatInputChange: (String) -> Unit,
    onSendClick: (ChatTab) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .background(
                    color = Color(0xE6383330),
                    shape = RoundedCornerShape(12.dp),
                ).padding(12.dp),
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            ChatTab.entries.forEach { tab ->
                AppButton(
                    text = tab.title,
                    onClick = { onTabSelected(tab) },
                    modifier =
                        Modifier
                            .weight(1f)
                            .height(36.dp),
                    style =
                        AppButtonStyle(
                            type = AppButtonType.PRIMARY,
                            containerColor = if (selectedTab == tab) Color.Unspecified else Color.Transparent,
                            contentColor = if (selectedTab == tab) Color.Unspecified else Color.LightGray,
                            fontSize = 11.sp,
                            lineHeight = 12.sp,
                            contentPadding = PaddingValues(horizontal = 4.dp, vertical = 4.dp),
                        ),
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        ChatMessagesArea(
            messages = messages,
            selectedTab = selectedTab,
            modifier = Modifier.weight(1f).fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(64.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
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
                        containerColor = Color(0xFFE0D8C8),
                        contentColor = Color(0xFF383330),
                        fontSize = 14.sp,
                        lineHeight = 16.sp,
                    ),
            )

            AppButton(
                text = "Send",
                onClick = { onSendClick(selectedTab) },
                modifier =
                    Modifier
                        .width(92.dp)
                        .fillMaxHeight(),
                style =
                    AppButtonStyle(
                        backgroundBrush = greenGradient,
                        fontSize = 16.sp,
                        lineHeight = 18.sp,
                    ),
            )
        }
    }
}

@Suppress("ktlint:standard:function-naming")
@Composable
fun ChatMessagesArea(
    messages: List<ChatMessage>,
    modifier: Modifier = Modifier,
    selectedTab: ChatTab,
) {
    Column(
        modifier =
            modifier
                .background(
                    color = Color(0xB3E0D8C8),
                    shape = RoundedCornerShape(8.dp),
                ).padding(12.dp),
        verticalArrangement = Arrangement.Top,
    ) {
        Text(
            text = "${selectedTab.title} Chat",
            color = Color(0xFF383330),
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (messages.isEmpty()) {
            Text(
                text = "No messages yet.",
                color = Color(0xFF383330),
                fontSize = 14.sp,
            )
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                items(
                    messages.filter {
                        it.chatTab == selectedTab
                    },
                ) { message ->
                    ChatMessageBubble(message = message)
                }
            }
        }
    }
}

@Suppress("ktlint:standard:function-naming")
@Composable
fun ChatMessageBubble(message: ChatMessage) {
    val alignment = if (message.isFromMe) Alignment.End else Alignment.Start
    val bubbleColor = if (message.isFromMe) Color(0xFF4CAF50) else Color(0xFFE0D8C8)
    val textColor = if (message.isFromMe) Color.White else Color(0xFF383330)

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = alignment,
    ) {
        Text(
            text = message.sender,
            color = Color(0xFF383330),
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
        )

        Box(
            modifier =
                Modifier
                    .fillMaxWidth(0.78f)
                    .background(bubbleColor, RoundedCornerShape(8.dp))
                    .padding(8.dp),
        ) {
            Text(
                text = message.message,
                color = textColor,
                fontSize = 13.sp,
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
    val isRed = color == Team.RED
    val operative = if (isRed) PlayerRoles.RED_OPERATIVE else PlayerRoles.BLUE_OPERATIVE
    val spymaster = if (isRed) PlayerRoles.RED_SPYMASTER else PlayerRoles.BLUE_SPYMASTER
    val title = if (isRed) "RED TEAM" else "BLUE TEAM"

    Column(
        modifier =
            Modifier
                .width(90.dp)
                .fillMaxHeight(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            title,
            fontWeight = FontWeight.Bold,
            color = textColor,
            fontSize = 12.sp,
        )

        Spacer(modifier = Modifier.height(8.dp))

        TeamRoleBox(
            title = "OPERATIVES",
            gradient = gradient,
            isCurrentUser = userRole == operative,
        )

        Spacer(modifier = Modifier.height(8.dp))

        TeamRoleBox(
            title = "SPYMASTERS",
            gradient = gradient,
            isCurrentUser = userRole == spymaster,
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "$teamFound FOUND",
            color = textColor,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 16.sp,
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
    if (isSpymaster) {
        Row {
            AppTextField(
                value = hintInput,
                onValueChange = onInputChange,
                modifier = Modifier.weight(1f),
                state =
                    AppTextFieldState(
                        label = "HINT",
                        placeholder = "Enter word...",
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
                modifier = Modifier.width(80.dp),
                state = AppTextFieldState(label = "COUNT", placeholder = "0"),
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
            )
        }
    } else {
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            Text(
                text = "Hint: $currentHint",
                fontSize = 20.sp,
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
    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .background(gradient, RoundedCornerShape(8.dp))
                .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = title,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 10.sp,
        )

        if (isCurrentUser) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "You",
                color = Color.White,
                fontSize = 12.sp,
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
    onClick: () -> Unit,
) {
    val backgroundColor =
        when {
            card.revealed -> getColor(card.type)
            isSpymaster -> getColor(card.type)
            else -> Color(0xFFE0D8C8)
        }

    val contentColor =
        if (!card.revealed && !isSpymaster) {
            Color(0xFF383330)
        } else {
            Color.White
        }

    AppButton(
        text = card.word,
        onClick = onClick,
        modifier = Modifier.aspectRatio(2f),
        style =
            AppButtonStyle(
                containerColor = backgroundColor,
                contentColor = contentColor,
                fontSize = 10.sp,
            ),
    )
}

fun getColor(type: CardType): Color =
    when (type) {
        CardType.BLUE -> Color(0xFF1565C0)
        CardType.RED -> Color(0xFFCF5530)
        CardType.NEUTRAL -> Color(0xFF383330)
        CardType.ASSASSIN -> Color.Black
    }

@Suppress("ktlint:standard:function-naming")
@Composable
fun OfflineGameStateTestScreen(gameViewModel: GameViewModel) {
    var currentHint by rememberSaveable { mutableStateOf("EAGLE") }
    var currentTurn by rememberSaveable { mutableStateOf(PlayerRoles.RED_OPERATIVE) }
    var remainingGuesses by rememberSaveable { mutableIntStateOf(3) }
    var currentBlueFound by rememberSaveable { mutableIntStateOf(0) }
    var currentRedFound by rememberSaveable { mutableIntStateOf(0) }

    val cards =
        remember {
            mutableStateListOf(
                GameCard("BERLIN", CardType.BLUE),
                GameCard("RIVER", CardType.BLUE),
                GameCard("MOON", CardType.BLUE),
                GameCard("PIANO", CardType.BLUE),
                GameCard("FOREST", CardType.BLUE),
                GameCard("ROME", CardType.RED),
                GameCard("APPLE", CardType.RED),
                GameCard("TRAIN", CardType.RED),
                GameCard("KING", CardType.RED),
                GameCard("GLASS", CardType.RED),
                GameCard("CHAIR", CardType.NEUTRAL),
                GameCard("STONE", CardType.NEUTRAL),
                GameCard("CLOUD", CardType.NEUTRAL),
                GameCard("FIELD", CardType.NEUTRAL),
                GameCard("WATCH", CardType.NEUTRAL),
                GameCard("VIPER", CardType.ASSASSIN),
                GameCard("BREAD", CardType.NEUTRAL),
                GameCard("LASER", CardType.RED),
                GameCard("BRIDGE", CardType.BLUE),
                GameCard("QUEEN", CardType.RED),
                GameCard("OCEAN", CardType.BLUE),
                GameCard("MOUSE", CardType.NEUTRAL),
                GameCard("PLANE", CardType.RED),
                GameCard("SUN", CardType.BLUE),
                GameCard("KEY", CardType.NEUTRAL),
            )
        }

    val chatMessages =
        listOf(
            ChatDomainModel(
                sender = "Max",
                text = "I think BERLIN fits the hint.",
                isFromMe = false,
            ),
            ChatDomainModel(
                sender = "You",
                text = "Maybe RIVER too.",
                isFromMe = true,
            ),
        )

    fun revealCard(index: Int) {
        val card = cards[index]

        if (card.revealed) return

        cards[index] = card.copy(revealed = true)

        when (card.type) {
            CardType.BLUE -> currentBlueFound++
            CardType.RED -> currentRedFound++
            CardType.NEUTRAL ->
                currentTurn =
                    if (currentTurn == PlayerRoles.BLUE_SPYMASTER) PlayerRoles.RED_OPERATIVE else PlayerRoles.BLUE_SPYMASTER
            CardType.ASSASSIN -> currentTurn = PlayerRoles.NONE
        }

        if (remainingGuesses > 0) {
            remainingGuesses--
        }
    }

    GameboardScreen(
        userRole = PlayerRoles.BLUE_OPERATIVE,
        gameState =
            GameState(
                currentHint = currentHint,
                currentTurn = currentTurn,
                remainingGuesses = remainingGuesses,
                cards = cards,
            ),
        onHintChange = {word, count -> currentHint = word
            remainingGuesses = count},
        onReveal = { index -> revealCard(index) },
        onSendChatMessage = {},
        gameViewModel = gameViewModel,
    )
}
