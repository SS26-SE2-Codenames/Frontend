package com.codenames.frontend.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.codenames.frontend.ui.buttons.AppButton
import com.codenames.frontend.ui.buttons.AppButtonStyle
import com.codenames.frontend.ui.buttons.ReturnCornerButton
import com.codenames.frontend.ui.theme.blueGradient
import com.codenames.frontend.ui.theme.greenGradient

@Suppress("ktlint:standard:function-naming")
@Composable
fun SettingsScreen(navController: NavHostController) {
    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .background(Color(0xFFf0d8ce)),
    ) {
        Text(
            text = "SETTINGS",
            color = Color(0xFF383330),
            fontSize = 36.sp,
            fontWeight = FontWeight.Bold,
            modifier =
                Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 48.dp),
        )

        Column(
            modifier = Modifier.align(Alignment.Center),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            AppButton(
                text = "Toggle SFX",
                onClick = {},
                modifier =
                    Modifier
                        .width(240.dp)
                        .height(80.dp),
                style =
                    AppButtonStyle(
                        backgroundBrush = blueGradient,
                        fontSize = 24.sp,
                        lineHeight = 28.sp,
                    ),
            )

            AppButton(
                text = "Toggle Music",
                onClick = {},
                modifier =
                    Modifier
                        .width(240.dp)
                        .height(80.dp),
                style =
                    AppButtonStyle(
                        backgroundBrush = greenGradient,
                        fontSize = 24.sp,
                        lineHeight = 28.sp,
                    ),
            )
        }

        ReturnCornerButton(
            onClick = { navController.popBackStack() },
        )
    }
}
