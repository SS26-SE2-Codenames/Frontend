package com.codenames.frontend.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.platform.testTag
import androidx.navigation.NavController
import com.codenames.frontend.ui.buttons.AppButton
import com.codenames.frontend.ui.buttons.AppButtonStyle
import com.codenames.frontend.ui.buttons.SettingsCornerButton
import com.codenames.frontend.ui.navigation.Screen
import com.codenames.frontend.ui.theme.AppBackground
import com.codenames.frontend.ui.theme.LocalResponsiveDimensions
import com.codenames.frontend.ui.theme.blueGradient
import com.codenames.frontend.viewmodel.SessionViewModel

@Suppress("ktlint:standard:function-naming")
@Composable
fun UserNameScreen(
    navController: NavController,
    viewModel: SessionViewModel,
) {
    val dimensions = LocalResponsiveDimensions.current
    var username by remember { mutableStateOf("") }

    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .background(AppBackground),
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(dimensions.screenPadding),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = "Codenames",
                fontSize = dimensions.titleFontSize,
                modifier = Modifier.padding(bottom = dimensions.sectionSpacing * 3),
            )

            TextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("enter username") },
                modifier = Modifier.fillMaxWidth(if (dimensions.isNarrowWidth) 0.7f else 0.5f),
            )

            Spacer(modifier = Modifier.height(dimensions.itemSpacing))

            AppButton(
                text = "Continue",
                onClick = {
                    if (username.isBlank()) return@AppButton
                    viewModel.setUsername(username)
                    navController.navigate(Screen.Start.route)
                },
                modifier =
                    Modifier
                        .width(dimensions.primaryButtonWidth)
                        .height(dimensions.primaryButtonHeight)
                        .testTag(JOIN_LOBBY_BUTTON_TAG),
                style =
                    AppButtonStyle(
                        enabled = username.isNotBlank(),
                        backgroundBrush = blueGradient,
                        fontSize = dimensions.buttonFontSize,
                        lineHeight = dimensions.buttonLineHeight,
                    ),
            )
        }

        SettingsCornerButton(
            onClick = { navController.navigate(Screen.Settings.route) },
        )
    }
}
