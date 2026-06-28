package com.codenames.frontend.ui.buttons

import androidx.compose.foundation.background
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.codenames.frontend.ui.theme.greenGradient

@Composable
fun AppSendButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    style: AppButtonStyle = AppButtonStyle()
) {
    val resolvedContainerColor =
        when {
            style.containerColor != Color.Unspecified -> style.containerColor
            else -> MaterialTheme.colorScheme.primary
        }

    val resolvedContentColor =
        when {
            style.contentColor != Color.Unspecified -> style.contentColor
            style.type == AppButtonType.SECONDARY -> MaterialTheme.colorScheme.primary
            else -> MaterialTheme.colorScheme.onPrimary
        }

    IconButton(
        onClick = onClick,
        enabled = style.enabled,
        modifier = modifier.background(style.backgroundBrush ?: greenGradient, shape = style.shape),
        colors = IconButtonDefaults.iconButtonColors(
            containerColor =  Color.Transparent,
            contentColor = resolvedContentColor
        )
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.Send,
            contentDescription = "Send",
        )
    }
}