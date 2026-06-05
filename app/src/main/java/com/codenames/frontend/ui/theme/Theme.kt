package com.codenames.frontend.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme =
    darkColorScheme(
        primary = AppBlueLight,
        secondary = AppRedLight,
        tertiary = AppGreen,
        background = AppInkDark,
        surface = AppInk,
        onPrimary = AppWhite,
        onSecondary = AppWhite,
        onTertiary = AppWhite,
        onBackground = AppWhite,
        onSurface = AppWhite,
    )

private val LightColorScheme =
    lightColorScheme(
        primary = AppBlue,
        secondary = AppRed,
        tertiary = AppGreen,
        background = AppBackground,
        surface = MaterialLightSurface,
        onPrimary = AppWhite,
        onSecondary = AppWhite,
        onTertiary = AppWhite,
        onBackground = MaterialDarkText,
        onSurface = MaterialDarkText,
    )

@Suppress("ktlint:standard:function-naming")
@Composable
fun CodenamesTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit,
) {
    val colorScheme =
        when {
            dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
                val context = LocalContext.current
                if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
            }

            darkTheme -> DarkColorScheme
            else -> LightColorScheme
        }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content,
    )
}
