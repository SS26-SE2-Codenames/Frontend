package com.codenames.codenames_frontend.ui.buttons

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.tooling.preview.Preview
import com.codenames.codenames_frontend.ui.theme.CodenamesTheme

enum class AppButtonType {
    PRIMARY,
    SECONDARY
}

@Composable
fun AppButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    type: AppButtonType = AppButtonType.PRIMARY
) {
    when (type) {
        AppButtonType.PRIMARY -> {
            Button(
                onClick = onClick,
                modifier = modifier,
                enabled = enabled,
                shape = RoundedCornerShape(12.dp),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Text(
                    text = text,
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }

        AppButtonType.SECONDARY -> {
            OutlinedButton(
                onClick = onClick,
                modifier = modifier,
                enabled = enabled,
                shape = RoundedCornerShape(12.dp),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp)
            ) {
                Text(
                    text = text,
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = false)
@Composable
fun AppButtonPreview() {
    CodenamesTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            AppButton(
                text = "Spiel erstellen",
                onClick = {}
            )

            AppButton(
                text = "Beitreten",
                onClick = {},
                type = AppButtonType.SECONDARY
            )
        }
    }
}