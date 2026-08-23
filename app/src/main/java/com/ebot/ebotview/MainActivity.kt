package com.ebot.ebotview

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Adb
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.PersonOutline
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ebot.ebotview.ui.theme.EbotLibTheme
import com.embot.ebotui.componsable.NavigationBar
import com.embot.ebotui.componsable.NavigationBarItemDefaults
import com.embot.ebotui.componsable.NavigationDefaults
import com.embot.ebotui.componsable.NavigationItem

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EbotLibTheme {
                MainContent(
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

data class NavItem(
    val index: Int,
    val label: String,
    val icon: ImageVector,
    val selected: Boolean = false
)

@Preview(showBackground = true)
@Composable
fun MainContent(modifier: Modifier = Modifier) {
    val indexSelectedState = remember { mutableIntStateOf(0) }
    val items = remember {
        listOf(
            NavItem(
                index = 0,
                label = "Home",
                icon = Icons.Outlined.Home,
                selected = false
            ),
            NavItem(
                index = 1,
                label = "Home",
                icon = Icons.Outlined.Search,
                selected = false
            ),
            NavItem(
                index = 2,
                label = "Home",
                icon = Icons.Outlined.Adb,
                selected = false
            ),
            NavItem(
                index = 3,
                label = "Home",
                icon = Icons.Outlined.Notifications,
                selected = false
            ),
            NavItem(
                index = 4,
                label = "Home",
                icon = Icons.Outlined.PersonOutline,
                selected = false
            )
        )
    }

    Scaffold(
        bottomBar = {
            NavigationBar(
                modifier = Modifier.fillMaxWidth(),
                indexSelectedState = indexSelectedState,
                colors = NavigationDefaults.colors(
                    container = Color(0xFF48230d)
                ),
            ) {
                items.forEach { item ->
                    NavigationItem(
                        selected = item.index == indexSelectedState.intValue,
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = Color(0xFFf5b22d)
                        ),
                        onItemClicked = {
                            indexSelectedState.intValue = item.index
                        },
                        icon = {
                            Icon(
                                modifier = Modifier.size(24.dp),
                                imageVector = item.icon,
                                contentDescription = null,
                                tint = Color(0xFF826454)
                            )
                        }
                    )
                }
            }

        }
    ) { paddingValues ->
        Column(
            modifier = modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
        }
    }
}