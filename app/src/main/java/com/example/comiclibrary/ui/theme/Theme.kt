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
    val colorScheme = if (darkTheme) {
        darkColorScheme(
            primary = when (themeColor) {
                ThemeColor.PURPLE -> PurplePrimary
                ThemeColor.BLUE -> BluePrimary
                ThemeColor.GREEN -> GreenPrimary
                ThemeColor.ROSE -> RosePrimary
                ThemeColor.ORANGE -> OrangePrimary
                ThemeColor.TEAL -> TealPrimary
            },
            onPrimary = DarkOnPrimary,
            primaryContainer = when (themeColor) {
                ThemeColor.PURPLE -> PurplePrimaryContainer
                ThemeColor.BLUE -> BluePrimaryContainer
                ThemeColor.GREEN -> GreenPrimaryContainer
                ThemeColor.ROSE -> RosePrimaryContainer
                ThemeColor.ORANGE -> OrangePrimaryContainer
                ThemeColor.TEAL -> TealPrimaryContainer
            },
            onPrimaryContainer = DarkOnPrimaryContainer,
            secondary = when (themeColor) {
                ThemeColor.PURPLE -> PurpleSecondary
                ThemeColor.BLUE -> BlueSecondary
                ThemeColor.GREEN -> GreenSecondary
                ThemeColor.ROSE -> RoseSecondary
                ThemeColor.ORANGE -> OrangeSecondary
                ThemeColor.TEAL -> TealSecondary
            },
            onSecondary = DarkOnPrimary,
            secondaryContainer = when (themeColor) {
                ThemeColor.PURPLE -> PurpleSecondaryContainer
                ThemeColor.BLUE -> BlueSecondaryContainer
                ThemeColor.GREEN -> GreenSecondaryContainer
                ThemeColor.ROSE -> RoseSecondaryContainer
                ThemeColor.ORANGE -> OrangeSecondaryContainer
                ThemeColor.TEAL -> TealSecondaryContainer
            },
            onSecondaryContainer = DarkOnPrimaryContainer,
            tertiary = when (themeColor) {
                ThemeColor.PURPLE -> PurpleTertiary
                ThemeColor.BLUE -> BlueTertiary
                ThemeColor.GREEN -> GreenTertiary
                ThemeColor.ROSE -> RoseTertiary
                ThemeColor.ORANGE -> OrangeTertiary
                ThemeColor.TEAL -> TealTertiary
            },
            onTertiary = DarkOnPrimary,
            tertiaryContainer = when (themeColor) {
                ThemeColor.PURPLE -> PurpleTertiaryContainer
                ThemeColor.BLUE -> BlueTertiaryContainer
                ThemeColor.GREEN -> GreenTertiaryContainer
                ThemeColor.ROSE -> RoseTertiaryContainer
                ThemeColor.ORANGE -> OrangeTertiaryContainer
                ThemeColor.TEAL -> TealTertiaryContainer
            },
            onTertiaryContainer = DarkOnPrimaryContainer,
            background = DarkBackground,
            onBackground = DarkOnSurface,
            surface = DarkSurface,
            onSurface = DarkOnSurface,
            surfaceVariant = DarkSurfaceVariant,
            onSurfaceVariant = DarkOnSurfaceVariant,
            surfaceContainerLowest = DarkSurfaceContainerLowest,
            surfaceContainerLow = DarkSurfaceContainerLow,
            surfaceContainer = DarkSurfaceContainer,
            surfaceContainerHigh = DarkSurfaceContainerHigh,
            surfaceContainerHighest = DarkSurfaceContainerHighest,
            error = DarkError,
            onError = DarkOnError,
            outline = DarkOutline
        )
    } else {
        lightColorScheme(
            primary = when (themeColor) {
                ThemeColor.PURPLE -> PurplePrimaryLight
                ThemeColor.BLUE -> BluePrimaryLight
                ThemeColor.GREEN -> GreenPrimaryLight
                ThemeColor.ROSE -> RosePrimaryLight
                ThemeColor.ORANGE -> OrangePrimaryLight
                ThemeColor.TEAL -> TealPrimaryLight
            },
            onPrimary = LightOnPrimary,
            primaryContainer = when (themeColor) {
                ThemeColor.PURPLE -> PurplePrimaryContainerLight
                ThemeColor.BLUE -> BluePrimaryContainerLight
                ThemeColor.GREEN -> GreenPrimaryContainerLight
                ThemeColor.ROSE -> RosePrimaryContainerLight
                ThemeColor.ORANGE -> OrangePrimaryContainerLight
                ThemeColor.TEAL -> TealPrimaryContainerLight
            },
            onPrimaryContainer = LightOnPrimaryContainer,
            secondary = when (themeColor) {
                ThemeColor.PURPLE -> PurpleSecondaryLight
                ThemeColor.BLUE -> BlueSecondaryLight
                ThemeColor.GREEN -> GreenSecondaryLight
                ThemeColor.ROSE -> RoseSecondaryLight
                ThemeColor.ORANGE -> OrangeSecondaryLight
                ThemeColor.TEAL -> TealSecondaryLight
            },
            onSecondary = LightOnPrimary,
            secondaryContainer = when (themeColor) {
                ThemeColor.PURPLE -> PurpleSecondaryContainerLight
                ThemeColor.BLUE -> BlueSecondaryContainerLight
                ThemeColor.GREEN -> GreenSecondaryContainerLight
                ThemeColor.ROSE -> RoseSecondaryContainerLight
                ThemeColor.ORANGE -> OrangeSecondaryContainerLight
                ThemeColor.TEAL -> TealSecondaryContainerLight
            },
            onSecondaryContainer = LightOnPrimaryContainer,
            tertiary = when (themeColor) {
                ThemeColor.PURPLE -> PurpleTertiaryLight
                ThemeColor.BLUE -> BlueTertiaryLight
                ThemeColor.GREEN -> GreenTertiaryLight
                ThemeColor.ROSE -> RoseTertiaryLight
                ThemeColor.ORANGE -> OrangeTertiaryLight
                ThemeColor.TEAL -> TealTertiaryLight
            },
            onTertiary = LightOnPrimary,
            tertiaryContainer = when (themeColor) {
                ThemeColor.PURPLE -> PurpleTertiaryContainerLight
                ThemeColor.BLUE -> BlueTertiaryContainerLight
                ThemeColor.GREEN -> GreenTertiaryContainerLight
                ThemeColor.ROSE -> RoseTertiaryContainerLight
                ThemeColor.ORANGE -> OrangeTertiaryContainerLight
                ThemeColor.TEAL -> TealTertiaryContainerLight
            },
            onTertiaryContainer = LightOnPrimaryContainer,
            background = LightBackground,
            onBackground = LightOnSurface,
            surface = LightSurface,
            onSurface = LightOnSurface,
            surfaceVariant = LightSurfaceVariant,
            onSurfaceVariant = LightOnSurfaceVariant,
            surfaceContainerLowest = LightSurfaceContainerLowest,
            surfaceContainerLow = LightSurfaceContainerLow,
            surfaceContainer = LightSurfaceContainer,
            surfaceContainerHigh = LightSurfaceContainerHigh,
            surfaceContainerHighest = LightSurfaceContainerHighest,
            error = LightError,
            onError = LightOnError,
            outline = LightOutline
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
