package com.ebot.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

object NavigationDefaults {

    @Composable
    fun colors(
        container: Color = Color.White,
        indicatorColor: Color = Color.Unspecified,
    ): NavigationBarColor {
        return NavigationBarColor(
            container = container,
            indicatorColor = indicatorColor
        )
    }

}