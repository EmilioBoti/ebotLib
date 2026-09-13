package com.ebot.ui.slider.internals

import android.graphics.Path
import androidx.compose.ui.geometry.Offset


internal fun createRadialSliderDashPath(dashesCoordinates: ArrayList<Pair<Offset, Offset>>): Path {
    return Path().apply {
        dashesCoordinates.forEach {
            this.moveTo(
                it.first.x,
                it.first.y,
            )
            this.lineTo(
                it.second.x,
                it.second.y,
            )
        }
        this.close()
    }
}