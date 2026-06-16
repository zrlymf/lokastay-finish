package com.example.lokastay.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = MainCyan,
    onPrimary = Neutral10,
    primaryContainer = PressedCyan,
    onPrimaryContainer = SurfaceCyan,

    secondary = MainYellow,
    onSecondary = Neutral100,
    secondaryContainer = PressedYellow,
    onSecondaryContainer = SurfaceYellow,

    tertiary = MainGreen,
    onTertiary = Neutral10,
    tertiaryContainer = PressedGreen,
    onTertiaryContainer = SurfaceGreen,

    error = MainRed,
    onError = Neutral10,
    errorContainer = PressedRed,
    onErrorContainer = SurfaceRed,

    background = Neutral100,
    onBackground = Neutral10,
    surface = Neutral100,
    onSurface = Neutral10,
    surfaceVariant = Neutral90,
    onSurfaceVariant = Neutral40,
    outline = Neutral60,
    outlineVariant = Neutral80
)

private val LightColorScheme = lightColorScheme(
    primary = MainCyan,
    onPrimary = Neutral10,
    primaryContainer = SurfaceCyan,
    onPrimaryContainer = PressedCyan,

    secondary = MainYellow,
    onSecondary = Neutral100,
    secondaryContainer = SurfaceYellow,
    onSecondaryContainer = PressedYellow,

    tertiary = MainGreen,
    onTertiary = Neutral10,
    tertiaryContainer = SurfaceGreen,
    onTertiaryContainer = PressedGreen,

    error = MainRed,
    onError = Neutral10,
    errorContainer = SurfaceRed,
    onErrorContainer = PressedRed,

    background = Neutral10,
    onBackground = Neutral100,
    surface = Neutral10,
    onSurface = Neutral100,
    surfaceVariant = Neutral20,
    onSurfaceVariant = Neutral70,
    outline = Neutral40,
    outlineVariant = Neutral30
)

@Composable
fun LokastayTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
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

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = android.graphics.Color.TRANSPARENT
            window.navigationBarColor = android.graphics.Color.TRANSPARENT

            WindowCompat.setDecorFitsSystemWindows(window, false)

            val insetsController = WindowCompat.getInsetsController(window, view)
            insetsController.isAppearanceLightStatusBars = !darkTheme
            insetsController.isAppearanceLightNavigationBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}