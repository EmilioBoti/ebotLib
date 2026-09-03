package com.ebot.ui.slider.internals

import androidx.compose.ui.graphics.Color


data class SliderColor(
    val progressColor: Color,
    val trackColor: Color,
    val ringColor: Color,
    val dashColor: Color,
    val brush: SliderBrush?
)

data class SliderBrush(val colors: List<Color>)