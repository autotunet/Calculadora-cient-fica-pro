package com.example.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val ProfessionalDarkColorScheme = darkColorScheme(
    primary = ElectricCyan,
    onPrimary = DeepObsidian,
    primaryContainer = DarkSurfaceElevated,
    onPrimaryContainer = ElectricCyan,
    secondary = NeonSky,
    onSecondary = DeepObsidian,
    secondaryContainer = KeypadOperator,
    onSecondaryContainer = NeonSky,
    tertiary = AccentPurple,
    onTertiary = DeepObsidian,
    background = DeepObsidian,
    onBackground = TextPrimary,
    surface = DarkSurface,
    onSurface = TextPrimary,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = TextSecondary,
    outline = BorderSubtle,
    outlineVariant = BorderActive,
    error = DangerRed,
    onError = TextPrimary
)

@Composable
fun MyApplicationTheme(
    content: @Composable () -> Unit
) {
    val colorScheme = ProfessionalDarkColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            window?.let {
                val insetsController = WindowCompat.getInsetsController(it, view)
                insetsController.isAppearanceLightStatusBars = false
                insetsController.isAppearanceLightNavigationBars = false
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = CalculatorTypography,
        content = content
    )
}
