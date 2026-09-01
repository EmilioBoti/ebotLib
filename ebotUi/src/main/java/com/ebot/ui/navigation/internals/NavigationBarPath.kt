package com.ebot.ui.navigation.internals

import android.graphics.Path
import android.graphics.RectF
import com.ebot.ui.navigation.DEFAULT_NOTCH_ARC_ANGLE
import com.ebot.ui.navigation.internals.state.NotchPathGeometry

internal fun createNavigationBarPath(pathGeometry: NotchPathGeometry): Path {
    return Path().apply {
        this.moveTo(pathGeometry.originX, pathGeometry.originY)
        this.lineTo(pathGeometry.xP1, pathGeometry.yP1)
        this.quadTo(
            pathGeometry.leftCurveControlX,
            pathGeometry.leftCurveControlY,
            pathGeometry.leftCurveX,
            pathGeometry.leftCurveY,
        )
        this.arcTo(
            RectF(
                pathGeometry.arcLeft,
                pathGeometry.arcTop,
                pathGeometry.arcRight,
                pathGeometry.arcBottom,
            ),
            DEFAULT_NOTCH_ARC_ANGLE,
            DEFAULT_NOTCH_ARC_ANGLE,
            false
        )
        this.lineTo(pathGeometry.xP2, pathGeometry.yP2)
        this.quadTo(
            pathGeometry.rightCurveControlX,
            pathGeometry.rightCurveControlY,
            pathGeometry.rightCurveX,
            pathGeometry.rightCurveY,
        )
        this.lineTo(pathGeometry.xP3, pathGeometry.yP3)
        this.lineTo(pathGeometry.xP4, pathGeometry.yP4)
        this.lineTo(pathGeometry.xP5, pathGeometry.yP5)
        this.close()
    }
}