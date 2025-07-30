package com.example.minisofascore.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.example.minisofascore.util.appTypography

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF374DF5),
    onPrimary = Color.White,
    background = Color.White,
    surface = Color.White,
    onBackground = Color(0xFF121212),
    surfaceContainer = Color(0xFF121212),
    onSurface = Color.Black,
    outline = Color(0xFFC0CFE4),
    outlineVariant = Color(0xFFE93030),
    secondary = Color(0xFFF7F6EF),
    onSecondary = Color.White,
    onPrimaryContainer = Color(0xFFEFF3F8),
    surfaceTint = Color(0xFF1DA86D),
    surfaceVariant = Color(0xFF1327BA)
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF2A2A2A),
    onPrimary = Color(0xFF121212),
    background = Color(0xFF121212),
    surface = Color(0xFF1E1E1E),
    onBackground = Color(0xFFF7F6EF),
    surfaceContainer = Color(0xFF121212),
    onSurface = Color.White,
    outline = Color(0xFF121212),
    outlineVariant = Color(0xFFE93030),
    secondary = Color(0xFF2A2A2A),
    onSecondary = Color(0xFF2A2A2A),
    onPrimaryContainer = Color(0xFF2C3AA8),
    surfaceTint = Color(0xFF1DA86D),
    surfaceVariant = Color(0xFF2D2D2D)
)


@Composable
fun MiniSofascoreTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
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
        typography = appTypography,
        content = content
    )
}