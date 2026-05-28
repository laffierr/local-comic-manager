package com.example.comiclibrary.ui.theme

import androidx.compose.ui.graphics.Color

// ===== 中性表面色 — 所有主题色共享 =====
// 深色模式: tone 4 (最深) → 24 (最浅/最突出)
// 浅色模式: tone 87 (最暗/最突出) → 100 (最浅)

// Dark neutral surfaces
val Dark_SurfaceDim = Color(0xFF0E0E0E)          // tone ~4
val Dark_Surface = Color(0xFF121212)             // tone ~6
val Dark_SurfaceBright = Color(0xFF181818)       // tone ~8
val Dark_ContainerLowest = Color(0xFF0E0E0E)     // tone ~4
val Dark_ContainerLow = Color(0xFF161616)        // tone ~10
val Dark_Container = Color(0xFF1C1C1E)           // tone ~12
val Dark_ContainerHigh = Color(0xFF242428)       // tone ~17
val Dark_ContainerHighest = Color(0xFF2E2E31)    // tone ~22
val DarkOnSurface = Color(0xFFE3E2E6)                    // tone ~92
val DarkOnSurfaceVariant = Color(0xFFC4C3C8)             // tone ~80
val DarkOutline = Color(0xFF8E8D93)                      // tone ~60
val DarkOutlineVariant = Color(0xFF48474D)               // tone ~30
val DarkError = Color(0xFFFFB4AB)
val DarkOnError = Color(0xFF690005)
val DarkErrorContainer = Color(0xFF93000A)
val DarkOnErrorContainer = Color(0xFFFFDAD6)

// Light neutral surfaces
val Light_SurfaceDim = Color(0xFFDBD9DE)         // tone ~87
val Light_Surface = Color(0xFFF8F8FB)            // tone ~98
val Light_SurfaceBright = Color(0xFFF8F8FB)      // tone ~98
val Light_ContainerLowest = Color(0xFFFFFFFF)     // tone 100
val Light_ContainerLow = Color(0xFFF2F1F5)       // tone ~96
val Light_Container = Color(0xFFECEBEF)          // tone ~94
val Light_ContainerHigh = Color(0xFFE6E5EA)      // tone ~92
val Light_ContainerHighest = Color(0xFFE0DFE4)   // tone ~90
val LightOnSurface = Color(0xFF1C1B1F)                   // tone ~10
val LightOnSurfaceVariant = Color(0xFF49454F)            // tone ~30
val LightOutline = Color(0xFF79747E)                     // tone ~50
val LightOutlineVariant = Color(0xFFCAC4D0)              // tone ~80
val LightError = Color(0xFFBA1A1A)
val LightOnError = Color(0xFFFFFFFF)
val LightErrorContainer = Color(0xFFFFDAD6)
val LightOnErrorContainer = Color(0xFF410002)

// ===== 主题强调色 — 品牌主色 + 中性辅助方案 =====
// primary: 高饱和度品牌色，辨识度强
// secondary: 接近中性色，只带微量品牌色相，用于 FilterChip 等
// tertiary: 几乎完全中性，与 surface 接近，不抢眼
// 这样 FAB (primaryContainer) 和 FilterChip 选中 (secondaryContainer) 视觉统一

data class AccentSet(
    val primaryDark: Color, val onPrimaryDark: Color,
    val primaryContainerDark: Color, val onPrimaryContainerDark: Color,
    val secondaryDark: Color, val onSecondaryDark: Color,
    val secondaryContainerDark: Color, val onSecondaryContainerDark: Color,
    val tertiaryDark: Color, val onTertiaryDark: Color,
    val tertiaryContainerDark: Color, val onTertiaryContainerDark: Color,
    val primaryLight: Color, val onPrimaryLight: Color,
    val primaryContainerLight: Color, val onPrimaryContainerLight: Color,
    val secondaryLight: Color, val onSecondaryLight: Color,
    val secondaryContainerLight: Color, val onSecondaryContainerLight: Color,
    val tertiaryLight: Color, val onTertiaryLight: Color,
    val tertiaryContainerLight: Color, val onTertiaryContainerLight: Color,
)

// ---- 紫色 — 经典品牌紫 + 灰紫辅色 ----
val Accent_Purple = AccentSet(
    primaryDark = Color(0xFFD0BCFF), onPrimaryDark = Color(0xFF381E72),
    primaryContainerDark = Color(0xFF4F378B), onPrimaryContainerDark = Color(0xFFEADDFF),
    secondaryDark = Color(0xFFC7BFD5), onSecondaryDark = Color(0xFF302A3C),
    secondaryContainerDark = Color(0xFF3E3849), onSecondaryContainerDark = Color(0xFFDDD7E9),
    tertiaryDark = Color(0xFFBEBAC8), onTertiaryDark = Color(0xFF2B2634),
    tertiaryContainerDark = Color(0xFF373340), onTertiaryContainerDark = Color(0xFFD7D2E2),
    primaryLight = Color(0xFF6750A4), onPrimaryLight = Color(0xFFFFFFFF),
    primaryContainerLight = Color(0xFFEADDFF), onPrimaryContainerLight = Color(0xFF21005D),
    secondaryLight = Color(0xFF635B70), onSecondaryLight = Color(0xFFFFFFFF),
    secondaryContainerLight = Color(0xFFE8DEF8), onSecondaryContainerLight = Color(0xFF1E192B),
    tertiaryLight = Color(0xFF7C7583), onTertiaryLight = Color(0xFFFFFFFF),
    tertiaryContainerLight = Color(0xFFF3EDFA), onTertiaryContainerLight = Color(0xFF221C2F),
)

// ---- 蓝色 — Google 蓝品牌 + 灰蓝辅色 ----
val Accent_Blue = AccentSet(
    primaryDark = Color(0xFFB5CCFF), onPrimaryDark = Color(0xFF002C6C),
    primaryContainerDark = Color(0xFF1C4194), onPrimaryContainerDark = Color(0xFFDCE5FF),
    secondaryDark = Color(0xFFC2CADD), onSecondaryDark = Color(0xFF283248),
    secondaryContainerDark = Color(0xFF3B4457), onSecondaryContainerDark = Color(0xFFD9E1F7),
    tertiaryDark = Color(0xFFBCC1D2), onTertiaryDark = Color(0xFF262D3D),
    tertiaryContainerDark = Color(0xFF353C4C), onTertiaryContainerDark = Color(0xFFD3D9EB),
    primaryLight = Color(0xFF1B6EF3), onPrimaryLight = Color(0xFFFFFFFF),
    primaryContainerLight = Color(0xFFDCE5FF), onPrimaryContainerLight = Color(0xFF001A41),
    secondaryLight = Color(0xFF5A6279), onSecondaryLight = Color(0xFFFFFFFF),
    secondaryContainerLight = Color(0xFFE2E7FC), onSecondaryContainerLight = Color(0xFF171E33),
    tertiaryLight = Color(0xFF757C91), onTertiaryLight = Color(0xFFFFFFFF),
    tertiaryContainerLight = Color(0xFFEEF0FD), onTertiaryContainerLight = Color(0xFF1B2136),
)

// ---- 绿色 — 微信读书风格绿 + 暖灰辅色 ----
val Accent_Green = AccentSet(
    primaryDark = Color(0xFF82D687), onPrimaryDark = Color(0xFF003910),
    primaryContainerDark = Color(0xFF1A5E1F), onPrimaryContainerDark = Color(0xFFBFF1C2),
    secondaryDark = Color(0xFFC1CFBC), onSecondaryDark = Color(0xFF283525),
    secondaryContainerDark = Color(0xFF3E493B), onSecondaryContainerDark = Color(0xFFD9E7D3),
    tertiaryDark = Color(0xFFBCC8B7), onTertiaryDark = Color(0xFF273324),
    tertiaryContainerDark = Color(0xFF384337), onTertiaryContainerDark = Color(0xFFD3DFCE),
    primaryLight = Color(0xFF38853B), onPrimaryLight = Color(0xFFFFFFFF),
    primaryContainerLight = Color(0xFFD3E8D3), onPrimaryContainerLight = Color(0xFF002106),
    secondaryLight = Color(0xFF5F7360), onSecondaryLight = Color(0xFFFFFFFF),
    secondaryContainerLight = Color(0xFFDEF1DC), onSecondaryContainerLight = Color(0xFF162017),
    tertiaryLight = Color(0xFF788D77), onTertiaryLight = Color(0xFFFFFFFF),
    tertiaryContainerLight = Color(0xFFEDFBE9), onTertiaryContainerLight = Color(0xFF1A241B),
)

// ---- 玫红 — 活力品牌玫红 + 灰玫辅色 ----
val Accent_Rose = AccentSet(
    primaryDark = Color(0xFFFFB3C6), onPrimaryDark = Color(0xFF5C002F),
    primaryContainerDark = Color(0xFF8A004C), onPrimaryContainerDark = Color(0xFFFFD9E2),
    secondaryDark = Color(0xFFD2BFC7), onSecondaryDark = Color(0xFF342A2F),
    secondaryContainerDark = Color(0xFF4A3840), onSecondaryContainerDark = Color(0xFFEDD9E1),
    tertiaryDark = Color(0xFFC9BAC1), onTertiaryDark = Color(0xFF30262C),
    tertiaryContainerDark = Color(0xFF43333B), onTertiaryContainerDark = Color(0xFFE4D2DA),
    primaryLight = Color(0xFFBE2E62), onPrimaryLight = Color(0xFFFFFFFF),
    primaryContainerLight = Color(0xFFFFD9E2), onPrimaryContainerLight = Color(0xFF3E001D),
    secondaryLight = Color(0xFF715D65), onSecondaryLight = Color(0xFFFFFFFF),
    secondaryContainerLight = Color(0xFFF2DDE5), onSecondaryContainerLight = Color(0xFF23191D),
    tertiaryLight = Color(0xFF8B767E), onTertiaryLight = Color(0xFFFFFFFF),
    tertiaryContainerLight = Color(0xFFFCEAF2), onTertiaryContainerLight = Color(0xFF271B20),
)

// ---- 橙色 — 温暖品牌橙 + 暖灰辅色 ----
val Accent_Orange = AccentSet(
    primaryDark = Color(0xFFFFB87C), onPrimaryDark = Color(0xFF542200),
    primaryContainerDark = Color(0xFF783300), onPrimaryContainerDark = Color(0xFFFFDCC7),
    secondaryDark = Color(0xFFD1C4B6), onSecondaryDark = Color(0xFF342B1F),
    secondaryContainerDark = Color(0xFF4A3E30), onSecondaryContainerDark = Color(0xFFEBDECF),
    tertiaryDark = Color(0xFFC7BEB2), onTertiaryDark = Color(0xFF30281C),
    tertiaryContainerDark = Color(0xFF423A2D), onTertiaryContainerDark = Color(0xFFE2D8CA),
    primaryLight = Color(0xFFCC5800), onPrimaryLight = Color(0xFFFFFFFF),
    primaryContainerLight = Color(0xFFFFDCC7), onPrimaryContainerLight = Color(0xFF411700),
    secondaryLight = Color(0xFF705D4B), onSecondaryLight = Color(0xFFFFFFFF),
    secondaryContainerLight = Color(0xFFEFDFD0), onSecondaryContainerLight = Color(0xFF23190E),
    tertiaryLight = Color(0xFF8B7765), onTertiaryLight = Color(0xFFFFFFFF),
    tertiaryContainerLight = Color(0xFFFAEADA), onTertiaryContainerLight = Color(0xFF261B11),
)

// ---- 青色 — 清新品牌青 + 冷灰辅色 ----
val Accent_Teal = AccentSet(
    primaryDark = Color(0xFF80E8D6), onPrimaryDark = Color(0xFF00382E),
    primaryContainerDark = Color(0xFF005144), onPrimaryContainerDark = Color(0xFFA7F2EB),
    secondaryDark = Color(0xFFB7CFCA), onSecondaryDark = Color(0xFF1F322F),
    secondaryContainerDark = Color(0xFF364A46), onSecondaryContainerDark = Color(0xFFCFE9E3),
    tertiaryDark = Color(0xFFB2C8C3), onTertiaryDark = Color(0xFF1C2E2B),
    tertiaryContainerDark = Color(0xFF32433F), onTertiaryContainerDark = Color(0xFFC9E2DB),
    primaryLight = Color(0xFF007A6B), onPrimaryLight = Color(0xFFFFFFFF),
    primaryContainerLight = Color(0xFFA7F2EB), onPrimaryContainerLight = Color(0xFF00201C),
    secondaryLight = Color(0xFF52736C), onSecondaryLight = Color(0xFFFFFFFF),
    secondaryContainerLight = Color(0xFFD6F1EB), onSecondaryContainerLight = Color(0xFF071D1A),
    tertiaryLight = Color(0xFF6C8D85), onTertiaryLight = Color(0xFFFFFFFF),
    tertiaryContainerLight = Color(0xFFEBF9F3), onTertiaryContainerLight = Color(0xFF0F2320),
)

// 颜色查找
val accentMap = mapOf(
    com.example.comiclibrary.util.ThemeColor.PURPLE to Accent_Purple,
    com.example.comiclibrary.util.ThemeColor.BLUE to Accent_Blue,
    com.example.comiclibrary.util.ThemeColor.GREEN to Accent_Green,
    com.example.comiclibrary.util.ThemeColor.ROSE to Accent_Rose,
    com.example.comiclibrary.util.ThemeColor.ORANGE to Accent_Orange,
    com.example.comiclibrary.util.ThemeColor.TEAL to Accent_Teal,
)

fun accentFor(color: com.example.comiclibrary.util.ThemeColor): AccentSet = accentMap[color]!!
