package com.codenames.frontend.ui.buttons

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import com.codenames.frontend.ui.theme.CodenamesTheme

enum class AppButtonType {
    PRIMARY,
    SECONDARY,
}

data class AppButtonStyle(
    val enabled: Boolean = true,
    val type: AppButtonType = AppButtonType.PRIMARY,
    val containerColor: Color = Color.Unspecified,
    val contentColor: Color = Color.Unspecified,
    val backgroundBrush: Brush? = null,
    val fontSize: TextUnit = TextUnit.Unspecified,
    val lineHeight: TextUnit = TextUnit.Unspecified,
    val shape: Shape = RoundedCornerShape(12.dp),
    val contentPadding: PaddingValues = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
)

@Composable
fun AppButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    style: AppButtonStyle = AppButtonStyle(),
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

    when (style.type) {
        AppButtonType.PRIMARY -> {
            Button(
                onClick = onClick,
                modifier =
                    if (style.backgroundBrush != null) {
                        modifier.background(style.backgroundBrush, shape = style.shape)
                    } else {
                        modifier
                    },
                enabled = style.enabled,
                shape = style.shape,
                contentPadding = style.contentPadding,
                colors =
                    ButtonDefaults.buttonColors(
                        containerColor = if (style.backgroundBrush != null) Color.Transparent else resolvedContainerColor,
                        contentColor = resolvedContentColor,
                    ),
            ) {
                AppButtonText(text = text, style = style)
            }
        }

        AppButtonType.SECONDARY -> {
            OutlinedButton(
                onClick = onClick,
                modifier = modifier,
                enabled = style.enabled,
                shape = style.shape,
                contentPadding = style.contentPadding,
                colors =
                    ButtonDefaults.outlinedButtonColors(
                        contentColor = resolvedContentColor,
                    ),
            ) {
                AppButtonText(text = text, style = style)
            }
        }
    }
}

@Composable
private fun AppButtonText(
    text: String,
    style: AppButtonStyle,
) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelLarge,
        fontSize = style.fontSize,
        lineHeight = style.lineHeight,
        textAlign = TextAlign.Center,
    )
}

@Preview(showBackground = true)
@Composable
private fun AppButtonPreview() {
    CodenamesTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            AppButton(
                text = "Spiel erstellen",
                onClick = {},
            )

            AppButton(
                text = "Beitreten",
                onClick = {},
                style = AppButtonStyle(type = AppButtonType.SECONDARY),
            )
        }
    }
}
