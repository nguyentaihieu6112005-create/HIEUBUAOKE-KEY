package com.hieubuaoke.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

object HieuBuaokeColors {
    val Background = Color(0xFF050807)
    val Background2 = Color(0xFF07100B)
    val Card = Color(0xFF0D1B14)
    val Card2 = Color(0xFF10251A)
    val NeonGreen = Color(0xFF39FF88)
    val Cyan = Color(0xFF00E5FF)
    val TextPrimary = Color(0xFFE8FFF0)
    val TextSecondary = Color(0xFF8FA99A)
    val Warning = Color(0xFFFFD54A)
    val Error = Color(0xFFFF5C6C)
    val Divider = Color(0xFF1A372C)
}

private val DarkColors = darkColorScheme(
    primary = HieuBuaokeColors.NeonGreen,
    secondary = HieuBuaokeColors.Cyan,
    background = HieuBuaokeColors.Background,
    surface = HieuBuaokeColors.Card,
    onPrimary = Color.Black,
    onSecondary = Color.Black,
    onBackground = HieuBuaokeColors.TextPrimary,
    onSurface = HieuBuaokeColors.TextPrimary,
    error = HieuBuaokeColors.Error
)

@Composable
fun HieuBuaokeTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColors,
        content = content
    )
}
