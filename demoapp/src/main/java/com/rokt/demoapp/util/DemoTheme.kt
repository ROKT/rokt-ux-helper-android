package com.rokt.demoapp.util

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

private val RoktPink = Color(0xFFB51E6D)
private val RoktGrey = Color(0xFF292422)

private val DemoColorScheme = lightColorScheme(
    primary = RoktPink,
    onPrimary = Color.White,
    background = Color.White,
    surface = Color.White,
    onBackground = RoktGrey,
    onSurface = RoktGrey,
)

@Composable
fun DemoTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = DemoColorScheme, content = content)
}

/** Pill-shaped filled button, matching `ButtonDefaultOutlined` in the iOS sample. */
@Composable
fun DemoButton(text: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(percent = 50),
        colors = ButtonDefaults.buttonColors(containerColor = RoktPink, contentColor = Color.White),
    ) {
        Text(text = text, style = MaterialTheme.typography.labelLarge)
    }
}
