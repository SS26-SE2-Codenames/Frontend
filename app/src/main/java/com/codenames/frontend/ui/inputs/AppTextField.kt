package com.codenames.frontend.ui.inputs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import com.codenames.frontend.ui.theme.CodenamesTheme

enum class AppTextFieldType {
    PRIMARY,
    SECONDARY,
}

data class AppTextFieldState(
    val enabled: Boolean = true,
    val label: String? = null,
    val placeholder: String? = null,
    val singleLine: Boolean = true,
    val isError: Boolean = false,
    val supportingText: String? = null,
)

data class AppTextFieldStyle(
    val type: AppTextFieldType = AppTextFieldType.PRIMARY,
    val containerColor: Color = Color.Unspecified,
    val contentColor: Color = Color.Unspecified,
    val backgroundBrush: Brush? = null,
    val fontSize: TextUnit = TextUnit.Unspecified,
    val lineHeight: TextUnit = TextUnit.Unspecified,
    val shape: Shape = RoundedCornerShape(12.dp),
)

data class AppTextFieldKeyboard(
    val options: KeyboardOptions = KeyboardOptions.Default,
    val actions: KeyboardActions = KeyboardActions.Default,
)

@Suppress("ktlint:standard:function-naming")
@Composable
fun AppTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    state: AppTextFieldState = AppTextFieldState(),
    style: AppTextFieldStyle = AppTextFieldStyle(),
    keyboard: AppTextFieldKeyboard = AppTextFieldKeyboard(),
) {
    val resolvedContainerColor = resolveContainerColor(style.containerColor)
    val resolvedContentColor = resolveContentColor(style.contentColor)
    val textStyle =
        buildTextStyle(
            contentColor = resolvedContentColor,
            fontSize = style.fontSize,
            lineHeight = style.lineHeight,
        )

    when (style.type) {
        AppTextFieldType.PRIMARY -> {
            PrimaryAppTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = modifier,
                state = state,
                style = style,
                textStyle = textStyle,
                containerColor = resolvedContainerColor,
                contentColor = resolvedContentColor,
                keyboard = keyboard,
            )
        }

        AppTextFieldType.SECONDARY -> {
            SecondaryAppTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = modifier,
                state = state,
                style = style,
                textStyle = textStyle,
                contentColor = resolvedContentColor,
                keyboard = keyboard,
            )
        }
    }
}

@Suppress("ktlint:standard:function-naming")
@Composable
private fun PrimaryAppTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier,
    state: AppTextFieldState,
    style: AppTextFieldStyle,
    textStyle: TextStyle,
    containerColor: Color,
    contentColor: Color,
    keyboard: AppTextFieldKeyboard,
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        modifier = applyOptionalBackground(modifier, style.backgroundBrush, style.shape),
        enabled = state.enabled,
        singleLine = state.singleLine,
        shape = style.shape,
        isError = state.isError,
        label =
            state.label?.let {
                { FieldDecorationText(text = it, style = style) }
            },
        placeholder =
            state.placeholder?.let {
                { FieldDecorationText(text = it, style = style) }
            },
        supportingText =
            state.supportingText?.let {
                { Text(text = it) }
            },
        textStyle = textStyle,
        keyboardOptions = keyboard.options,
        keyboardActions = keyboard.actions,
        colors =
            TextFieldDefaults.colors(
                focusedContainerColor = if (style.backgroundBrush != null) Color.Transparent else containerColor,
                unfocusedContainerColor = if (style.backgroundBrush != null) Color.Transparent else containerColor,
                disabledContainerColor = if (style.backgroundBrush != null) Color.Transparent else containerColor,
                errorContainerColor = if (style.backgroundBrush != null) Color.Transparent else containerColor,
                focusedTextColor = contentColor,
                unfocusedTextColor = contentColor,
                disabledTextColor = contentColor.copy(alpha = 0.6f),
                focusedLabelColor = contentColor,
                unfocusedLabelColor = contentColor.copy(alpha = 0.8f),
                focusedPlaceholderColor = contentColor.copy(alpha = 0.6f),
                unfocusedPlaceholderColor = contentColor.copy(alpha = 0.6f),
                cursorColor = contentColor,
            ),
    )
}

@Suppress("ktlint:standard:function-naming")
@Composable
private fun SecondaryAppTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier,
    state: AppTextFieldState,
    style: AppTextFieldStyle,
    textStyle: TextStyle,
    contentColor: Color,
    keyboard: AppTextFieldKeyboard,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        enabled = state.enabled,
        singleLine = state.singleLine,
        shape = style.shape,
        isError = state.isError,
        label =
            state.label?.let {
                { FieldDecorationText(text = it, style = style) }
            },
        placeholder =
            state.placeholder?.let {
                { FieldDecorationText(text = it, style = style) }
            },
        supportingText =
            state.supportingText?.let {
                { Text(text = it) }
            },
        textStyle = textStyle,
        keyboardOptions = keyboard.options,
        keyboardActions = keyboard.actions,
        colors =
            OutlinedTextFieldDefaults.colors(
                focusedTextColor = contentColor,
                unfocusedTextColor = contentColor,
                disabledTextColor = contentColor.copy(alpha = 0.6f),
                focusedLabelColor = contentColor,
                unfocusedLabelColor = contentColor.copy(alpha = 0.8f),
                focusedPlaceholderColor = contentColor.copy(alpha = 0.6f),
                unfocusedPlaceholderColor = contentColor.copy(alpha = 0.6f),
                cursorColor = contentColor,
                focusedBorderColor = contentColor,
                unfocusedBorderColor = contentColor.copy(alpha = 0.7f),
                disabledBorderColor = contentColor.copy(alpha = 0.4f),
            ),
    )
}

@Suppress("ktlint:standard:function-naming")
@Composable
private fun FieldDecorationText(
    text: String,
    style: AppTextFieldStyle,
) {
    Text(
        text = text,
        fontSize = style.fontSize,
        lineHeight = style.lineHeight,
    )
}

@Composable
private fun resolveContainerColor(color: Color): Color =
    if (color != Color.Unspecified) {
        color
    } else {
        MaterialTheme.colorScheme.surface
    }

@Composable
private fun resolveContentColor(color: Color): Color =
    if (color != Color.Unspecified) {
        color
    } else {
        MaterialTheme.colorScheme.onSurface
    }

@Composable
private fun buildTextStyle(
    contentColor: Color,
    fontSize: TextUnit,
    lineHeight: TextUnit,
): TextStyle =
    MaterialTheme.typography.bodyLarge.copy(
        color = contentColor,
        fontSize = fontSize,
        lineHeight = lineHeight,
    )

private fun applyOptionalBackground(
    modifier: Modifier,
    backgroundBrush: Brush?,
    shape: Shape,
): Modifier =
    if (backgroundBrush != null) {
        modifier.background(backgroundBrush, shape)
    } else {
        modifier
    }

@Suppress("ktlint:standard:function-naming")
@Preview(showBackground = true)
@Composable
private fun AppTextFieldPreview() {
    CodenamesTheme {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
        ) {
            AppTextField(
                value = "",
                onValueChange = {},
                state =
                    AppTextFieldState(
                        label = "Lobby ID",
                        placeholder = "z. B. ABC123",
                    ),
            )
        }
    }
}
