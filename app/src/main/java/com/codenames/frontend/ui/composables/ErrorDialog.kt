package com.codenames.frontend.ui.composables

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.codenames.frontend.ui.buttons.AppButton
import com.codenames.frontend.ui.theme.AppInk

@Suppress("ktlint:standard:function-naming")
@Composable
fun ErrorDialog(
    message: String,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Something went wrong")
        },
        text = {
            Text(
                text = message,
                color = AppInk,
            )
        },
        confirmButton = {
            AppButton(
                text = "OK",
                onClick = onDismiss,
            )
        },
    )
}
