package com.ebot.ui.slider.internals

import android.util.Log
import androidx.compose.ui.geometry.Offset
import com.ebot.ui.slider.DASH_SIZE
import com.ebot.ui.slider.END_ANGLE
import com.ebot.ui.slider.START_ANGLE
import com.ebot.ui.slider.START_DASH_POS
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

internal fun Offset.calculateProgress(
    currentProgress: Float,
    centerX: Float,
    centerY: Float
): Float {
    var current = currentProgress
    val angle = Math.toDegrees(
        atan2(
            y = this.y - centerY,
            x = this.x - centerX
        ).toDouble()
    ).toFloat()

    val adjustedAngle = angle.plus(360f)
    val isInRange = adjustedAngle !in START_ANGLE..START_ANGLE.plus(END_ANGLE)

    val reAdjustedAngle = if (isInRange) adjustedAngle - 360f else adjustedAngle

    if (reAdjustedAngle in START_ANGLE..START_ANGLE.plus(END_ANGLE)) {
        current = (((reAdjustedAngle - START_ANGLE) / END_ANGLE) * 100f).coerceIn(0f, 100f)
    }
    return current
}

internal fun calculateDashCoordinates(
    centerX: Float,
    ringRadius: Float
): ArrayList<Pair<Offset, Offset>> {
    val dashesPositions: ArrayList<Pair<Offset, Offset>> = arrayListOf()

    val startPoint = Math.toRadians(START_DASH_POS)
    val innerRadius = ringRadius - centerX * .2f
    for (i in 0 until DASH_SIZE) {
        dashesPositions.add(
            Pair(
                Offset(
                    centerX + (ringRadius * cos(startPoint + i).toFloat()),
                    centerX + (ringRadius * sin(startPoint + i).toFloat()),
                ),
                Offset(
                    centerX + (innerRadius * cos(startPoint + i).toFloat()),
                    centerX + (innerRadius * sin(startPoint + i).toFloat()),
                )
            )
        )
    }
    return dashesPositions
}