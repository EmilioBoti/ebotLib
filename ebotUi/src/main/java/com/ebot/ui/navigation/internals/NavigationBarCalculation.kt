package com.ebot.ui.navigation.internals

import androidx.compose.ui.geometry.Offset
import com.ebot.ui.navigation.DEFAULT_EDGE
import com.ebot.ui.navigation.internals.state.IndicatorCoordinate
import com.ebot.ui.navigation.internals.state.NavigationBarMetric
import com.ebot.ui.navigation.internals.state.NotchPathGeometry

internal fun calculateNotchPathCoordinates(drawingMetaData: NavigationBarMetric): NotchPathGeometry {
    val currentCenterX = drawingMetaData.centerNotchX
    val notchHeight = drawingMetaData.totalHeight - drawingMetaData.totalHeight.times(0.3f) + drawingMetaData.itemMargin

    return NotchPathGeometry(
        originX = DEFAULT_EDGE,
        originY = DEFAULT_EDGE,
        xP1 = currentCenterX - drawingMetaData.notchWidth - drawingMetaData.curve,
        yP1 = DEFAULT_EDGE,
        leftCurveControlX = currentCenterX - drawingMetaData.notchWidth,
        leftCurveControlY = DEFAULT_EDGE,
        leftCurveX = currentCenterX - drawingMetaData.notchWidth,
        leftCurveY = drawingMetaData.curve,
        arcLeft = currentCenterX - drawingMetaData.notchWidth,
        arcTop = DEFAULT_EDGE,
        arcRight = currentCenterX + drawingMetaData.notchWidth,
        arcBottom = notchHeight,
        xP2 = currentCenterX + drawingMetaData.notchWidth,
        yP2 = drawingMetaData.curve,
        rightCurveControlX = currentCenterX + drawingMetaData.notchWidth,
        rightCurveControlY = DEFAULT_EDGE,
        rightCurveX = currentCenterX + drawingMetaData.notchWidth + drawingMetaData.curve,
        rightCurveY = DEFAULT_EDGE,
        xP3 = drawingMetaData.totalWidth - DEFAULT_EDGE,
        yP3 = DEFAULT_EDGE,
        xP4 = drawingMetaData.totalWidth - DEFAULT_EDGE,
        yP4 = drawingMetaData.totalHeight - DEFAULT_EDGE,
        xP5 = DEFAULT_EDGE,
        yP5 = drawingMetaData.totalHeight - DEFAULT_EDGE,
    )
}

/**
 * @param margin value from 0.0 to 1.0
 */
internal fun calculateIndicatorCoordinates(
    notchWidth: Float,
    currentCenterX: Float,
    currentCenterY: Float,
    margin: Float,
): IndicatorCoordinate {
    val indicatorOffset = Offset(x = currentCenterX, y = currentCenterY - currentCenterY * currentCenterY.times(0.0022f))
    return IndicatorCoordinate(
        radius = notchWidth - notchWidth * margin.coerceIn(0f, 1f),
        offset = indicatorOffset
    )
}

internal fun calculateAvailableSpace(
    measurablesSize: Int,
    totalWidth: Int,
    constraintsMaxWidth: Int,
): Int {
    return if (totalWidth > constraintsMaxWidth) {
        constraintsMaxWidth / measurablesSize
    } else {
        (constraintsMaxWidth - totalWidth) / measurablesSize
    }
}
