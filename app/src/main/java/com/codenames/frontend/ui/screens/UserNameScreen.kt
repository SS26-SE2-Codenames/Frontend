package com.codenames.frontend.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.codenames.frontend.ui.buttons.AppButton
import com.codenames.frontend.ui.buttons.AppButtonStyle
import com.codenames.frontend.ui.navigation.Screen

@Suppress("ktlint:standard:function-naming")
@Composable
fun UserNameScreen(navController: NavController) {
    var username by remember { mutableStateOf("") }

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .background(Color(0xFFf0d8ce)),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "Codenames",
            fontSize = 48.sp,
            modifier = Modifier.padding(bottom = 100.dp),
        )

        TextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("enter username") },
            modifier = Modifier.fillMaxWidth(0.5f),
        )

        Spacer(modifier = Modifier.height(10.dp))

        AppButton(
            text = "Continue",
            onClick = { navController.navigate("${Screen.Start.route}/$username") },
            modifier =
                Modifier
                    .width(220.dp)
                    .height(80.dp)
                    .testTag(JOIN_LOBBY_BUTTON_TAG),
            style =
                AppButtonStyle(
                    enabled = username.isNotBlank(),
                    backgroundBrush = blueGradient,
                    fontSize = 26.sp,
                    lineHeight = 30.sp,
                ),
        )
    }
}
