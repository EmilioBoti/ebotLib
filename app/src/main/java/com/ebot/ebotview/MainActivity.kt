package com.ebot.ebotview

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AcUnit
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ebot.ebotview.ui.theme.BlueNavy
import com.ebot.ebotview.ui.theme.EbotLibTheme
import com.embot.ebotui.componsable.SliderRing
import com.embot.ebotui.componsable.SliderStyleDefault

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EbotLibTheme {
                Scaffold { paddingValues ->
                    MainContent(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                    )
                }
            }
        }
    }
}

@Composable
fun MainContent(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        SliderRing(
            modifier = Modifier.size(350.dp),
            progress = 0f,
            trackWidth = 10.dp,
            colors = SliderStyleDefault.sliderColor(
                progressColor = BlueNavy
            ),
            onChange = { progress ->
                Log.i("PROGRESS", "$progress")
            }
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    modifier = Modifier.size(32.dp),
                    imageVector = Icons.Outlined.AcUnit,
                    contentDescription = null
                )
                Row {
                    Text(
                        text = "20.0",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontSize = 42.sp,
                            fontWeight = FontWeight.Light
                        )
                    )
                    Text(
                        text = "ºC",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Light
                        )
                    )
                }
                Text(
                    text = "Manual",
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainContentPreview() {
    MainContent()
}