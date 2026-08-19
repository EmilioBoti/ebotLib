package com.embot.ebotui.componsable

import android.graphics.Paint
import android.graphics.Path
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.center
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.drawscope.inset
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.center
import androidx.compose.ui.unit.dp
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin


@Immutable
class SliderColor(
    val progressColor: Color,
    val trackColor: Color,
    val ringColor: Color,
    val dashColor: Color,
)

object SliderStyleDefault {

    @Composable
    fun sliderColor(
        progressColor: Color = Color.Blue,
        trackColor: Color = Color.LightGray,
        ringColor: Color = Color.White,
        dashColor: Color = Color.LightGray,
    ): SliderColor {
        return SliderColor(
            progressColor = progressColor,
            trackColor = trackColor,
            ringColor = ringColor,
            dashColor = dashColor
        )
    }
}

internal const val START_ANGLE = 135F
internal const val END_ANGLE = 270F
internal const val DASH_SIZE = 45
internal const val START_DASH_POS = 90.0

fun Offset.calculateProgress(
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

    val reAdjustedAngle = if (isInRange) adjustedAngle - 360f
    else adjustedAngle

    if (reAdjustedAngle in START_ANGLE..START_ANGLE.plus(END_ANGLE)) {
        current = (((reAdjustedAngle - START_ANGLE) / END_ANGLE) * 100f).coerceIn(0f, 100f)
    }
    return current
}

@Composable
fun SliderRing(
    modifier: Modifier = Modifier,
    progress: Float = 0f,
    trackWidth: Dp = 8.dp,
    dashWidth: Dp = 2.dp,
    colors: SliderColor = SliderStyleDefault.sliderColor(),
    onChange: (progress: Float) -> Unit = {},
    content: @Composable () -> Unit
) {
    var progress by remember { mutableFloatStateOf(progress) }
    var centerX by remember { mutableFloatStateOf(0f) }
    var centerY by remember { mutableFloatStateOf(0f) }

    BoxWithConstraints(
        modifier = modifier
    ) {
        val margin = 10.dp

        val fullRadius = maxWidth / 2f
        val insetHalf = fullRadius - margin

        val innerDashRadius = fullRadius - insetHalf.times(0.12f) - insetHalf.times(0.47f)
        val contentDiameter = innerDashRadius.times(2f)

        val dashesPositions: ArrayList<Pair<Offset, Offset>> = arrayListOf()

        Box(
            modifier = Modifier
                .matchParentSize()
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDrag = { change, _ ->
                            progress = change.position.calculateProgress(progress, centerX, centerY)
                            onChange(progress)
                        }
                    )
                }
                .onSizeChanged { size ->
                    centerX = size.center.x.toFloat()
                    centerY = size.center.y.toFloat()
                }
                .drawWithCache {
                    val dashPaint = Paint().apply {
                        this.color = colors.dashColor.toArgb()
                        this.style = Paint.Style.STROKE
                        this.strokeWidth = dashWidth.toPx()
                        this.strokeCap = Paint.Cap.ROUND
                    }

                    val ringPaint = Paint().apply {
                        color = colors.ringColor.toArgb()
                        isAntiAlias = true
                        setShadowLayer(
                            12f,
                            0f,
                            2f,
                            android.graphics.Color.argb(80, 0, 0, 0)
                        )
                    }
                    val stroke = Stroke(
                        width = trackWidth.toPx(),
                        cap = StrokeCap.Round
                    )

                    // CALCULATE DASH LINES POSITIONS
                    dashesPositions.clear()
                    val startPoint = Math.toRadians(START_DASH_POS)
                    val radius = centerX - this.size.center.x.times(.12f) - margin.toPx() - 8.dp.toPx()
                    val radius2 = centerX - this.size.center.x.times(.12f) - this.size.center.x.times(.25f)

                    for (i in startPoint.toInt() until DASH_SIZE) {
                        dashesPositions.add(
                            Pair(
                                Offset(
                                    this.size.center.x + (radius * cos(startPoint + i).toFloat()),
                                    this.size.center.y + (radius * sin(startPoint + i).toFloat()),
                                ),
                                Offset(
                                    this.size.center.x + (radius2 * cos(startPoint + i).toFloat()),
                                    this.size.center.y + (radius2 * sin(startPoint + i).toFloat()),
                                )
                            )
                        )
                    }

                    // DRAW DASH LINES
                    val dashesLines = Path().apply {
                        dashesPositions.forEach {
                            this.moveTo(
                                it.first.x - margin.toPx(),
                                it.first.y - margin.toPx(),
                            )
                            this.lineTo(
                                it.second.x - margin.toPx(),
                                it.second.y - margin.toPx(),
                            )
                        }
                        this.close()
                    }

                    onDrawBehind {
                        drawIntoCanvas { canvas ->
                            inset(margin.toPx()) {
                                // DRAW RING
                                canvas.nativeCanvas.drawCircle(
                                    centerX - margin.toPx(),
                                    centerY - margin.toPx(),
                                    this.size.center.x - margin.toPx() - 6.dp.toPx(),
                                    ringPaint
                                )
                                // DRAW TRACK
                                drawArc(
                                    color = colors.trackColor,
                                    startAngle = START_ANGLE,
                                    sweepAngle = END_ANGLE,
                                    useCenter = false,
                                    style = stroke
                                )
                                // DRAW PROGRESS TRACK
                                val progressAngle = (progress / 100f) * END_ANGLE // convert progress range (0 to 100) to angle (135 -  270)
                                drawArc(
                                    color = colors.progressColor,
                                    startAngle = START_ANGLE,
                                    sweepAngle = progressAngle,
                                    useCenter = false,
                                    style = stroke
                                )
                                // DRAW DASH LINES
                                canvas.nativeCanvas.drawPath(
                                    dashesLines,
                                    dashPaint
                                )
                            }
                        }
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier.size(contentDiameter),
                contentAlignment = Alignment.Center
            ) {
                content()
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SliderRingPreview() {
    SliderRing(
        modifier = Modifier.size(300.dp)
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