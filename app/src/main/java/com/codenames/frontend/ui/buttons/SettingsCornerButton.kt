package com.codenames.frontend.ui.buttons

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.codenames.frontend.ui.theme.AppInk
import com.codenames.frontend.ui.theme.AppWhite
import com.codenames.frontend.ui.theme.LocalResponsiveDimensions

@Suppress("ktlint:standard:function-naming")
@Composable
fun BoxScope.SettingsCornerButton(onClick: () -> Unit) {
    val dimensions = LocalResponsiveDimensions.current

    Box(
        modifier =
            Modifier
                .align(Alignment.TopEnd)
                .padding(top = dimensions.smallSpacing, end = dimensions.smallSpacing)
                .width(dimensions.cornerButtonSize)
                .height(dimensions.cornerButtonSize)
                .zIndex(1f),
    ) {
        androidx.compose.material3.IconButton(
            onClick = onClick,
            modifier = Modifier.fillMaxSize(),
            colors =
                androidx.compose.material3.IconButtonDefaults.iconButtonColors(
                    containerColor = Color.Transparent,
                    contentColor = AppWhite,
                ),
        ) {
            Icon(
                imageVector = androidx.compose.material.icons.Icons.Default.Settings,
                contentDescription = "Settings",
                tint = AppWhite,
            )
        }
    }
}

@Suppress("ktlint:standard:function-naming")
@Composable
fun BoxScope.ReturnCornerButton(onClick: () -> Unit) {
    val dimensions = LocalResponsiveDimensions.current

    Box(
        modifier =
            Modifier
                .align(Alignment.TopEnd)
                .padding(top = dimensions.screenPadding, end = dimensions.screenPadding)
                .width(dimensions.returnButtonWidth)
                .height(dimensions.secondaryButtonHeight)
                .zIndex(1f),
    ) {
        AppButton(
            text = "Return",
            onClick = onClick,
            modifier = Modifier.fillMaxSize(),
            style = cornerButtonStyle(),
        )
    }
}

@Composable
private fun cornerButtonStyle(): AppButtonStyle {
    val dimensions = LocalResponsiveDimensions.current

    return AppButtonStyle(
        containerColor = AppInk,
        contentColor = AppWhite,
        fontSize = 16.sp,
        lineHeight = 18.sp,
        contentPadding =
            androidx.compose.foundation.layout.PaddingValues(
                horizontal = dimensions.itemSpacing,
                vertical = dimensions.smallSpacing,
            ),
    )
}
