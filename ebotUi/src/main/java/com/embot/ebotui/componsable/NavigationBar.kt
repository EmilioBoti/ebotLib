package com.embot.ebotui.componsable

import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Abc
import androidx.compose.material.icons.outlined.Adb
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

internal data class ItemData(
    val width: Int,
    val height: Int,
    val offset: Offset
)

@Composable
fun NavigationItem(
    selected: Boolean,
    notchWidth: Dp,
    icon: @Composable () -> Unit,
    onItemClicked: () -> Unit
) {
    val indicatorWidth = (notchWidth.value * 1.2f).dp
    Column(
        modifier = Modifier.clickable(
            enabled = true,
            indication = null,
            interactionSource = null,
            onClick = onItemClicked
        ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Column(
            modifier = Modifier.size(indicatorWidth)
                .background(
                    color = if (selected) Color(0xFFf5b22d) else Color.Transparent,
                    shape = CircleShape
                ),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            icon()
        }
    }
}

@Composable
fun NavigationBar(
    modifier: Modifier = Modifier,
    indexSelectedState: MutableIntState,
    notchWidth: Dp,
    content: @Composable () -> Unit
) {
    var centerNotchX by indexSelectedState
    val positions: ArrayList<ItemData> = ArrayList()

    Box(
        modifier = modifier.height(75.dp)
            .drawWithCache {
                val color = Color.White.toArgb()
                val pathPaint = Paint().apply {
                    this.color = color
                    this.isAntiAlias = true
                    this.strokeWidth = 8f
                    this.style = Paint.Style.FILL
                    this.setShadowLayer(
                        12f,
                        0f,
                        -1f,
                        android.graphics.Color.argb(80, 0, 0, 0)
                    )
                }

                val poX = positions[centerNotchX]
                val marginXCurve = 60f
                val marginYCurve = 60f
                val minHeight = 0f
                val width = notchWidth.toPx()
                val notchHeight = this.size.height - this.size.height.times(0.14f)

                val background = Path().apply {
                    this.moveTo(minHeight, minHeight)
                    this.lineTo(poX.offset.x - width - marginXCurve, minHeight)
                    this.quadTo(
                        poX.offset.x - width,
                        minHeight,
                        poX.offset.x - width,
                        marginYCurve,
                    )
                    this.arcTo(
                        RectF(
                            poX.offset.x - width,
                            minHeight,
                            poX.offset.x + width,
                            notchHeight,
                        ),
                        -180f,
                        -180f,
                        false
                    )
                    this.lineTo(poX.offset.x + width, marginYCurve)
                    this.quadTo(
                        poX.offset.x + width,
                        minHeight,
                        poX.offset.x + width + marginXCurve,
                        minHeight,
                    )
                    this.lineTo(this@drawWithCache.size.width - minHeight, minHeight)
                    this.lineTo(
                        this@drawWithCache.size.width - minHeight,
                        this@drawWithCache.size.height - minHeight
                    )
                    this.lineTo(minHeight, this@drawWithCache.size.height - minHeight)
                    this.close()
                }
                onDrawBehind {
                    drawIntoCanvas { canvas ->
                        val nativeCanvas = canvas.nativeCanvas
                        nativeCanvas.drawPath(
                            background,
                            pathPaint
                        )
                    }
                }
            },
        contentAlignment = Alignment.Center
    ) {
        Layout(
            content = content
        ) { measurables, constraints ->
            val height = measurables.maxOf { it.minIntrinsicHeight(constraints.maxHeight) }
            val widths = measurables.map { item -> item.maxIntrinsicWidth(constraints.maxWidth) }
            val totalWidth = widths.sum()

            var itemsToPlace: List<Placeable>

            if (totalWidth > constraints.maxWidth) {
                val itemWidth = constraints.maxWidth / measurables.size
                val itemConstraints = constraints.copy(minWidth = itemWidth, maxWidth = itemWidth)
                itemsToPlace = measurables.map { it.measure(itemConstraints) }
            } else {
                val availableSpace = (constraints.maxWidth - totalWidth) / measurables.size
                itemsToPlace = measurables.mapIndexed { index, measurable ->
                    val itemWidth = widths[index] + availableSpace
                    measurable.measure(constraints.copy(minWidth = itemWidth, maxWidth = itemWidth))
                }
            }

            layout(constraints.maxWidth, height) {
                var x = 0
                itemsToPlace.forEach { placeable ->
                    placeable.placeRelative(x, 0)
                    x += placeable.width
                    positions.add(
                        ItemData(
                            width = placeable.width,
                            height = placeable.height,
                            offset = Offset(
                                x = x - placeable.width / 2f,
                                y = placeable.height / 2f
                            )
                        )
                    )
                }
            }
        }

    }
}

@Preview(showBackground = true)
@Composable
fun NavigationBarPreview() {
    val notchWidth = 30.dp
    NavigationBar(
        modifier = Modifier.fillMaxWidth(),
        notchWidth = notchWidth,
        indexSelectedState = remember { mutableIntStateOf(1) }
    ) {
        NavigationItem(
            selected = false,
            onItemClicked = {},
            notchWidth = notchWidth,
            icon = {
                Icon(
                    modifier = Modifier.size(24.dp),
                    imageVector = Icons.Outlined.Abc,
                    contentDescription = null
                )
            }
        )
        NavigationItem(
            selected = true,
            onItemClicked = {},
            notchWidth = notchWidth,
            icon = {
                Icon(
                    modifier = Modifier.size(24.dp),
                    imageVector = Icons.Outlined.Adb,
                    contentDescription = null
                )
            }
        )
    }
}