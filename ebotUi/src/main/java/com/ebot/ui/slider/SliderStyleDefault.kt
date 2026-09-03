package com.ebot.ui.slider

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.ebot.ui.slider.internals.SliderColor

object SliderStyleDefault {

    @Composable
    fun sliderColor(
        progressColor: Color = Color.Blue,
        trackColor: Color = Color.LightGray,
        ringColor: Color = Color.White,
        dashColor: Color = Color.LightGray
    ): SliderColor {
        return SliderColor(
            progressColor = progressColor,
            trackColor = trackColor,
            ringColor = ringColor,
            dashColor = dashColor
        )
    }
}