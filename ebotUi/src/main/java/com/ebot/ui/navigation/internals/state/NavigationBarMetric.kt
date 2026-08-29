package com.ebot.ui.navigation.internals.state

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D

internal data class NavigationBarMetric(
    val centerNotchX: Animatable<Float, AnimationVector1D>,
    val centerNotchY: Float,
    val totalWidth: Float,
    val totalHeight: Float,
    val notchWidth: Float,
)