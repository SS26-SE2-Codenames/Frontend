package com.codenames.codenames_frontend.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Brush
import com.codenames.codenames_frontend.ui.buttons.AppButton
import com.codenames.codenames_frontend.ui.buttons.AppButtonStyle
import com.codenames.codenames_frontend.ui.inputs.*
import com.codenames.codenames_frontend.ui.roles.PlayerRole

enum class CardType {
    BLUE, RED, NEUTRAL, ASSASSIN
}

data class GameCard(
    val word: String,
    val type: CardType,
    val revealed: Boolean = false
)

@Composable
fun GameTestScreen() {

    var currentHint by remember { mutableStateOf("Waiting for hint...") }

    val cards = remember {
        mutableStateListOf(
            *List(25) {
                GameCard(
                    word = "Word ${it + 1}",
                    type = when (it) {
                        0 -> CardType.ASSASSIN
                        in 1..8 -> CardType.BLUE
                        in 9..15 -> CardType.RED
                        else -> CardType.NEUTRAL
                    }
                )
            }.toTypedArray()
        )
    }

    fun revealCard(index: Int) {
        val card = cards[index]
        cards[index] = card.copy(revealed = true)
    }

    Row(modifier = Modifier.fillMaxSize()) {

        GameboardScreen(
            userRole = PlayerRole.BLUE_SPYMASTER,
            currentHint = currentHint,
            onHintChange = { currentHint = it },
            cards = cards,
            onReveal = {},
            modifier = Modifier.weight(1f)
        )

        GameboardScreen(
            userRole = PlayerRole.BLUE_OPERATIVE,
            currentHint = currentHint,
            onHintChange = {},
            cards = cards,
            onReveal = { index -> revealCard(index) },
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun GameboardScreen(
    userRole: PlayerRole,
    currentHint: String,
    onHintChange: (String) -> Unit,
    cards: List<GameCard>,
    onReveal: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var hintInput by rememberSaveable { mutableStateOf("") }
    val isSpymaster = userRole == PlayerRole.BLUE_SPYMASTER || userRole == PlayerRole.RED_SPYMASTER
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    val blueLeft = cards.count { it.type == CardType.BLUE && !it.revealed }
    val redLeft = cards.count { it.type == CardType.RED && !it.revealed }

    var scale by remember { mutableFloatStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }

    val blueGradient = Brush.verticalGradient(
        colors = listOf(Color(0xFF42A5F5), Color(0xFF1565C0))
    )
    val redGradient = Brush.verticalGradient(
        colors = listOf(Color(0xFFCF5530), Color(0xFFDE8468))
    )

    Column(
        modifier = modifier
            .fillMaxHeight()
            .padding(16.dp)
    ) {

        Row(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {

            Column(
                modifier = Modifier
                    .width(90.dp)
                    .fillMaxHeight(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "BLUE TEAM",
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1565C0),
                    fontSize = 12.sp
                )
                Spacer(modifier = Modifier.height(8.dp))

                TeamRoleBox(
                    title = "OPERATIVES",
                    gradient = blueGradient,
                    isCurrentUser = userRole == PlayerRole.BLUE_OPERATIVE
                )
                Spacer(modifier = Modifier.height(8.dp))
                TeamRoleBox(
                    title = "SPYMASTERS",
                    gradient = blueGradient,
                    isCurrentUser = userRole == PlayerRole.BLUE_SPYMASTER
                )

                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "$blueLeft LEFT",
                    color = Color(0xFF1565C0),
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 18.sp
                )
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .padding(horizontal = 8.dp)
                    .clipToBounds()
                    .pointerInput(Unit) {
                        detectTransformGestures { _, pan, zoom, _ ->
                            scale = (scale * zoom).coerceIn(0.5f, 3f)
                            offset += pan
                        }
                    }
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight(unbounded = true)
                        .graphicsLayer(
                            scaleX = scale,
                            scaleY = scale,
                            translationX = offset.x,
                            translationY = offset.y
                        ),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val columns = 5
                    for (i in cards.indices step columns) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            for (j in 0 until columns) {
                                val index = i + j
                                if (index < cards.size) {
                                    val card = cards[index]
                                    Box(modifier = Modifier.weight(1f)) {
                                        CodenamesCard(
                                            card = card,
                                            isSpymaster = isSpymaster,
                                            onClick = {
                                                if (!isSpymaster && !card.revealed) {
                                                    onReveal(index)
                                                }
                                            }
                                        )
                                    }
                                } else {
                                    Spacer(modifier = Modifier.weight(1f))
                                }
                            }
                        }
                    }
                }
            }

            Column(
                modifier = Modifier
                    .width(90.dp)
                    .fillMaxHeight(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "RED TEAM",
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFCF5530),
                    fontSize = 12.sp
                )
                Spacer(modifier = Modifier.height(8.dp))

                TeamRoleBox(
                    title = "OPERATIVES",
                    gradient = redGradient,
                    isCurrentUser = userRole == PlayerRole.RED_OPERATIVE
                )
                Spacer(modifier = Modifier.height(8.dp))
                TeamRoleBox(
                    title = "SPYMASTERS",
                    gradient = redGradient,
                    isCurrentUser = userRole == PlayerRole.RED_SPYMASTER
                )

                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "$redLeft LEFT",
                    color = Color(0xFFCF5530),
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 18.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (isSpymaster) {
            Row {
                AppTextField(
                    value = hintInput,
                    onValueChange = { hintInput = it },
                    modifier = Modifier.weight(1f),
                    state = AppTextFieldState(
                        label = "HINT",
                        placeholder = "Enter word..."
                    ),
                    keyboard = AppTextFieldKeyboard(
                        actions = androidx.compose.foundation.text.KeyboardActions(
                            onSend = {
                                if (hintInput.isNotBlank()) {
                                    onHintChange(hintInput.uppercase())
                                    hintInput = ""
                                    focusManager.clearFocus()
                                    keyboardController?.hide()
                                }
                            }
                        )
                    )
                )

                AppButton(
                    text = "SEND",
                    onClick = {
                        if (hintInput.isNotBlank()) {
                            onHintChange(hintInput.uppercase())
                            hintInput = ""
                        }
                    }
                )
            }
        } else {
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                Text(
                    text = "Hint: $currentHint",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun TeamRoleBox(
    title: String,
    gradient: Brush,
    isCurrentUser: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(gradient, RoundedCornerShape(8.dp))
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = title,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 10.sp
        )

        if (isCurrentUser) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "👤 You",
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun CodenamesCard(
    card: GameCard,
    isSpymaster: Boolean,
    onClick: () -> Unit
) {

    val backgroundColor = when {
        card.revealed -> getColor(card.type)
        isSpymaster -> getColor(card.type)
        else -> Color(0xFFE0D8C8)
    }

    AppButton(
        text = card.word,
        onClick = onClick,
        modifier = Modifier.aspectRatio(2f),
        style = AppButtonStyle(
            containerColor = backgroundColor,
            contentColor = Color.White,
            fontSize = 10.sp
        )
    )
}

fun getColor(type: CardType): Color {
    return when (type) {
        CardType.BLUE -> Color(0xFF1565C0)
        CardType.RED -> Color(0xFFCF5530)
        CardType.NEUTRAL -> Color(0xFF383330)
        CardType.ASSASSIN -> Color.Black
    }
}