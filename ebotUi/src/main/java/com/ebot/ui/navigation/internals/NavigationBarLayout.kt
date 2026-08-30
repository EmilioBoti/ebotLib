package com.ebot.ui.navigation.internals

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.unit.Constraints
import com.ebot.ui.navigation.DEFAULT_MARGIN_CURVE
import com.ebot.ui.navigation.DEFAULT_MARGIN_ITEM
import com.ebot.ui.navigation.DEFAULT_NOTCH_WIDTH
import com.ebot.ui.navigation.NavigationBarColor
import com.ebot.ui.navigation.NavigationDefaults
import com.ebot.ui.navigation.internals.state.ItemPlacement
import com.ebot.ui.navigation.internals.state.NavigationBarMetric


@Stable
interface NavigationBarScope {
    fun updatedSelectedItem(index: Int)
    fun onClick(index: Int)
}

internal class NavigationBarScopeInstance(
    val setSelectedItem: (Int) -> Unit,
    val onItemClicked: (Int) -> Unit,
): NavigationBarScope {

    override fun updatedSelectedItem(index: Int) { setSelectedItem(index) }
    override fun onClick(index: Int) { onItemClicked(index) }
}

@Composable
internal fun NavigationBarLayout(
    modifier: Modifier = Modifier,
    colors: NavigationBarColor = NavigationDefaults.colors(),
    animationDuration: Int,
    content: @Composable NavigationBarScope.() -> Unit
) {
    var selectedIndex by remember { mutableIntStateOf(0) }
    val animatedOffsetX = remember { Animatable(0f) }
    var centerNotchY by remember { mutableFloatStateOf(0f) }
    val positions: ArrayList<ItemPlacement> = arrayListOf()

    LaunchedEffect(Unit) {
        snapshotFlow { selectedIndex }
            .collect { index ->
                val target = positions.getOrNull(index) ?: return@collect
                centerNotchY = target.offset.y
                animatedOffsetX.animateTo(
                    targetValue = target.offset.x,
                    animationSpec = tween(
                        durationMillis = animationDuration,
                        easing = FastOutSlowInEasing
                    )
                )
            }
    }

    Layout(
        modifier = modifier
            .graphicsLayer { compositingStrategy = CompositingStrategy.Offscreen }
            .drawWithCache {
                val drawingMetaData = NavigationBarMetric(
                    centerNotchX = animatedOffsetX.value,
                    centerNotchY = centerNotchY,
                    totalWidth = this.size.width,
                    totalHeight = this.size.height,
                    notchWidth = DEFAULT_NOTCH_WIDTH.toPx(),
                    itemMargin = DEFAULT_MARGIN_ITEM,
                    curve = DEFAULT_MARGIN_CURVE
                )
                drawBackgroundNotch(
                    drawingMetaData = drawingMetaData,
                    colors = colors
                )
            },
        content = {
            NavigationBarScopeInstance(
                setSelectedItem = { selectedIndex = it },
                onItemClicked = { selectedIndex = it }
            ).content()
        }
    ) { measurables: List<Measurable>, constraints: Constraints ->
        val height = measurables.maxOf { it.minIntrinsicHeight(constraints.maxHeight) }
        val widths = measurables.map { item -> item.maxIntrinsicWidth(constraints.maxWidth) }
        val totalWidth = widths.sum()

        val itemsToPlace: List<Placeable> = calculateAvailableSpace(
            measurablesSize = measurables.size,
            totalWidth = totalWidth,
            constraintsMaxWidth = constraints.maxWidth
        ).let { space ->
            if (totalWidth > constraints.maxWidth) {
                measurables.map { it.measure(constraints.copy(minWidth = space, maxWidth = space)) }
            } else {
                measurables.mapIndexed { index, measurable ->
                    val itemWidth = widths[index] + space
                    measurable.measure(constraints.copy(minWidth = itemWidth, maxWidth = itemWidth))
                }
            }
        }

        layout(constraints.maxWidth, height) {
            var x = 0
            if (positions.size != itemsToPlace.size) {
                positions.clear()
                repeat(itemsToPlace.size) { positions.add(ItemPlacement(0, 0, Offset.Zero)) }
            }
            itemsToPlace.forEachIndexed { index: Int, placeable: Placeable ->
                placeable.placeRelative(x, 0)
                x += placeable.width
                positions[index] = ItemPlacement(
                    width = placeable.width,
                    height = placeable.height,
                    offset = Offset(x - placeable.width / 2f, placeable.height / 2f)
                )
            }
        }
    }
}