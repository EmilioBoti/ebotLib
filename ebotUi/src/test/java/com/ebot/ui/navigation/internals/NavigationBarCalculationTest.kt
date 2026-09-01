package com.ebot.ui.navigation.internals

import com.ebot.ui.navigation.DEFAULT_MARGIN_CURVE
import com.ebot.ui.navigation.DEFAULT_MARGIN_ITEM
import com.ebot.ui.navigation.internals.state.NavigationBarMetric
import com.ebot.ui.navigation.internals.state.NotchPathGeometry
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*

class NavigationBarCalculationTest {

    private lateinit var metrics: NavigationBarMetric

    companion object {
        const val MARGIN_ITEM = DEFAULT_MARGIN_ITEM
        const val CURVE_NOTCH = DEFAULT_MARGIN_CURVE
    }

    @Before
    fun setUp() {
        metrics = NavigationBarMetric(
            centerNotchX = 150f,
            centerNotchY = 60f,
            totalWidth = 300f,
            totalHeight = 120f,
            notchWidth = 50f,
            itemMargin = MARGIN_ITEM,
            curve = CURVE_NOTCH
        )
    }

    private fun getCalculateNotchPathCoordinates(metrics: NavigationBarMetric): NotchPathGeometry {
        return calculateNotchPathCoordinates(drawingMetaData = metrics)
    }

    @Test
    fun `given valid metrics when calculating then returns correct origin`() {
        val coordinates = getCalculateNotchPathCoordinates(metrics)

        assertEquals(0f, coordinates.originX)
        assertEquals(0f, coordinates.originY)
    }

    @Test
    fun `given valid metrics when calculating then returns correct notch coordinates`() {
        val coordinates = getCalculateNotchPathCoordinates(metrics)

        assertEquals(40f, coordinates.xP1)
        assertEquals(0f, coordinates.yP1)
        assertEquals(100f, coordinates.leftCurveControlX)
        assertEquals(0f, coordinates.leftCurveControlY)
        assertEquals(100f, coordinates.leftCurveX)
        assertEquals(60f, coordinates.leftCurveY)
        assertEquals(200f, coordinates.xP2)
        assertEquals(60f, coordinates.yP2)
        assertEquals(200f, coordinates.rightCurveControlX)
        assertEquals(0f, coordinates.rightCurveControlY)
        assertEquals(260f, coordinates.rightCurveX)
        assertEquals(0f, coordinates.rightCurveY)
        assertEquals(100f, coordinates.arcLeft)
        assertEquals(0f, coordinates.arcTop)
        assertEquals(200f, coordinates.arcRight)
        assertEquals(94f, coordinates.arcBottom)
    }

    @Test
    fun `given valid metrics when calculating then returns correct indicator coordinates`() {
        val coordinates = calculateIndicatorCoordinates(metrics.notchWidth, metrics.centerNotchX, metrics.centerNotchY, margin = 0.14f)

        assertEquals(43f, coordinates.radius, 0.001f)
        assertEquals(150f, coordinates.offset.x, 0.001f)
        assertEquals(52.08f, coordinates.offset.y, 0.001f)
    }

    @Test
    fun `given valid metrics when calculating coordinates then returns correct boundaries`() {
        val coordinates = getCalculateNotchPathCoordinates(metrics)

        assertEquals(300f, coordinates.xP3)
        assertEquals(300f, coordinates.xP4)
        assertEquals(0f, coordinates.xP5)
        assertEquals(0f, coordinates.yP3)
        assertEquals(120f, coordinates.yP4)
        assertEquals(120f, coordinates.yP5)
    }

    @Test
    fun `given total width exceeds max width when calculating available space then divides max width by measurables size`() {
        val result = calculateAvailableSpace(5, 300, 250)
        assertEquals(50, result)
    }

    @Test
    fun `given total width does not exceed max width when calculating available space then divides remaining width by measurables size`() {
        val result = calculateAvailableSpace(5, 300, 350)
        assertEquals(10, result)
    }

    @Test
    fun `given total width equals max width when calculating available space then returns zero`() {
        val result = calculateAvailableSpace(5, 300, 300)
        assertEquals(0, result)
    }

}