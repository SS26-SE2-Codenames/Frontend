package com.codenames.codenames_frontend.ui.screens
import com.codenames.codenames_frontend.ui.buttons.AppButton
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.codenames.codenames_frontend.ui.navigation.Screen


@Composable
fun LobbyScreen(navController: NavHostController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AppButton(
            text = "Join Operators",
            onClick = {
                navController.navigate(Screen.Gameboard.route)
            }
        ) //just to test navigation
    }
}



