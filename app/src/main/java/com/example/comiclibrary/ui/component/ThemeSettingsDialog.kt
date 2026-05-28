package com.example.comiclibrary.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.comiclibrary.util.ThemeColor
import com.example.comiclibrary.util.ThemeMode

private val colorMap = mapOf(
    ThemeColor.PURPLE to Color(0xFF6750A4),
    ThemeColor.BLUE to Color(0xFF1B6EF3),
    ThemeColor.GREEN to Color(0xFF38853B),
    ThemeColor.ROSE to Color(0xFFBE2E62),
    ThemeColor.ORANGE to Color(0xFFCC5800),
    ThemeColor.TEAL to Color(0xFF007A6B)
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ThemeSettingsDialog(
    currentMode: ThemeMode,
    currentColor: ThemeColor,
    onModeChange: (ThemeMode) -> Unit,
    onColorChange: (ThemeColor) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("主题设置") },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text("外观模式", style = MaterialTheme.typography.titleSmall)
                Spacer(Modifier.height(8.dp))
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ThemeMode.entries.forEach { mode ->
                        FilterChip(
                            selected = currentMode == mode,
                            onClick = { onModeChange(mode) },
                            label = {
                                Text(
                                    when (mode) {
                                        ThemeMode.SYSTEM -> "跟随系统"
                                        ThemeMode.DARK -> "深色"
                                        ThemeMode.LIGHT -> "浅色"
                                    }
                                )
                            }
                        )
                    }
                }

                Spacer(Modifier.height(20.dp))
                Text("主题颜色", style = MaterialTheme.typography.titleSmall)
                Spacer(Modifier.height(8.dp))
                FlowRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    ThemeColor.entries.forEach { themeColor ->
                        val color = colorMap[themeColor] ?: Color.Gray
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(color)
                                .then(
                                    if (currentColor == themeColor)
                                        Modifier.border(3.dp, MaterialTheme.colorScheme.onSurface, CircleShape)
                                    else Modifier
                                )
                                .clickable { onColorChange(themeColor) },
                            contentAlignment = Alignment.Center
                        ) {
                            if (currentColor == themeColor) {
                                Icon(
                                    Icons.Filled.Check,
                                    contentDescription = "已选择",
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("完成") }
        }
    )
}
