package com.example.rachapro.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun HomeScreen(
    isLoggingOut: Boolean,
    onLogout: () -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FD))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = "Inicio",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF171B26)
        )

        Text(
            text = "Sesión iniciada correctamente",
            modifier = Modifier.padding(top = 12.dp),
            fontSize = 16.sp,
            color = Color(0xFF7A8194)
        )

        Button(
            onClick = onLogout,
            modifier = Modifier.padding(top = 32.dp),
            enabled = !isLoggingOut
        ) {

            Text(
                text =
                    if (isLoggingOut) {
                        "Cerrando sesión..."
                    } else {
                        "Cerrar sesión"
                    }
            )
        }
    }
}