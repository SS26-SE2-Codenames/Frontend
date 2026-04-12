package com.codenames.codenames_frontend.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
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
    val isSpymaster =
        userRole == PlayerRole.BLUE_SPYMASTER || userRole == PlayerRole.RED_SPYMASTER
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    val blueLeft = cards.count { it.type == CardType.BLUE && !it.revealed }
    val redLeft = cards.count { it.type == CardType.RED && !it.revealed }

    Column(
        modifier = modifier
            .fillMaxHeight()
            .padding(16.dp)
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "BLUE: $blueLeft",
                color = Color(0xFF1565C0),
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Role: ${userRole.name}",
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "RED: $redLeft",
                color = Color(0xFFCF5530),
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(5),
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            itemsIndexed(cards) { index, card ->
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
            Text(
                text = "Hint: $currentHint",
                fontSize = 20.sp,
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