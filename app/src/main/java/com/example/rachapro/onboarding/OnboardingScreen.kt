package com.example.rachapro.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private data class OnboardingPage(
    val icon: String,
    val title: String,
    val description: String
)

private val onboardingPages = listOf(
    OnboardingPage(
        icon = "✓",
        title = "Organiza tus actividades",
        description = "Crea y gestiona todas tus tareas académicas en un solo lugar. Prioriza lo importante."
    ),
    OnboardingPage(
        icon = "◎",
        title = "Estudia con enfoque",
        description = "Usa la técnica Pomodoro para mantener tu concentración y mejorar tu productividad."
    ),
    OnboardingPage(
        icon = "↗",
        title = "Construye tu racha",
        description = "Mantén el ritmo día a día. Gana puntos, desbloquea logros y alcanza tus metas."
    )
)

@Composable
fun OnboardingScreen(
    onNext: () -> Unit
) {

    var currentPage by rememberSaveable {
        mutableIntStateOf(0)
    }

    val page = onboardingPages[currentPage]

    val purple = Color(0xFF6847FF)
    val blue = Color(0xFF5280FF)
    val background = Color(0xFFF8F9FD)
    val primaryText = Color(0xFF171B26)
    val secondaryText = Color(0xFF7A8194)
    val borderColor = Color(0xFFE1E4EC)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(background)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 24.dp)
    ) {

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(130.dp))

            Box(
                modifier = Modifier
                    .size(128.dp)
                    .shadow(
                        elevation = 10.dp,
                        shape = RoundedCornerShape(22.dp)
                    )
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                purple,
                                blue
                            )
                        ),
                        shape = RoundedCornerShape(22.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {

                Text(
                    text = page.icon,
                    color = Color.White,
                    fontSize = 56.sp,
                    fontWeight = FontWeight.Normal
                )
            }

            Spacer(modifier = Modifier.height(36.dp))

            Text(
                text = page.title,
                color = primaryText,
                fontSize = 30.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = page.description,
                color = secondaryText,
                fontSize = 18.sp,
                lineHeight = 28.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 8.dp)
            )

            Spacer(modifier = Modifier.height(46.dp))

            PageIndicator(
                currentPage = currentPage,
                purple = purple,
                inactiveColor = borderColor
            )
        }

        if (currentPage == 0) {

            Button(
                onClick = {
                    currentPage++
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(58.dp)
                    .align(Alignment.BottomCenter),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = purple,
                    contentColor = Color.White
                )
            ) {

                Text(
                    text = "Siguiente",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

        } else {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                OutlinedButton(
                    onClick = {
                        currentPage--
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(58.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {

                    Text(
                        text = "‹  Atrás",
                        color = secondaryText,
                        fontSize = 16.sp
                    )
                }

                Button(
                    onClick = {
                        if (currentPage < onboardingPages.lastIndex) {
                            currentPage++
                        } else {
                            onNext()
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(58.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = purple,
                        contentColor = Color.White
                    )
                ) {

                    Text(
                        text = if (currentPage == onboardingPages.lastIndex) {
                            "Empezar"
                        } else {
                            "Siguiente"
                        },
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

@Composable
private fun PageIndicator(
    currentPage: Int,
    purple: Color,
    inactiveColor: Color
) {

    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        repeat(3) { index ->

            if (index == currentPage) {

                Box(
                    modifier = Modifier
                        .size(
                            width = 32.dp,
                            height = 8.dp
                        )
                        .background(
                            color = purple,
                            shape = RoundedCornerShape(50)
                        )
                )

            } else {

                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(
                            color = inactiveColor,
                            shape = CircleShape
                        )
                )
            }
        }
    }
}