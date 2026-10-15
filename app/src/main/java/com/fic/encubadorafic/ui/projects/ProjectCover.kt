package com.fic.encubadorafic.ui.projects

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ProjectCover(categoryId: Long, modifier: Modifier = Modifier) {
    val colors = when (categoryId) {
        2L -> listOf(Color(0xFF174E46), Color(0xFF56A78C))
        3L -> listOf(Color(0xFF4B3A75), Color(0xFF9C89C8))
        4L -> listOf(Color(0xFF713D27), Color(0xFFD69A65))
        else -> listOf(Color(0xFF142D4E), Color(0xFF4D8CD7))
    }
    val icon = when (categoryId) {
        2L -> Icons.Default.Eco; 3L -> Icons.Default.School; 4L -> Icons.Default.Memory; else -> Icons.Default.Code
    }
    Box(
        modifier
            .fillMaxWidth()
            .clipToBounds()
            .background(Brush.linearGradient(colors)),
        contentAlignment = Alignment.Center
    ) {
        Box(
            Modifier
                .align(Alignment.TopEnd)
                .offset(x = 35.dp, y = (-35).dp)
                .size(170.dp)
                .background(Color.White.copy(alpha = .08f), CircleShape)
        )
        Box(
            Modifier
                .align(Alignment.BottomStart)
                .offset(x = (-40).dp, y = 45.dp)
                .size(190.dp)
                .background(Color.White.copy(alpha = .06f), CircleShape)
        )
        Icon(icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(72.dp))
        Text(
            "INCUBADORA · FIC", color = Color.White.copy(alpha = .8f), fontSize = 11.sp,
            letterSpacing = 3.sp, modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(20.dp)
        )
    }
}
