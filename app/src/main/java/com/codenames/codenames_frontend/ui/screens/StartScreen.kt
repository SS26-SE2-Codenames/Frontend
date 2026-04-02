package com.codenames.codenames_frontend.ui.screens
import com.codenames.codenames_frontend.ui.buttons.AppButton
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import com.codenames.codenames_frontend.ui.navigation.Screen


@Composable
fun StartScreen(navController: NavHostController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AppButton(
            text = "Create Lobby",
            onClick = {
                navController.navigate(Screen.Lobby.route)
            }
        )
        AppButton(
            text = "Join Lobby",
            onClick = {
                navController.navigate(Screen.JoinLobby.route)
            }
        )
    }
}


