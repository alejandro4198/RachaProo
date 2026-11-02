package com.example.rachapro.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.clickable
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.example.rachapro.ui.theme.RachaAmber
import com.example.rachapro.ui.theme.RachaBackground
import com.example.rachapro.ui.theme.RachaCyan
import com.example.rachapro.ui.theme.RachaIndigo
import com.example.rachapro.ui.theme.RachaOnSurfaceMuted
import com.example.rachapro.ui.theme.RachaSurface

@Composable
fun HomeScreen(
    userName: String? = null,
    onStatClick: (String) -> Unit,
    onLogout: () -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
            .fillMaxHeight(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {

        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "¡Hola!",
                fontSize = 14.sp,
                color = RachaOnSurfaceMuted,
            )
            Text(
                text = userName ?: "Usuario",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = RachaSurface
            )
        }

        // Estadísticas simples
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            simpleStat("📚", "Actividades", "3", RachaIndigo, onStatClick)
            simpleStat("🔥", "Racha", "7", RachaAmber, onStatClick)
            simpleStat("📈", "Productividad", "85%", RachaCyan, onStatClick)
        }
    }
}

@Composable
private fun simpleStat(
    icon: String,
    label: String,
    value: String,
    color: androidx.compose.ui.graphics.Color,
    onClick: (String) -> Unit
) {
    Text(
        text = "$icon $value $label",
        fontSize = 14.sp,
        color = color,
        modifier = Modifier.clickable { onClick(label) }
    )
}