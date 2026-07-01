package com.codenames.frontend.ui.composables

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource

@Suppress("ktlint:standard:function-naming")
@Composable
fun ScreenBackground(
    @DrawableRes imageRes: Int,
    modifier: Modifier = Modifier,
) {
    Image(
        painter = painterResource(id = imageRes),
        contentDescription = null,
        modifier = modifier.fillMaxSize(),
        contentScale = ContentScale.Crop,
    )
}
