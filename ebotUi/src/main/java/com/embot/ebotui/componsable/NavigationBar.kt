package com.embot.ebotui.componsable

import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Abc
import androidx.compose.material.icons.outlined.Adb
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.CacheDrawScope
import androidx.compose.ui.draw.DrawResult
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

internal const val DEFAULT_MARGIN_CURVE = 60F
internal const val DEFAULT_MARGIN_ITEM = 10F
internal const val DEFAULT_NOTCH_ARC_ANGLE = -180f
internal val DEFAULT_NOTCH_WIDTH = 30.dp
internal val DEFAULT_LAYOUT_HEIGHT = 75.dp

private val NavItemWindowInsets = WindowInsets().only(
    WindowInsetsSides.Horizontal + WindowInsetsSides.Bottom
)

internal data class ItemData(
    val width: Int,
    val height: Int,
    val offset: Offset
)

@Immutable
class NavigationBarColor(
    val container: Color,
)

@Immutable
class ItemColor(
    val indicatorColor: Color,
)

object NavigationBarItemDefaults {

    @Composable
    fun colors(
        indicatorColor: Color = Color.Unspecified,
    ): ItemColor {
        return ItemColor(
            indicatorColor = indicatorColor,
        )
    }

}

object NavigationDefaults {

    @Composable
    fun colors(
        container: Color = Color.White,
    ): NavigationBarColor {
        return NavigationBarColor(
            container = container
        )
    }

}

internal data class DrawingMetaData(
    val centerNotchX: Int,
    val totalWidth: Float,
    val totalHeight: Float,
    val notchWidth: Float,
)

private fun CacheDrawScope.getDrawingMetaData(
    centerNotchX: Int
): DrawingMetaData {
    return DrawingMetaData(
        centerNotchX = centerNotchX,
        totalWidth = this.size.width,
        totalHeight = this.size.height,
        notchWidth = DEFAULT_NOTCH_WIDTH.toPx()
    )
}


private fun CacheDrawScope.drawBackgroundNotch(
    drawingMetaData: DrawingMetaData,
    positions: List<ItemData>,
    colors: NavigationBarColor,
): DrawResult {
    val containerColor = colors.container.toArgb()
    val centerNotchX = drawingMetaData.centerNotchX
    val totalWidth = drawingMetaData.totalWidth
    val totalHeight = drawingMetaData.totalHeight
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

    val currentCenterX = positions[centerNotchX].offset.x
    val minHeight = 0f
    val notchHeight = this.size.height - this.size.height.times(0.3f) + DEFAULT_MARGIN_ITEM

    val background = Path().apply {
        this.moveTo(minHeight, minHeight)
        this.lineTo(currentCenterX - notchWidth - DEFAULT_MARGIN_CURVE, minHeight)
        this.quadTo(
            currentCenterX - notchWidth,
            minHeight,
            currentCenterX - notchWidth,
            DEFAULT_MARGIN_CURVE,
        )
        this.arcTo(
            RectF(
                currentCenterX - notchWidth,
                minHeight,
                currentCenterX + notchWidth,
                notchHeight,
            ),
            DEFAULT_NOTCH_ARC_ANGLE,
            DEFAULT_NOTCH_ARC_ANGLE,
            false
        )
        this.lineTo(currentCenterX + notchWidth, DEFAULT_MARGIN_CURVE)
        this.quadTo(
            currentCenterX + notchWidth,
            minHeight,
            currentCenterX + notchWidth + DEFAULT_MARGIN_CURVE,
            minHeight,
        )
        this.lineTo(totalWidth - minHeight, minHeight)
        this.lineTo(
            totalWidth - minHeight,
            totalHeight - minHeight
        )
        this.lineTo(minHeight, totalHeight - minHeight)
        this.close()
    }
    return onDrawBehind {
        drawIntoCanvas { canvas ->
            val nativeCanvas = canvas.nativeCanvas
            nativeCanvas.drawPath(
                background,
                pathPaint
            )
        }
    }
}

@Composable
fun NavigationItem(
    index: Int,
    selectedIndexState: MutableIntState,
    colors: ItemColor = NavigationBarItemDefaults.colors(),
    windowInsets: WindowInsets = NavItemWindowInsets,
    icon: @Composable () -> Unit,
    onItemClicked: (index: Int) -> Unit
) {
    val selected by remember { derivedStateOf { selectedIndexState.intValue == index } }
    val indicatorWidth = (DEFAULT_NOTCH_WIDTH.value * 1.9f - DEFAULT_MARGIN_ITEM).dp
    val height = DEFAULT_LAYOUT_HEIGHT + windowInsets.asPaddingValues().calculateBottomPadding()
    Column(
        modifier = Modifier.height(height + height.times(0.04f))
            .padding(top = height.times(0.08f))
            .clickable(
                enabled = true,
                indication = null,
                interactionSource = null,
                onClick = { onItemClicked(index) }
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Column(
            modifier = Modifier.size(indicatorWidth)
                .background(
                    color = if (selected) colors.indicatorColor else Color.Transparent,
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
    selectedIndexState: MutableIntState,
    colors: NavigationBarColor = NavigationDefaults.colors(),
    content: @Composable () -> Unit,
) {
    var centerNotchX by selectedIndexState
    val positions = remember { mutableStateListOf<ItemData>() }

    Layout(
        modifier = modifier
            .graphicsLayer { compositingStrategy = CompositingStrategy.Auto }
            .drawWithCache {
                val drawingMetaData = getDrawingMetaData(centerNotchX = centerNotchX)
                drawBackgroundNotch(
                    drawingMetaData = drawingMetaData,
                    positions = positions,
                    colors = colors
                )
            },
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
            if (positions.size != itemsToPlace.size) {
                positions.clear()
                repeat(itemsToPlace.size) { positions.add(ItemData(0, 0, Offset.Zero)) }
            }
            itemsToPlace.forEachIndexed { index, placeable ->
                placeable.placeRelative(x, 0)
                x += placeable.width
                positions[index] = ItemData(
                    width = placeable.width,
                    height = placeable.height,
                    offset = Offset(x - placeable.width / 2f, placeable.height / 2f)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun NavigationBarPreview() {
    val selectedIndexState = remember { mutableIntStateOf(1) }
    NavigationBar(
        modifier = Modifier.fillMaxWidth(),
        selectedIndexState = selectedIndexState,
        colors = NavigationDefaults.colors(
            container = Color(0xFF48230d)
        ),
    ) {
        NavigationItem(
            index = 0,
            selectedIndexState = selectedIndexState,
            colors = NavigationBarItemDefaults.colors(
                indicatorColor = Color(0xFFf5b22d)
            ),
            onItemClicked = {},
            icon = {
                Icon(
                    modifier = Modifier.size(24.dp),
                    imageVector = Icons.Outlined.Abc,
                    contentDescription = null
                )
            }
        )
        NavigationItem(
            index = 1,
            selectedIndexState = selectedIndexState,
            colors = NavigationBarItemDefaults.colors(
                indicatorColor = Color(0xFFf5b22d)
            ),
            onItemClicked = {},
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