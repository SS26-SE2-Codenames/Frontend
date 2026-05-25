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
        screenPadding = if (isCompactHeight) 10.dp else 16.dp,
        sectionSpacing = if (isCompactHeight) 10.dp else 16.dp,
        itemSpacing = if (isCompactHeight) 6.dp else 10.dp,
        smallSpacing = 6.dp,
        cornerButtonSize = if (isCompactHeight) 44.dp else 52.dp,
        returnButtonWidth = if (isNarrowWidth) 112.dp else 132.dp,
        primaryButtonWidth = (maxWidth * 0.22f).coerceIn(150.dp, 220.dp),
        primaryButtonHeight = (maxHeight * 0.16f).coerceIn(56.dp, 82.dp),
        secondaryButtonHeight = (maxHeight * 0.12f).coerceIn(44.dp, 64.dp),
        lobbyRoleCardWidth = (maxWidth * 0.20f).coerceIn(150.dp, 200.dp),
        lobbyRoleCardHeight = (maxHeight * 0.30f).coerceIn(105.dp, 145.dp),
        lobbyCenterColumnWidthFraction = if (isNarrowWidth) 0.62f else 0.52f,
        gameTopPadding = if (isCompactHeight) 8.dp else 12.dp,
        gameSidebarWidth = (maxWidth * 0.10f).coerceIn(72.dp, 96.dp),
        gameChatWidth = (maxWidth * 0.42f).coerceIn(300.dp, 420.dp),
        gameChatHeightFraction = if (isCompactHeight) 0.86f else 0.90f,
        gameStatusBarHeight = if (isCompactHeight) 24.dp else 32.dp,
        gameHintCountWidth = if (isCompactHeight) 96.dp else 112.dp,
        gameHintInputHeight = if (isCompactHeight) 72.dp else 80.dp,
        gameHintInputWeight = if (isNarrowWidth) 0.50f else 0.62f,
        gameHintSendButtonWidth = if (isCompactHeight) 96.dp else 112.dp,
        buttonFontSize = if (isCompactHeight) 18.sp else 22.sp,
        buttonLineHeight = if (isCompactHeight) 20.sp else 26.sp,
        titleFontSize = if (isCompactHeight) 28.sp else 36.sp,
        bodyFontSize = if (isCompactHeight) 16.sp else 20.sp,
        smallFontSize = if (isCompactHeight) 10.sp else 12.sp,
        gameBoardTopSpacing = if (isCompactHeight) 2.dp else 4.dp,
    )
}
