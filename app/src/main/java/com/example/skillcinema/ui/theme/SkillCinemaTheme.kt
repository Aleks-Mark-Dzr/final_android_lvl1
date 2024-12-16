package com.example.skillcinema.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

@Composable
fun SkillCinemaTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        // Здесь вы можете указать собственные параметры темы,
        // такие как цветовые схемы, типографика и т. д.
        content = content
    )
}
