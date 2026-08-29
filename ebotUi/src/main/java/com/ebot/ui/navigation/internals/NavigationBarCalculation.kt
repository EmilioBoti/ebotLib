package com.ebot.ui.navigation.internals

import androidx.compose.ui.geometry.Offset
import com.ebot.ui.navigation.DEFAULT_EDGE
import com.ebot.ui.navigation.DEFAULT_MARGIN_CURVE
import com.ebot.ui.navigation.DEFAULT_MARGIN_ITEM
import com.ebot.ui.navigation.internals.state.NavigationBarMetric
import com.ebot.ui.navigation.internals.state.NotchPathGeometry

internal fun calculateNotchPathGeometry(drawingMetaData: NavigationBarMetric): NotchPathGeometry {
    val currentCenterX = drawingMetaData.centerNotchX.value
    val currentCenterY = drawingMetaData.centerNotchY
    val notchHeight = drawingMetaData.totalHeight - drawingMetaData.totalHeight.times(0.3f) + DEFAULT_MARGIN_ITEM
    val indicatorOffset = Offset(x = currentCenterX, y = currentCenterY - currentCenterY * currentCenterY.times(0.0022f))

    return NotchPathGeometry(
        originX = DEFAULT_EDGE,
        originY = DEFAULT_EDGE,
        xP1 = currentCenterX - drawingMetaData.notchWidth - DEFAULT_MARGIN_CURVE,
        yP1 = DEFAULT_EDGE,
        leftCurveControlX = currentCenterX - drawingMetaData.notchWidth,
        leftCurveControlY = DEFAULT_EDGE,
        leftCurveX = currentCenterX - drawingMetaData.notchWidth,
        leftCurveY = DEFAULT_MARGIN_CURVE,
        arcLeft = currentCenterX - drawingMetaData.notchWidth,
        arcTop = DEFAULT_EDGE,
        arcRight = currentCenterX + drawingMetaData.notchWidth,
        arcBottom = notchHeight,
        xP2 = currentCenterX + drawingMetaData.notchWidth,
        yP2 = DEFAULT_MARGIN_CURVE,
        rightCurveControlX = currentCenterX + drawingMetaData.notchWidth,
        rightCurveControlY = DEFAULT_EDGE,
        rightCurveX = currentCenterX + drawingMetaData.notchWidth + DEFAULT_MARGIN_CURVE,
        rightCurveY = DEFAULT_EDGE,
        xP3 = drawingMetaData.totalWidth - DEFAULT_EDGE,
        yP3 = DEFAULT_EDGE,
        xP4 = drawingMetaData.totalWidth - DEFAULT_EDGE,
        yP4 = drawingMetaData.totalHeight - DEFAULT_EDGE,
        xP5 = DEFAULT_EDGE,
        yP5 = drawingMetaData.totalHeight - DEFAULT_EDGE,
        indicatorOffset = indicatorOffset
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
