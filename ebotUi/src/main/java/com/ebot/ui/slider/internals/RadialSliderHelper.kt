package com.ebot.ui.slider.internals

import android.graphics.Paint
import android.graphics.SweepGradient
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import kotlin.collections.map

internal fun createStrokePaint(color: Color, trackWidth: Float = 0f, colors: List<Color>? = null): Paint {
    return Paint().apply {
        this.color = color.toArgb()
        this.isAntiAlias = true
        this.strokeCap = Paint.Cap.ROUND
        this.strokeWidth = trackWidth
        this.style = Paint.Style.STROKE
        if (colors?.isNotEmpty() == true && colors.size >= 2) {
            this.shader = getGradient(colors)
        }
    }
}

internal fun getGradient(colors: List<Color>): SweepGradient {
    var pos = 0.0f
    val colorsInt = colors.map { it.toArgb() }.toIntArray()
    val positions = FloatArray(colors.size) { index ->
        if (index == 0) {
            pos
        } else {
            pos += 0.23f
            pos
        }
    }
    return SweepGradient(0.0f, 2.0f, colorsInt, positions)
}

internal fun createShadowFillPaint(color: Color): Paint {
    return Paint().apply {
        this.color = color.toArgb()
        this.isAntiAlias = true
        this.setShadowLayer(
            12f,
            0f,
            2f,
            android.graphics.Color.argb(80, 0, 0, 0)
        )
    }
}