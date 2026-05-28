package com.example.comiclibrary.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.example.comiclibrary.util.ThemeColor

@Composable
fun ComicLibraryTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    themeColor: ThemeColor = ThemeColor.PURPLE,
    content: @Composable () -> Unit
) {
    val a = accentFor(themeColor)

    val colorScheme = if (darkTheme) {
        darkColorScheme(
            // Primary
            primary = a.primaryDark,
            onPrimary = a.onPrimaryDark,
            primaryContainer = a.primaryContainerDark,
            onPrimaryContainer = a.onPrimaryContainerDark,
            // Secondary
            secondary = a.secondaryDark,
            onSecondary = a.onSecondaryDark,
            secondaryContainer = a.secondaryContainerDark,
            onSecondaryContainer = a.onSecondaryContainerDark,
            // Tertiary
            tertiary = a.tertiaryDark,
            onTertiary = a.onTertiaryDark,
            tertiaryContainer = a.tertiaryContainerDark,
            onTertiaryContainer = a.onTertiaryContainerDark,
            // Surfaces — neutral, no tint
            surfaceDim = Dark_SurfaceDim,
            surface = Dark_Surface,
            surfaceBright = Dark_SurfaceBright,
            surfaceContainerLowest = Dark_ContainerLowest,
            surfaceContainerLow = Dark_ContainerLow,
            surfaceContainer = Dark_Container,
            surfaceContainerHigh = Dark_ContainerHigh,
            surfaceContainerHighest = Dark_ContainerHighest,
            onSurface = DarkOnSurface,
            onSurfaceVariant = DarkOnSurfaceVariant,
            outline = DarkOutline,
            outlineVariant = DarkOutlineVariant,
            // Error
            error = DarkError,
            onError = DarkOnError,
            errorContainer = DarkErrorContainer,
            onErrorContainer = DarkOnErrorContainer
        )
    } else {
        lightColorScheme(
            // Primary
            primary = a.primaryLight,
            onPrimary = a.onPrimaryLight,
            primaryContainer = a.primaryContainerLight,
            onPrimaryContainer = a.onPrimaryContainerLight,
            // Secondary
            secondary = a.secondaryLight,
            onSecondary = a.onSecondaryLight,
            secondaryContainer = a.secondaryContainerLight,
            onSecondaryContainer = a.onSecondaryContainerLight,
            // Tertiary
            tertiary = a.tertiaryLight,
            onTertiary = a.onTertiaryLight,
            tertiaryContainer = a.tertiaryContainerLight,
            onTertiaryContainer = a.onTertiaryContainerLight,
            // Surfaces — neutral, no tint
            surfaceDim = Light_SurfaceDim,
            surface = Light_Surface,
            surfaceBright = Light_SurfaceBright,
            surfaceContainerLowest = Light_ContainerLowest,
            surfaceContainerLow = Light_ContainerLow,
            surfaceContainer = Light_Container,
            surfaceContainerHigh = Light_ContainerHigh,
            surfaceContainerHighest = Light_ContainerHighest,
            onSurface = LightOnSurface,
            onSurfaceVariant = LightOnSurfaceVariant,
            outline = LightOutline,
            outlineVariant = LightOutlineVariant,
            // Error
            error = LightError,
            onError = LightOnError,
            errorContainer = LightErrorContainer,
            onErrorContainer = LightOnErrorContainer
        )
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val w = (view.context as Activity).window
            w.statusBarColor = colorScheme.surface.toArgb()
            WindowCompat.getInsetsController(w, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
