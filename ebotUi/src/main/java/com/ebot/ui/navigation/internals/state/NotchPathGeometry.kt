package com.ebot.ui.navigation.internals.state

import androidx.compose.ui.geometry.Offset

internal data class NotchPathGeometry(
    val originX: Float,
    val originY: Float,
    val xP1: Float,
    val yP1: Float,
    val leftCurveControlX: Float,
    val leftCurveControlY: Float,
    val leftCurveX: Float,
    val leftCurveY: Float,
    val arcLeft: Float,
    val arcTop: Float,
    val arcRight: Float,
    val arcBottom: Float,
    val xP2: Float,
    val yP2: Float,
    val rightCurveControlX: Float,
    val rightCurveControlY: Float,
    val rightCurveX: Float,
    val rightCurveY: Float,
    val xP3: Float,
    val yP3: Float,
    val xP4: Float,
    val yP4: Float,
    val xP5: Float,
    val yP5: Float,
    val indicatorOffset: Offset
)
