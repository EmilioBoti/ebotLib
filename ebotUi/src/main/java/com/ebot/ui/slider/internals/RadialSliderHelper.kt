package com.ebot.ui.slider.internals

import android.graphics.Paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb

internal fun createStrokePaint(color: Color, trackWidth: Float = 0f): Paint {
    return Paint().apply {
        this.color = color.toArgb()
        this.isAntiAlias = true
        this.strokeCap = Paint.Cap.ROUND
        this.strokeWidth = trackWidth
        this.style = Paint.Style.STROKE
    }
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