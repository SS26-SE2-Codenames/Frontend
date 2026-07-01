package com.codenames.frontend.ui.composables

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.codenames.frontend.data.model.GameCard
import com.codenames.frontend.data.model.enums.CardType
import com.codenames.frontend.ui.buttons.AppButton
import com.codenames.frontend.ui.buttons.AppButtonStyle
import com.codenames.frontend.ui.screens.getColor
import com.codenames.frontend.ui.theme.AppGreen
import com.codenames.frontend.ui.theme.AppInk
import com.codenames.frontend.ui.theme.AppLightGray
import com.codenames.frontend.ui.theme.AppSurface
import com.codenames.frontend.ui.theme.AppWhite
import com.codenames.frontend.ui.theme.LocalResponsiveDimensions

const val BOARD_COLUMNS = 5
const val CARD_ASPECT_RATIO = 2.35f
const val LINEBREAK_TRESHOLD = 8

@Suppress("ktlint:standard:function-naming")
@Composable
fun GameBoardGrid(
    cards: List<GameCard>,
    scale: Float,
    offset: Offset,
    isSpymaster: Boolean,
    selectedCardPositions: Set<Int>,
    onCardClick: (Int) -> Unit,
    modifier: Modifier,
) {
    Box(
        modifier = modifier,
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(unbounded = true)
                    .graphicsLayer(
                        scaleX = scale,
                        scaleY = scale,
                        translationX = offset.x,
                        translationY = offset.y,
                        transformOrigin = TransformOrigin(0.5f, 0f),
                    ),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            cards
                .chunked(BOARD_COLUMNS)
                .forEachIndexed { rowIndex, row ->

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        row.forEachIndexed { columnIndex, card ->

                            val index =
                                (rowIndex * BOARD_COLUMNS) + columnIndex

                            Box(
                                modifier = Modifier.weight(1f),
                            ) {
                                CodenamesCard(
                                    card = card,
                                    isSpymaster = isSpymaster,
                                    isSelected = index in selectedCardPositions,
                                    onClick = {
                                        if (!isSpymaster && !card.revealed) {
                                            onCardClick(index)
                                        }
                                    },
                                )
                            }
                        }
                    }
                }
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
                .aspectRatio(CARD_ASPECT_RATIO)
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
                fontSize = if (card.word.length < LINEBREAK_TRESHOLD) dimensions.cardFontSize else dimensions.smallCardFontSize,
                shape = cardShape,
            ),
    )
}
