package com.codenames.frontend.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import com.codenames.frontend.ui.buttons.AppButton
import com.codenames.frontend.ui.buttons.AppButtonStyle
import com.codenames.frontend.ui.navigation.Screen

@Composable
fun UserNameScreen(navController: NavController) {

    var username by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFf0d8ce)),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    )

    {
        Text(
            text = "Codenames", fontSize = 48.sp, modifier = Modifier.padding(bottom = 100.dp)
        )

        TextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("enter username") },
            modifier = Modifier.fillMaxWidth(0.5f)
        )

        Spacer(modifier = Modifier.height(10.dp))


        AppButton(
            text = "Continue",
            onClick = { navController.navigate("${Screen.Start.route}/$username") },
            modifier = Modifier
                .width(220.dp)
                .height(80.dp)
                .testTag(JOIN_LOBBY_BUTTON_TAG),
            style = AppButtonStyle(
                enabled = username.isNotBlank(),
                backgroundBrush = blueGradient,
                fontSize = 26.sp,
                lineHeight = 30.sp,
            ),
        )
    }
}



