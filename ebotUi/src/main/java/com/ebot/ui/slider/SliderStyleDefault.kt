package com.ebot.ui.slider

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.ebot.ui.slider.internals.SliderColor
import com.ebot.ui.slider.internals.SliderBrush

object SliderStyleDefault {

    @Composable
    fun sliderColor(
        progressColor: Color = Color.Blue,
        trackColor: Color = Color.LightGray,
        ringColor: Color = Color.White,
        dashColor: Color = Color.LightGray,
        brush: SliderBrush? = null
    ): SliderColor {
        return SliderColor(
            progressColor = progressColor,
            trackColor = trackColor,
            ringColor = ringColor,
            dashColor = dashColor,
            brush = brush
        )
    }

    @Composable
    fun sliderGradientColor(
        colors: List<Color> = listOf(Color.Blue, Color.Blue)
    ): SliderBrush {
        return SliderBrush(
            colors = colors
        )
    }

}