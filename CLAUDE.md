# CLAUDE.md

This file provides guidance to Claude Code when working with code in this repository.

## 对话语言

**所有与本项目的对话请使用中文进行交流。** 代码、变量名、技术术语可使用英文，但解释、讨论、commit message 等均使用中文。

## 新对话快速上手

展开新对话时，按以下顺序快速了解项目：

1. **读取 README.md** — 了解软件功能列表、技术栈、版本历史
2. **读取本文件** — 了解架构、构建命令、关键规则
3. **检查 git log** — `git log --oneline -10` 了解最近改动
4. **检查记忆系统** — 读取 `memory/MEMORY.md` 了解用户偏好和历史决策

项目的全局 Tips（构建命令、JDK 位置、规则约定等）都记录在本文件中，修改后同步更新。

## Project Overview

ComicLibrary — an Android comic library manager app built with Jetpack Compose, targeting SDK 36 (min 26).

**Tech stack:** Kotlin, Jetpack Compose (Material 3), Hilt DI, Room database, DataStore preferences, Coil image loading, Navigation Compose.

## Build and Commands

```bash
# Build debug APK (requires JAVA_HOME set)
export JAVA_HOME="D:\\APPs\\Android Studio\\jbr"
export PATH="$JAVA_HOME/bin:$PATH"
./gradlew assembleDebug

# Run unit tests
./gradlew test

# Run instrumentation tests
./gradlew connectedAndroidTest
```

**JDK location:** `D:\APPs\Android Studio\jbr` (JetBrains Runtime 21 bundled with Android Studio)

**APK output:** `app/build/outputs/apk/debug/app-debug.apk`

## Debugging with Android Studio

The user has Android Studio installed at `D:\APPs\Android Studio`. You can use it for:
- Opening the project: `D:\APPs\Android Studio\bin\studio64.exe .`
- Building signed APKs
- Debugging on device/emulator
- Running lint checks and profilers

If you cannot run the app yourself (no device/emulator), instruct the user to open Android Studio and run the `app` module directly.

Android SDK is at: `C:\Users\wyz\AppData\Local\Android\Sdk`

## Architecture

MVVM with repository pattern:
- `ui/` — Compose screens + ViewModels (one VM per screen via Hilt)
- `data/local/` — Room DAOs, entities, AppDatabase
- `data/repository/` — Repository interfaces + implementations
- `di/` — Hilt modules (AppModule, RepositoryModule)
- `domain/model/` — Domain models
- `util/` — Utilities (SettingsManager, CoverScanner, ImageEnumerator, FavoriteManager)

### Navigation Structure

```
NavHost (startDestination = "tabs")
  ├── "tabs" → MainTabsContent (HorizontalPager + BottomNavigationBar)
  │     ├── Page 0: BookshelfScreen
  │     └── Page 1: FavoritesScreen
  ├── "home" → HomeScreen
  ├── "comic/{comicId}" → ComicDetailScreen
  ├── "favorites/collection/{collectionId}" → CollectionDetailScreen
  ├── "reader/{comicId}" → ReaderScreen
  ├── "tag-comics/{tagId}/{tagName}" → TagComicsScreen
  └── "tags" → TagScreen
```

Key points:
- Main tabs (Bookshelf/Favorites) use HorizontalPager for swipe navigation, NOT NavHost routing
- Detail screens push on top of "tabs" via NavHost — bottom bar auto-hides
- Bottom bar is inside MainTabsContent, not in MainScreen
- `AppNavGraph` takes `onThemeSettings` callback from MainScreen

### Theme Architecture

- `SettingsManager` stores `themeMode` and `themeColor` as DataStore-backed `Flow`s (both use `distinctUntilChanged()`)
- `MainActivity` reads both flows via `collectAsStateWithLifecycle` and passes to `ComicLibraryTheme`
- `ThemeSettingsDialog` (in `ui/component/`) is the centralized theme picker
- No theme toggles in individual screens — only a settings icon that opens the dialog
- `ThemeColor` is stored as lowercase string in DataStore, parsed via `ThemeColor.valueOf(name.uppercase())`

## Critical Rules

### Version Bumping
每次修改完 bug 或完成功能后，必须在 `app/build.gradle.kts` 中同时推进 `versionCode` 和 `versionName`：
- `versionCode` 递增 1 (当前 15 → 16 → 17...)
- `versionName` 按语义化版本递增 (当前 0.6.0)

版本号规则（语义化版本 SemVer）：

| 改动类型 | 升哪个 | 示例 |
|----------|--------|------|
| 修 bug、小幅调整 | PATCH | 0.6.0 → 0.6.1 |
| 加新功能（兼容旧版） | MINOR | 0.6.0 → 0.7.0 |
| 破坏性改动（不兼容旧版） | MAJOR | 0.6.0 → 1.0.0 |

正式发布后（Google Play 上线）建议跳到 `1.0.0`。`versionCode` 只是一个递增整数，Google Play 用它判断哪个包更新——只能递增、不能重复。

### Git Commits
每次代码改动完成后，必须执行一次 git commit，记录本次更改的内容。commit message 使用中文描述。

### README 同步
每次修改代码后，检查 README.md 中的功能描述是否仍然准确，如果功能有增删改，同步更新 README。

### 全局 Tips
上述以及其他类似的全局 tips（如构建命令、JDK 位置等），都记录在本 CLAUDE.md 中，方便新对话继承上下文。

### State Management in ViewModels
ViewModels that combine database flows with transient UI state (dialogs, toasts, etc.) must include the transient state in the `combine{}` block. A separate `_uiState` MutableStateFlow that's never observed is a bug.

**Correct pattern (ComicDetailViewModel):** add a `_dialogState` MutableStateFlow and include it in the `combine{}` call.

**Correct pattern (BookshelfViewModel):** use a single `_uiState` MutableStateFlow for all UI state and derive display flows from it via `flatMapLatest`.

**Snackbar pattern:** add `snackbarMessage: String?` to UiState, use `LaunchedEffect` in the Composable to show via `SnackbarHostState`, call `dismissSnackbar()` to clear.

**Important:** `kotlinx.coroutines.flow.combine` only has overloads for up to 5 flows. When you need 6+ flows, nest two `combine` calls.

### DataStore Settings
Both `themeMode` and `themeColor` are DataStore-backed `Flow`s. `setThemeMode()` and `setThemeColor()` write to DataStore only — the Flow automatically emits the new value. No MutableStateFlow or manual sync needed.

### DocumentFile / SAF
All file access goes through Android's Storage Access Framework (SAF). Folder URIs must have persistable permissions granted via `contentResolver.takePersistableUriPermission()`. `CoverScanner` and `ImageEnumerator` work exclusively with `DocumentFile`.

### Room DAOs
- Observables return `Flow<List<ComicWithCollections>>` (transaction-wrapped)
- Write operations are `suspend` functions
- Foreign key cascades are defined in entity cross-refs, not in DAO queries

### compileSdk Warning
AGP 8.7.3 does not officially support compileSdk 36. To suppress the warning, `gradle.properties` has:
```
android.suppressUnsupportedCompileSdk=36
```

### 记忆系统 (Memory)

项目的持久化记忆存储在 `memory/` 目录中，包含：
- `MEMORY.md` — 所有记忆的索引文件，每行一个条目
- 用户偏好、项目决策、反馈记录等

新对话时应检查 `memory/MEMORY.md` 了解用户历史偏好。用户明确要求记住某事时，写入对应的记忆文件。

### 配色设计理念

当前使用**品牌主色 + 中性辅助**方案（方案 C）：
- **primary** / **primaryContainer**：品牌色，饱和度高，有辨识度
- **secondary** / **secondaryContainer**：接近中性色，只带微量品牌色相
- **tertiary** / **tertiaryContainer**：几乎完全中性，与 surface 接近

这样设计的原因：Material 3 的 FAB 使用 `primaryContainer`，FilterChip 选中态使用 `secondaryContainer`。如果 secondary 色相与 primary 差异过大，按钮颜色会不统一。将所有 accent 收敛到同一色系解决了这个问题。

## File Index

| Area | Key Files |
|------|-----------|
| App entry | `MainActivity.kt`, `ComicLibraryApp.kt` |
| Theme | `ui/theme/Theme.kt`, `ui/theme/Color.kt`, `util/SettingsManager.kt`, `ui/component/ThemeSettingsDialog.kt` |
| Navigation | `ui/navigation/AppNavGraph.kt`, `Screen.kt`, `BottomNavItem.kt` |
| Bookshelf | `ui/bookshelf/BookshelfScreen.kt`, `BookshelfViewModel.kt`, `BookshelfUiState.kt` |
| Comic detail | `ui/comicdetail/ComicDetailScreen.kt`, `ComicDetailViewModel.kt` |
| Favorites | `ui/favorites/FavoritesScreen.kt`, `FavoritesViewModel.kt`, `CollectionDetailScreen.kt` |
| Tags | `ui/tags/TagScreen.kt`, `TagViewModel.kt` |
| Database | `data/local/AppDatabase.kt`, `dao/*.kt`, `entity/*.kt` |
| Repositories | `data/repository/ComicRepository.kt`, `TagRepository.kt`, `CollectionRepository.kt` |
| Components | `ui/component/ComicCoverCard.kt`, `ConfirmDeleteDialog.kt`, `ThemeSettingsDialog.kt`, etc. |
