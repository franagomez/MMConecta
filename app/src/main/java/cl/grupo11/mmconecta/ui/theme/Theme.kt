package cl.grupo11.mmconecta.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Paleta provisional; no representa una identidad corporativa autorizada.
private val LightColors = lightColorScheme(primary = Color(0xFF7A263A),
    secondary = Color(0xFF725B40), background = Color(0xFFFFF8F5))
private val DarkColors = darkColorScheme(primary = Color(0xFFFFB1C0),
    secondary = Color(0xFFE0C2A0))

private val AppShapes = Shapes(
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(20.dp),
    large = RoundedCornerShape(28.dp)
)

private val baseTypography = Typography()
private val AppTypography = baseTypography.copy(
    headlineLarge = baseTypography.headlineLarge.copy(fontWeight = FontWeight.ExtraBold),
    headlineSmall = baseTypography.headlineSmall.copy(fontWeight = FontWeight.Bold),
    titleLarge = baseTypography.titleLarge.copy(fontWeight = FontWeight.Bold),
    titleMedium = baseTypography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
    labelLarge = baseTypography.labelLarge.copy(fontWeight = FontWeight.Bold, letterSpacing = 0.8.sp)
)

@Composable
fun MMConectaTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = if (isSystemInDarkTheme()) DarkColors else LightColors,
        shapes = AppShapes,
        typography = AppTypography,
        content = content
    )
}
