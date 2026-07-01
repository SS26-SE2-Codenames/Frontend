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
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavHostController
import com.codenames.frontend.R
import com.codenames.frontend.ui.buttons.AppButton
import com.codenames.frontend.ui.buttons.AppButtonStyle
import com.codenames.frontend.ui.buttons.ReturnCornerButton
import com.codenames.frontend.ui.composables.ScreenBackground
import com.codenames.frontend.ui.theme.AppBackground
import com.codenames.frontend.ui.theme.AppInk
import com.codenames.frontend.ui.theme.LocalResponsiveDimensions
import com.codenames.frontend.ui.theme.blueGradient
import com.codenames.frontend.ui.theme.greenGradient

@Suppress("ktlint:standard:function-naming")
@Composable
fun SettingsScreen(navController: NavHostController) {
    val dimensions = LocalResponsiveDimensions.current

    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .background(AppBackground),
    ) {
        ScreenBackground(R.drawable.main_menu_art)
        Text(
            text = "SETTINGS",
            color = AppInk,
            fontSize = dimensions.titleFontSize,
            fontWeight = FontWeight.Bold,
            modifier =
                Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = dimensions.sectionSpacing * 2),
        )

        Column(
            modifier = Modifier.align(Alignment.Center),
            verticalArrangement = Arrangement.spacedBy(dimensions.itemSpacing),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            AppButton(
                text = "Toggle SFX",
                onClick = {},
                modifier =
                    Modifier
                        .width(dimensions.primaryButtonWidth)
                        .height(dimensions.primaryButtonHeight),
                style =
                    AppButtonStyle(
                        backgroundBrush = blueGradient,
                        fontSize = dimensions.buttonFontSize,
                        lineHeight = dimensions.buttonLineHeight,
                    ),
            )

            AppButton(
                text = "Toggle Music",
                onClick = {},
                modifier =
                    Modifier
                        .width(dimensions.primaryButtonWidth)
                        .height(dimensions.primaryButtonHeight),
                style =
                    AppButtonStyle(
                        backgroundBrush = greenGradient,
                        fontSize = dimensions.buttonFontSize,
                        lineHeight = dimensions.buttonLineHeight,
                    ),
            )
        }

        ReturnCornerButton(
            onClick = { navController.popBackStack() },
        )
    }
}
