package com.owl.minerva.fbu.app.view.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme: ColorScheme = darkColorScheme(
    background = BackgroundColor,
    surface = BackgroundColor,
    surfaceVariant = SurfaceColor,
    primary = PrimaryColor,
    secondary = SecondaryColor,
    onPrimary = BackgroundColor,
    onSecondary = BackgroundColor
)

@Composable
fun FBUTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = AppTypography,
        content = content
    )
}
