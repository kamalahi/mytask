package com.example.mytasks.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

// A color scheme is the full set of colors every Material 3 component reads from,
// so you set them once here instead of coloring each button by hand.
private val LightColors = lightColorScheme(
    primary = Teal40,
    secondary = Sand40,
    tertiary = Amber40
)

private val DarkColors = darkColorScheme(
    primary = Teal80,
    secondary = Sand80,
    tertiary = Amber80
)

@Composable
fun MyTasksTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        content = content
    )
}
