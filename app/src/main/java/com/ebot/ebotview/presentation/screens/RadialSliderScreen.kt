package com.ebot.ebotview.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ebot.ebotview.ui.theme.AzureBlue
import com.ebot.ui.slider.RadialSlider
import com.ebot.ui.slider.SliderStyleDefault


@Composable
fun RadialSliderScreen() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        var progress by remember { mutableFloatStateOf(30f) }

        RadialSlider(
            modifier = Modifier.size(300.dp),
            progress = progress,
            trackWidth = 12.dp,
            dashWidth = 1.9.dp,
            colors = SliderStyleDefault.sliderColor(
                progressColor = AzureBlue
            ),
            onChange = { value ->
                progress = value
            }
        ) {
            // YOUR CONTENT
            Text(
                text = "${progress.toInt()}%",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontSize = 42.sp
                )
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RadialSliderScreenPreview() {
    RadialSliderScreen()
}