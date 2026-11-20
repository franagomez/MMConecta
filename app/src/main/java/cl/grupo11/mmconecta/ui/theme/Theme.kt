package cl.grupo11.mmconecta.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Paleta provisional; no representa una identidad corporativa autorizada.
private val LightColors = lightColorScheme(primary = Color(0xFF6B4E9B),
    secondary = Color(0xFF7A6A8F), background = Color(0xFFFAF8FC))
private val DarkColors = darkColorScheme(primary = Color(0xFFCBB3EA),
    secondary = Color(0xFFD8C9E8))

@Composable
fun MMConectaTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = if (isSystemInDarkTheme()) DarkColors else LightColors,
        content = content)
}
