package com.codenames.frontend.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
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
import com.codenames.frontend.data.model.enums.Team
import com.codenames.frontend.ui.buttons.AppButton
import com.codenames.frontend.ui.buttons.AppButtonStyle
import com.codenames.frontend.ui.composables.GameBoardGrid
import com.codenames.frontend.ui.inputs.AppTextField
import com.codenames.frontend.ui.inputs.AppTextFieldKeyboard
import com.codenames.frontend.ui.inputs.AppTextFieldState
import com.codenames.frontend.ui.roles.PlayerRoles

enum class CardType {
    BLUE,
    RED,
    NEUTRAL,
    ASSASSIN,
}

data class GameCard(
    val word: String,
    val type: CardType,
    val revealed: Boolean = false,
)

@Suppress("ktlint:standard:function-naming")
@Composable
fun GameTestScreen() {
    var currentHint by remember { mutableStateOf("Waiting for hint...") }

    // will be replaced by backend call
    val cards =
        remember {
            mutableStateListOf(
                *List(25) {
                    GameCard(
                        word = "Word ${it + 1}",
                        type =
                            when (it) {
                                0 -> CardType.ASSASSIN
                                in 1..8 -> CardType.BLUE
                                in 9..15 -> CardType.RED
                                else -> CardType.NEUTRAL
                            },
                    )
                }.toTypedArray(),
            )
        }

    fun revealCard(index: Int) {
        val card = cards[index]
        cards[index] = card.copy(revealed = true)
    }

    Row(modifier = Modifier.fillMaxSize()) {
        GameboardScreen(
            userRole = PlayerRoles.BLUE_SPYMASTER,
            currentHint = currentHint,
            onHintChange = { currentHint = it },
            cards = cards,
            onReveal = {},
            modifier = Modifier.weight(1f),
        )

        GameboardScreen(
            userRole = PlayerRoles.BLUE_OPERATIVE,
            currentHint = currentHint,
            onHintChange = {},
            cards = cards,
            onReveal = { index -> revealCard(index) },
            modifier = Modifier.weight(1f),
        )
    }
}

@Suppress("ktlint:standard:function-naming")
@Composable
fun GameboardScreen(
    userRole: PlayerRoles,
    currentHint: String,
    onHintChange: (String) -> Unit,
    cards: List<GameCard>,
    onReveal: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    var hintInput by rememberSaveable { mutableStateOf("") }
    val isSpymaster = userRole == PlayerRoles.BLUE_SPYMASTER || userRole == PlayerRoles.RED_SPYMASTER
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    val blueLeft = cards.count { it.type == CardType.BLUE && !it.revealed }
    val redLeft = cards.count { it.type == CardType.RED && !it.revealed }

    var scale by remember { mutableFloatStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }

    val onInputChange: (String) -> Unit = { hintInput = it }

    Column(
        modifier =
            modifier
                .fillMaxHeight()
                .padding(16.dp),
    ) {
        Row(
            modifier =
                Modifier
                    .weight(1f)
                    .fillMaxWidth(),
        ) {
            TeamSidebar(
                userRole,
                color = Team.BLUE,
                teamLeft = blueLeft,
                textColor = Color(0xFF1565C0),
                gradient = blueGradient,
            )

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

            TeamSidebar(
                userRole,
                color = Team.RED,
                teamLeft = redLeft,
                textColor = Color(0xFFCF5530),
                gradient = redGradient,
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        HintSection(
            isSpymaster,
            currentHint,
            hintInput,
            onHintChange,
            onInputChange,
            keyboardController,
            focusManager,
        )
    }
}

@Suppress("ktlint:standard:function-naming")
@Composable
fun TeamSidebar(
    userRole: PlayerRoles,
    color: Team,
    teamLeft: Int,
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
            text = "$teamLeft LEFT",
            color = textColor,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 18.sp,
        )
    }
}

@Suppress("ktlint:standard:function-naming")
@Composable
fun HintSection(
    isSpymaster: Boolean,
    currentHint: String,
    hintInput: String,
    onHintChange: (String) -> Unit,
    onInputChange: (String) -> Unit,
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
                                    if (hintInput.isNotBlank()) {
                                        onHintChange(hintInput.uppercase())
                                        onInputChange("")
                                        focusManager.clearFocus()
                                        keyboardController?.hide()
                                    }
                                },
                            ),
                    ),
            )

            AppButton(
                text = "SEND",
                onClick = {
                    if (hintInput.isNotBlank()) {
                        onHintChange(hintInput.uppercase())
                        onInputChange("")
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
                text = "👤 You",
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

    AppButton(
        text = card.word,
        onClick = onClick,
        modifier = Modifier.aspectRatio(2f),
        style =
            AppButtonStyle(
                containerColor = backgroundColor,
                contentColor = Color.White,
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
