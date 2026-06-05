package com.codenames.frontend.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.codenames.frontend.ui.theme.AppBackground
import com.codenames.frontend.ui.theme.LocalResponsiveDimensions

@Suppress("ktlint:standard:function-naming")
@Composable
fun GameSettingsScreen() {
    val dimensions = LocalResponsiveDimensions.current

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(dimensions.screenPadding)
                .background(AppBackground),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text("Insert Game Settings here")
    }
}
