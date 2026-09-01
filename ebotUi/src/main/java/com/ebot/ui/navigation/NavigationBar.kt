package com.ebot.ui.navigation

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Abc
import androidx.compose.material.icons.outlined.Adb
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ebot.ui.navigation.internals.NavigationBarLayout
import com.ebot.ui.navigation.internals.NavigationBarScope

internal const val DEFAULT_MARGIN_CURVE = 60F
internal const val DEFAULT_MARGIN_ITEM = 10F
internal const val DEFAULT_NOTCH_ARC_ANGLE = -180f
internal const val DEFAULT_EDGE = 0f
internal val DEFAULT_NOTCH_WIDTH = 30.dp
internal val DEFAULT_LAYOUT_HEIGHT = 75.dp

@Composable
fun NavigationBar(
    modifier: Modifier = Modifier,
    colors: NavigationBarColor = NavigationDefaults.colors(),
    animationDuration: Int = 600,
    content: @Composable NavigationBarScope.() -> Unit
) {
    NavigationBarLayout(
        modifier = modifier,
        colors = colors,
        animationDuration = animationDuration,
        content = content
    )
}

@Preview(showBackground = true)
@Composable
fun NavigationBarPreview() {
    NavigationBar(
        modifier = Modifier.fillMaxWidth(),
        colors = NavigationDefaults.colors(
            container = Color(0xFF48230d),
            indicatorColor = Color(0xFFf5b22d)
        ),
    ) {
        NavigationItem(
            index = 0,
            isSelected = false,
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
            isSelected = true,
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