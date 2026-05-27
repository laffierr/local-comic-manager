package com.example.comiclibrary.util

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

enum class ThemeMode { SYSTEM, DARK, LIGHT }
enum class ThemeColor(val displayName: String) {
    PURPLE("紫色"),
    BLUE("蓝色"),
    GREEN("绿色"),
    ROSE("玫红"),
    ORANGE("橙色"),
    TEAL("青色")
}

@Singleton
class SettingsManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val themeKey = stringPreferencesKey("theme_mode")
    private val gridColumnsKey = intPreferencesKey("grid_columns")
    private val pinnedCollectionsKey = stringSetPreferencesKey("pinned_collections")
    private val themeColorKey = stringPreferencesKey("theme_color")

    // ---- ThemeMode: DataStore-backed Flow ----

    val themeMode: Flow<ThemeMode> = context.dataStore.data.map { prefs ->
        when (prefs[themeKey]) {
            "dark" -> ThemeMode.DARK
            "light" -> ThemeMode.LIGHT
            else -> ThemeMode.SYSTEM
        }
    }.distinctUntilChanged()

    // ---- ThemeColor: DataStore-backed Flow ----

    val themeColor: Flow<ThemeColor> = context.dataStore.data.map { prefs ->
        val name = prefs[themeColorKey] ?: return@map ThemeColor.PURPLE
        try {
            ThemeColor.valueOf(name.uppercase())
        } catch (_: Exception) {
            ThemeColor.PURPLE
        }
    }.distinctUntilChanged()

    // ---- Grid columns ----

    val gridColumns: Flow<Int> = context.dataStore.data.map { prefs ->
        prefs[gridColumnsKey] ?: 3
    }.distinctUntilChanged()

    // ---- Pinned collections ----

    val pinnedCollectionIds: Flow<Set<String>> = context.dataStore.data.map { prefs ->
        prefs[pinnedCollectionsKey] ?: emptySet()
    }.distinctUntilChanged()

    // ---- Write methods ----

    suspend fun setThemeMode(mode: ThemeMode) {
        context.dataStore.edit { prefs ->
            prefs[themeKey] = mode.name.lowercase()
        }
    }

    suspend fun setThemeColor(color: ThemeColor) {
        context.dataStore.edit { prefs ->
            prefs[themeColorKey] = color.name.lowercase()
        }
    }

    suspend fun setGridColumns(columns: Int) {
        context.dataStore.edit { prefs ->
            prefs[gridColumnsKey] = columns.coerceIn(2, 6)
        }
    }

    suspend fun togglePinnedCollection(collectionId: Long) {
        context.dataStore.edit { prefs ->
            val current = prefs[pinnedCollectionsKey]?.toMutableSet() ?: mutableSetOf()
            val idStr = collectionId.toString()
            if (current.contains(idStr)) current.remove(idStr) else current.add(idStr)
            prefs[pinnedCollectionsKey] = current
        }
    }
}
