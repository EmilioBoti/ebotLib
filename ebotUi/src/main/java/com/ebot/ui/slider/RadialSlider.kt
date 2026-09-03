package com.ebot.ui.slider

import android.graphics.RectF
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.center
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.center
import androidx.compose.ui.unit.dp
import com.ebot.ui.slider.internals.SliderColor
import com.ebot.ui.slider.internals.calculateDashCoordinates
import com.ebot.ui.slider.internals.calculateProgress
import com.ebot.ui.slider.internals.createRadialSliderDashPath
import com.ebot.ui.slider.internals.createShadowFillPaint
import com.ebot.ui.slider.internals.createStrokePaint
import kotlin.math.min

internal const val START_ANGLE = 135F
internal const val END_ANGLE = 270F
internal const val DASH_SIZE = 44
internal const val START_DASH_POS = 90.0

@Composable
fun RadialSlider(
    modifier: Modifier = Modifier,
    progress: () -> Float,
    trackWidth: Dp = 8.dp,
    dashWidth: Dp = 2.dp,
    colors: SliderColor = SliderStyleDefault.sliderColor(),
    onChange: (progress: Float) -> Unit = {},
    content: @Composable () -> Unit
) {
    Layout(
        modifier = modifier
            .pointerInput(Unit) {
                detectDragGestures(
                    onDrag = { change, _ ->
                        val progress = change.position.calculateProgress(
                            progress(),
                            this.size.center.x.toFloat(),
                            this.size.center.y.toFloat()
                        )
                        onChange(progress)
                    }
                )
            }
            .drawWithCache {
                // ARC AREA
                val arc = RectF(
                    trackWidth.toPx(),
                    trackWidth.toPx(),
                    this.size.width - trackWidth.toPx(),
                    this.size.height - trackWidth.toPx()
                )
                val ringRadius = arc.width() / 2f - trackWidth.toPx()

                val ringPaint = createShadowFillPaint(colors.ringColor)
                val dashPaint = createStrokePaint(colors.dashColor, dashWidth.toPx())
                val trackPaint = createStrokePaint(colors.trackColor, trackWidth.toPx())
                val trackProgressPaint = createStrokePaint(colors.progressColor, trackWidth.toPx())

                val dashesCoordinates = calculateDashCoordinates(centerX = this.size.width / 2f, ringRadius = ringRadius - trackWidth.toPx())
                val dashLinesPath = createRadialSliderDashPath(dashesCoordinates)

                onDrawBehind {
                    drawIntoCanvas { canvas ->
                        // DRAW RING
                        canvas.nativeCanvas.drawCircle(
                            this.size.center.x,
                            this.size.center.y,
                            ringRadius,
                            ringPaint
                        )
                        // DRAW DASH LINES
                        canvas.nativeCanvas.drawPath(
                            dashLinesPath,
                            dashPaint
                        )
                        // DRAW TRACK
                        canvas.nativeCanvas.drawArc(
                            arc,
                            START_ANGLE,
                            END_ANGLE,
                            false,
                            trackPaint
                        )
                        // DRAW PROGRESS TRACK
                        val progressAngle = (progress() / 100f) * END_ANGLE // convert progress range (0 to 100) to angle (135 -  270)

                        canvas.nativeCanvas.drawArc(
                            arc,
                            START_ANGLE,
                            progressAngle,
                            false,
                            trackProgressPaint
                        )
                    }
                }
            },
        content = content
    ) { measurables, constraints ->

        val sizeLayout = min(constraints.maxWidth, constraints.maxHeight)

        val fullRadius = sizeLayout / 2f
        val insetHalf = fullRadius - trackWidth.toPx()

        val innerDashRadius = fullRadius - insetHalf.times(0.12f) - insetHalf.times(0.47f)
        val contentDiameter = innerDashRadius.times(2f)

        val placeableItem = measurables.first().measure(
            constraints = constraints.copy(
                minWidth = contentDiameter.toInt(),
                maxWidth = contentDiameter.toInt(),
                minHeight = contentDiameter.toInt(),
                maxHeight = contentDiameter.toInt()
            )
        )

        layout(sizeLayout, sizeLayout) {
            val x = (sizeLayout - placeableItem.width) / 2
            val y = (sizeLayout - placeableItem.height) / 2
            placeableItem.place(x = x, y = y)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RadialSliderPreview() {
    RadialSlider(
        modifier = Modifier.size(300.dp),
        progress = {  0f },
        dashWidth = 2.dp
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "HERE"
            )
        }
    }
}