package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = AccentBlue,
    onPrimary = CoolWhite,
    secondary = NeonViolet,
    onSecondary = CoolWhite,
    tertiary = EmeraldMint,
    background = DarkSlate,
    onBackground = CoolWhite,
    surface = DeepCard,
    onSurface = CoolWhite,
    surfaceVariant = DeepCardElevated,
    onSurfaceVariant = OffWhite,
    error = RoseRed,
    onError = CoolWhite
)

private val LightColorScheme = lightColorScheme(
    primary = AccentBlue,
    onPrimary = CoolWhite,
    secondary = NeonViolet,
    onSecondary = CoolWhite,
    tertiary = EmeraldMint,
    background = CoolWhite,
    onBackground = DarkSlate,
    surface = OffWhite,
    onSurface = DarkSlate,
    surfaceVariant = CoolWhite,
    onSurfaceVariant = SlateGray,
    error = RoseRed,
    onError = CoolWhite
)

@Composable
fun MomentumTheme(
    darkTheme: Boolean = true, // Force Dark by default for that premium SaaS feel
    dynamicColor: Boolean = false, // Disable dynamic colors to keep brand identity precise
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
