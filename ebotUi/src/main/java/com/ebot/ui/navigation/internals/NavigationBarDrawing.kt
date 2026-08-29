package com.ebot.ui.navigation.internals

import android.graphics.Paint
import androidx.compose.ui.draw.CacheDrawScope
import androidx.compose.ui.draw.DrawResult
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import com.ebot.ui.navigation.NavigationBarColor
import com.ebot.ui.navigation.internals.state.NavigationBarMetric


internal fun CacheDrawScope.drawBackgroundNotch(
    drawingMetaData: NavigationBarMetric,
    colors: NavigationBarColor,
): DrawResult {
    val containerColor = colors.container.toArgb()
    val notchWidth = drawingMetaData.notchWidth

    val pathPaint = Paint().apply {
        this.color = containerColor
        this.isAntiAlias = true
        this.strokeWidth = 8f
        this.style = Paint.Style.FILL
        this.setShadowLayer(
            8f,
            0f,
            -1f,
            android.graphics.Color.argb(80, 0, 0, 0)
        )
    }

    val pathGeometry = calculateNotchPathGeometry(drawingMetaData = drawingMetaData)
    val backgroundNotchPath = createNavigationBarPath(pathGeometry)

    return onDrawBehind {
        drawIntoCanvas { canvas ->
            val nativeCanvas = canvas.nativeCanvas
            nativeCanvas.drawPath(
                backgroundNotchPath,
                pathPaint
            )
        }
        drawCircle(
            color = colors.indicatorColor,
            radius = notchWidth - notchWidth * 0.14f,
            center = pathGeometry.indicatorOffset
        )
    }
}