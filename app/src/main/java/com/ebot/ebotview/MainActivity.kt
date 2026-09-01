package com.ebot.ebotview

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ebot.ebotview.presentation.screens.ScreenFive
import com.ebot.ebotview.presentation.screens.ScreenFour
import com.ebot.ebotview.presentation.screens.ScreenOne
import com.ebot.ebotview.presentation.screens.ScreenThree
import com.ebot.ebotview.presentation.screens.RadialSliderScreen
import com.ebot.ebotview.ui.theme.EbotLibTheme
import com.ebot.ui.navigation.NavigationBar
import com.ebot.ui.navigation.NavigationDefaults
import com.ebot.ui.navigation.NavigationItem

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


sealed interface IdentifierMenu {
    data object Item1: IdentifierMenu
    data object Item2: IdentifierMenu
    data object Item3: IdentifierMenu
    data object Item4: IdentifierMenu
    data object Item5: IdentifierMenu
}
data class NavItem(
    val index: Int,
    val label: String,
    val icon: ImageVector,
    val identifier: IdentifierMenu
)


val items = listOf(
    NavItem(
        index = 0,
        label = "Home",
        icon = Icons.Outlined.Home,
        identifier = IdentifierMenu.Item1
    ),
    NavItem(
        index = 1,
        label = "Home",
        icon = Icons.Outlined.Search,
        identifier = IdentifierMenu.Item2
    ),
    NavItem(
        index = 2,
        label = "Home",
        icon = Icons.Outlined.Adb,
        identifier = IdentifierMenu.Item3
    ),
    NavItem(
        index = 3,
        label = "Home",
        icon = Icons.Outlined.Notifications,
        identifier = IdentifierMenu.Item4
    ),
    NavItem(
        index = 4,
        label = "Home",
        icon = Icons.Outlined.PersonOutline,
        identifier = IdentifierMenu.Item5
    )
)

@Preview(showBackground = true)
@Composable
fun MainContent(modifier: Modifier = Modifier) {
    var indexSelectedState by remember { mutableStateOf<IdentifierMenu>(IdentifierMenu.Item1) }

    Scaffold(
        bottomBar = {
            NavigationBar(
                modifier = Modifier.fillMaxWidth(),
                animationDuration = 600,
                colors = NavigationDefaults.colors(
                    container = Color(0xFF48230d),
                    indicatorColor = Color(0xFFf5b22d)
                ),
            ) {
                items.forEach { item ->
                    NavigationItem(
                        index = item.index,
                        isSelected = indexSelectedState == item.identifier,
                        onItemClicked = {
                            indexSelectedState = item.identifier
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
        when(indexSelectedState) {
            IdentifierMenu.Item1 -> ScreenOne(modifier = Modifier.background(Color.Red))
            IdentifierMenu.Item2 -> RadialSliderScreen()
            IdentifierMenu.Item3 -> ScreenThree(modifier = Modifier.background(Color.Green))
            IdentifierMenu.Item4 -> ScreenFour(modifier = Modifier.background(Color.Magenta))
            IdentifierMenu.Item5 -> ScreenFive(modifier = Modifier.background(Color.Cyan))
        }
    }
}