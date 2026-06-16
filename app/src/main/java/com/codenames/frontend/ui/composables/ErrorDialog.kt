package com.codenames.frontend.ui.composables

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.codenames.frontend.ui.buttons.AppButton
import com.codenames.frontend.ui.buttons.AppButtonStyle
import com.codenames.frontend.ui.theme.AppBlue
import com.codenames.frontend.ui.theme.AppInkDark
import com.codenames.frontend.ui.theme.AppInkOverlay
import com.codenames.frontend.ui.theme.AppSurface
import com.codenames.frontend.ui.theme.AppWhite

@Suppress("ktlint:standard:function-naming")
@Composable
fun ErrorDialog(
    message: String,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = AppSurface,
        titleContentColor = AppInkDark,
        textContentColor = AppInkOverlay,
        title = {
            Text("Something went wrong")
        },
        text = {
            Text(text = message)
        },
        confirmButton = {
            AppButton(
                text = "OK",
                onClick = onDismiss,
                style =
                    AppButtonStyle(
                        containerColor = AppBlue,
                        contentColor = AppWhite,
                    ),
            )
        },
    )
}
