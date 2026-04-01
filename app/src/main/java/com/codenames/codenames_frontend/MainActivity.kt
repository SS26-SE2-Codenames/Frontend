package com.codenames.codenames_frontend

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.codenames.codenames_frontend.ui.buttons.AppButton
import com.codenames.codenames_frontend.ui.buttons.AppButtonType
import com.codenames.codenames_frontend.ui.theme.CodenamesTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CodenamesTheme {
                ButtonTestScreen()
            }
        }
    }
}

@Composable
fun ButtonTestScreen() {
    var message by remember { mutableStateOf("Noch nichts geklickt") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(message)

        AppButton(
            text = "Spiel erstellen",
            onClick = {
                message = "Primary Button wurde geklickt"
            }
        )

        AppButton(
            text = "Beitreten",
            onClick = {
                message = "Secondary Button wurde geklickt"
            },
            type = AppButtonType.SECONDARY
        )
    }
}