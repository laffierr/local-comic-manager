package com.example.comiclibrary

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.rememberNavController
import com.example.comiclibrary.ui.component.ThemeSettingsDialog
import com.example.comiclibrary.ui.navigation.AppNavGraph
import com.example.comiclibrary.ui.theme.ComicLibraryTheme
import com.example.comiclibrary.util.SettingsManager
import com.example.comiclibrary.util.ThemeColor
import com.example.comiclibrary.util.ThemeMode
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject lateinit var settingsManager: SettingsManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val themeMode by settingsManager.themeMode.collectAsStateWithLifecycle(initialValue = ThemeMode.SYSTEM)
            val themeColor by settingsManager.themeColor.collectAsStateWithLifecycle(initialValue = ThemeColor.PURPLE)
            val isDark = when (themeMode) {
                ThemeMode.DARK -> true
                ThemeMode.LIGHT -> false
                ThemeMode.SYSTEM -> isSystemInDarkTheme()
            }

            ComicLibraryTheme(darkTheme = isDark, themeColor = themeColor) {
                MainScreen(settingsManager = settingsManager)
            }
        }
    }
}

@Composable
fun MainScreen(settingsManager: SettingsManager) {
    val navController = rememberNavController()
    val scope = rememberCoroutineScope()
    var showThemeDialog by remember { mutableStateOf(false) }

    val themeMode by settingsManager.themeMode.collectAsStateWithLifecycle(initialValue = ThemeMode.SYSTEM)
    val themeColor by settingsManager.themeColor.collectAsStateWithLifecycle(initialValue = ThemeColor.PURPLE)

    if (showThemeDialog) {
        ThemeSettingsDialog(
            currentMode = themeMode,
            currentColor = themeColor,
            onModeChange = { mode ->
                scope.launch { settingsManager.setThemeMode(mode) }
            },
            onColorChange = { color ->
                scope.launch { settingsManager.setThemeColor(color) }
            },
            onDismiss = { showThemeDialog = false }
        )
    }

    Scaffold { innerPadding ->
        AppNavGraph(
            navController = navController,
            onThemeSettings = { showThemeDialog = true },
            modifier = Modifier
                .padding(innerPadding)
                .consumeWindowInsets(innerPadding)
        )
    }
}
