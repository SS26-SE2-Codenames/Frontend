package com.codenames.frontend

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import com.codenames.frontend.ui.navigation.NavGraph
import com.codenames.frontend.ui.theme.CodenamesTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
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

@Suppress("ktlint:standard:function-naming")
@Composable
fun AppContent() {
    NavGraph()
}
