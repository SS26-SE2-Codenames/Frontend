package com.codenames.frontend.ui.buttons

import android.widget.Button
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex

private val cornerButtonColor = Color(0xFF383330)

@Suppress("ktlint:standard:function-naming")
@Composable
fun BoxScope.SettingsCornerButton(onClick: () -> Unit) {
    Box(
        modifier =
            Modifier
                .align(Alignment.TopEnd)
                .padding(top = 8.dp, end = 8.dp)
                .width(56.dp)
                .height(56.dp)
                .zIndex(1f),
    ) {
        androidx.compose.material3.IconButton(
            onClick = onClick,
            modifier = Modifier.fillMaxSize(),
            colors = androidx.compose.material3.IconButtonDefaults.iconButtonColors(
                containerColor = Color.Transparent,
                contentColor = Color.White,
            ),
        ) {
            Icon(
                imageVector = androidx.compose.material.icons.Icons.Default.Settings,
                contentDescription = "Settings",
                tint = Color.White,
            )
        }
    }
}

@Suppress("ktlint:standard:function-naming")
@Composable
fun BoxScope.ReturnCornerButton(onClick: () -> Unit) {
    Box(
        modifier =
            Modifier
                .align(Alignment.TopEnd)
                .padding(top = 16.dp, end = 16.dp)
                .width(140.dp)
                .height(56.dp)
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

private fun cornerButtonStyle(): AppButtonStyle =
    AppButtonStyle(
        containerColor = cornerButtonColor,
        contentColor = Color.White,
        fontSize = 16.sp,
        lineHeight = 18.sp,
    )
