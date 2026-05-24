package com.example.myairecipe.ui.theme

import android.app.Activity
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

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF8B5000),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFFFDCBE),
    onPrimaryContainer = Color(0xFF2C1600),
    secondary = Color(0xFF735A42),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFFFDCBE),
    onSecondaryContainer = Color(0xFF2A1806),
    tertiary = Color(0xFF58633A),
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFDBE8B4),
    onTertiaryContainer = Color(0xFF161E01),
    error = Color(0xFFBA1A1A),
    onError = Color(0xFFFFFFFF),
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002),
    background = Color(0xFFFFFBFF),
    onBackground = Color(0xFF201A15),
    surface = Color(0xFFFFFBFF),
    onSurface = Color(0xFF201A15),
    surfaceVariant = Color(0xFFF2DFD1),
    onSurfaceVariant = Color(0xFF50453A),
    outline = Color(0xFF837468),
    outlineVariant = Color(0xFFD5C3B5)
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFFFB870),
    onPrimary = Color(0xFF4A2800),
    primaryContainer = Color(0xFF693C00),
    onPrimaryContainer = Color(0xFFFFDCBE),
    secondary = Color(0xFFE2C0A2),
    onSecondary = Color(0xFF412C18),
    secondaryContainer = Color(0xFF59422C),
    onSecondaryContainer = Color(0xFFFFDCBE),
    tertiary = Color(0xFFBFCB9A),
    onTertiary = Color(0xFF2A3410),
    tertiaryContainer = Color(0xFF414B24),
    onTertiaryContainer = Color(0xFFDBE8B4),
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),
    background = Color(0xFF201A15),
    onBackground = Color(0xFFEAE1D9),
    surface = Color(0xFF201A15),
    onSurface = Color(0xFFEAE1D9),
    surfaceVariant = Color(0xFF50453A),
    onSurfaceVariant = Color(0xFFD5C3B5),
    outline = Color(0xFF9E8E81),
    outlineVariant = Color(0xFF50453A)
)

@Composable
fun MyAIRecipeTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false, // Disabled to use our custom premium theme
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