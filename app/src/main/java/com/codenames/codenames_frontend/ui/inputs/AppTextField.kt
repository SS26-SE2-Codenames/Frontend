package com.codenames.codenames_frontend.ui.inputs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import com.codenames.codenames_frontend.ui.theme.CodenamesTheme

enum class AppTextFieldType {
    PRIMARY,
    SECONDARY
}

@Composable
fun AppTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    label: String? = null,
    placeholder: String? = null,
    type: AppTextFieldType = AppTextFieldType.PRIMARY,
    containerColor: Color = MaterialTheme.colorScheme.surface,
    contentColor: Color = MaterialTheme.colorScheme.onSurface,
    backgroundBrush: Brush? = null,
    fontSize: TextUnit = TextUnit.Unspecified,
    lineHeight: TextUnit = TextUnit.Unspecified,
    singleLine: Boolean = true,
    isError: Boolean = false,
    supportingText: String? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default
) {
    val shape = RoundedCornerShape(12.dp)

    when (type) {
        AppTextFieldType.PRIMARY -> {
            TextField(
                value = value,
                onValueChange = onValueChange,
                modifier = if (backgroundBrush != null) {
                    modifier.background(backgroundBrush, shape)
                } else {
                    modifier
                },
                enabled = enabled,
                singleLine = singleLine,
                shape = shape,
                isError = isError,
                label = if (label != null) {
                    {
                        Text(
                            text = label,
                            fontSize = fontSize,
                            lineHeight = lineHeight
                        )
                    }
                } else {
                    null
                },
                placeholder = if (placeholder != null) {
                    {
                        Text(
                            text = placeholder,
                            fontSize = fontSize,
                            lineHeight = lineHeight
                        )
                    }
                } else {
                    null
                },
                supportingText = if (supportingText != null) {
                    {
                        Text(text = supportingText)
                    }
                } else {
                    null
                },
                textStyle = MaterialTheme.typography.bodyLarge.copy(
                    color = contentColor,
                    fontSize = fontSize,
                    lineHeight = lineHeight
                ),
                keyboardOptions = keyboardOptions,
                keyboardActions = keyboardActions,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = if (backgroundBrush != null) Color.Transparent else containerColor,
                    unfocusedContainerColor = if (backgroundBrush != null) Color.Transparent else containerColor,
                    disabledContainerColor = if (backgroundBrush != null) Color.Transparent else containerColor,
                    errorContainerColor = if (backgroundBrush != null) Color.Transparent else containerColor,

                    focusedTextColor = contentColor,
                    unfocusedTextColor = contentColor,
                    disabledTextColor = contentColor.copy(alpha = 0.6f),

                    focusedLabelColor = contentColor,
                    unfocusedLabelColor = contentColor.copy(alpha = 0.8f),

                    focusedPlaceholderColor = contentColor.copy(alpha = 0.6f),
                    unfocusedPlaceholderColor = contentColor.copy(alpha = 0.6f),

                    cursorColor = contentColor
                )
            )
        }

        AppTextFieldType.SECONDARY -> {
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = modifier,
                enabled = enabled,
                singleLine = singleLine,
                shape = shape,
                isError = isError,
                label = if (label != null) {
                    {
                        Text(
                            text = label,
                            fontSize = fontSize,
                            lineHeight = lineHeight
                        )
                    }
                } else {
                    null
                },
                placeholder = if (placeholder != null) {
                    {
                        Text(
                            text = placeholder,
                            fontSize = fontSize,
                            lineHeight = lineHeight
                        )
                    }
                } else {
                    null
                },
                supportingText = if (supportingText != null) {
                    {
                        Text(text = supportingText)
                    }
                } else {
                    null
                },
                textStyle = MaterialTheme.typography.bodyLarge.copy(
                    color = contentColor,
                    fontSize = fontSize,
                    lineHeight = lineHeight
                ),
                keyboardOptions = keyboardOptions,
                keyboardActions = keyboardActions,
                colors = OutlinedTextFieldDefaults.colors(
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
                    disabledBorderColor = contentColor.copy(alpha = 0.4f)
                )
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun AppTextFieldPreview() {
    CodenamesTheme {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            AppTextField(
                value = "",
                onValueChange = {},
                label = "Lobby ID",
                placeholder = "z. B. ABC123",
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}