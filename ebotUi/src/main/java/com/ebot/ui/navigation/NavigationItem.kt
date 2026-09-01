package com.ebot.ui.navigation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ebot.ui.navigation.internals.NavigationBarScope

internal val NavItemWindowInsets = WindowInsets().only(
    WindowInsetsSides.Horizontal + WindowInsetsSides.Bottom
)

@Composable
fun NavigationBarScope.NavigationItem(
    index: Int,
    isSelected: Boolean,
    windowInsets: WindowInsets = NavItemWindowInsets,
    onItemClicked: () -> Unit,
    icon: @Composable () -> Unit,
) {
    val indicatorWidth = (DEFAULT_NOTCH_WIDTH.value * 1.9f - DEFAULT_MARGIN_ITEM).dp
    val height = DEFAULT_LAYOUT_HEIGHT + windowInsets.asPaddingValues().calculateBottomPadding()

    /**
     * Notify the parent navigationBar the current selected item
     */
    if (isSelected) this.updatedSelectedItem(index)

    Column(
        modifier = Modifier.height(height + height.times(0.04f))
            .padding(top = height.times(0.08f))
            .clickable(
                enabled = true,
                indication = null,
                interactionSource = null,
                onClick = {
                    onItemClicked()
                    updatedSelectedItem(index)
                }
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Column(
            modifier = Modifier.size(indicatorWidth),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            icon()
        }
    }
}