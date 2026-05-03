package com.codenames.frontend.ui.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.codenames.frontend.ui.screens.CodenamesCard
import com.codenames.frontend.ui.screens.GameCard

@Suppress("ktlint:standard:function-naming")
@Composable
fun GameBoardGrid(
    cards: List<GameCard>,
    scale: Float,
    offset: Offset,
    isSpymaster: Boolean,
    onReveal: (Int) -> Unit,
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
                    ),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            val columns = 5
            for (i in cards.indices step columns) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
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
                                    },
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
}
