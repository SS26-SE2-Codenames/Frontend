package com.codenames.frontend.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Immutable
data class ResponsiveDimensions(
    val isCompactHeight: Boolean,
    val isNarrowWidth: Boolean,
    val screenPadding: Dp,
    val sectionSpacing: Dp,
    val itemSpacing: Dp,
    val smallSpacing: Dp,
    val cornerButtonSize: Dp,
    val returnButtonWidth: Dp,
    val primaryButtonWidth: Dp,
    val primaryButtonHeight: Dp,
    val secondaryButtonHeight: Dp,
    val lobbyRoleCardWidth: Dp,
    val lobbyRoleCardHeight: Dp,
    val lobbyCenterColumnWidthFraction: Float,
    val gameTopPadding: Dp,
    val gameSidebarWidth: Dp,
    val gameChatWidth: Dp,
    val gameChatHeightFraction: Float,
    val gameStatusBarHeight: Dp,
    val gameHintCountWidth: Dp,
    val gameHintInputHeight: Dp,
    val gameHintInputWeight: Float,
    val gameHintSendButtonWidth: Dp,
    val buttonFontSize: TextUnit,
    val buttonLineHeight: TextUnit,
    val titleFontSize: TextUnit,
    val bodyFontSize: TextUnit,
    val smallFontSize: TextUnit,
    val gameBoardTopSpacing: Dp,
)

val LocalResponsiveDimensions =
    staticCompositionLocalOf {
        responsiveDimensionsFor(
            maxWidth = 900.dp,
            maxHeight = 420.dp,
        )
    }

fun responsiveDimensionsFor(
    maxWidth: Dp,
    maxHeight: Dp,
): ResponsiveDimensions {
    val isCompactHeight = maxHeight < 420.dp
    val isNarrowWidth = maxWidth < 760.dp

    return ResponsiveDimensions(
        isCompactHeight = isCompactHeight,
        isNarrowWidth = isNarrowWidth,
        screenPadding = compactDp(isCompactHeight, compact = 10.dp, regular = 16.dp),
        sectionSpacing = compactDp(isCompactHeight, compact = 10.dp, regular = 16.dp),
        itemSpacing = compactDp(isCompactHeight, compact = 6.dp, regular = 10.dp),
        smallSpacing = 6.dp,
        cornerButtonSize = compactDp(isCompactHeight, compact = 44.dp, regular = 52.dp),
        returnButtonWidth = narrowDp(isNarrowWidth, narrow = 112.dp, regular = 132.dp),
        primaryButtonWidth = (maxWidth * 0.22f).coerceIn(150.dp, 220.dp),
        primaryButtonHeight = (maxHeight * 0.16f).coerceIn(56.dp, 82.dp),
        secondaryButtonHeight = (maxHeight * 0.12f).coerceIn(44.dp, 64.dp),
        lobbyRoleCardWidth = (maxWidth * 0.20f).coerceIn(150.dp, 200.dp),
        lobbyRoleCardHeight = (maxHeight * 0.30f).coerceIn(105.dp, 145.dp),
        lobbyCenterColumnWidthFraction = narrowFloat(isNarrowWidth, narrow = 0.62f, regular = 0.52f),
        gameTopPadding = compactDp(isCompactHeight, compact = 8.dp, regular = 12.dp),
        gameSidebarWidth = (maxWidth * 0.10f).coerceIn(72.dp, 96.dp),
        gameChatWidth = (maxWidth * 0.42f).coerceIn(300.dp, 420.dp),
        gameChatHeightFraction = compactFloat(isCompactHeight, compact = 0.86f, regular = 0.90f),
        gameStatusBarHeight = compactDp(isCompactHeight, compact = 24.dp, regular = 32.dp),
        gameHintCountWidth = compactDp(isCompactHeight, compact = 96.dp, regular = 112.dp),
        gameHintInputHeight = compactDp(isCompactHeight, compact = 72.dp, regular = 80.dp),
        gameHintInputWeight = narrowFloat(isNarrowWidth, narrow = 0.50f, regular = 0.62f),
        gameHintSendButtonWidth = compactDp(isCompactHeight, compact = 96.dp, regular = 112.dp),
        buttonFontSize = compactSp(isCompactHeight, compact = 18, regular = 22),
        buttonLineHeight = compactSp(isCompactHeight, compact = 20, regular = 26),
        titleFontSize = compactSp(isCompactHeight, compact = 28, regular = 36),
        bodyFontSize = compactSp(isCompactHeight, compact = 16, regular = 20),
        smallFontSize = compactSp(isCompactHeight, compact = 10, regular = 12),
        gameBoardTopSpacing = compactDp(isCompactHeight, compact = 2.dp, regular = 4.dp),
    )
}

private fun compactDp(
    isCompactHeight: Boolean,
    compact: Dp,
    regular: Dp,
): Dp = if (isCompactHeight) compact else regular

private fun narrowDp(
    isNarrowWidth: Boolean,
    narrow: Dp,
    regular: Dp,
): Dp = if (isNarrowWidth) narrow else regular

private fun compactFloat(
    isCompactHeight: Boolean,
    compact: Float,
    regular: Float,
): Float = if (isCompactHeight) compact else regular

private fun narrowFloat(
    isNarrowWidth: Boolean,
    narrow: Float,
    regular: Float,
): Float = if (isNarrowWidth) narrow else regular

private fun compactSp(
    isCompactHeight: Boolean,
    compact: Int,
    regular: Int,
): TextUnit = if (isCompactHeight) compact.sp else regular.sp
