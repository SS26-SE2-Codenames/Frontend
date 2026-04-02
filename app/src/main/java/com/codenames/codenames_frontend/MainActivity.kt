package com.codenames.codenames_frontend

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import com.codenames.codenames_frontend.ui.navigation.NavGraph
import com.codenames.codenames_frontend.ui.theme.CodenamesTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CodenamesTheme {
                AppContent()
            }
        }
    }
}

@Composable
fun AppContent() {
    NavGraph()
}