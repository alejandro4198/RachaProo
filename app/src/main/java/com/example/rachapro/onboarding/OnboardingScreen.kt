package com.example.rachapro.onboarding

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rachapro.ui.components.RachaBrandGradient
import com.example.rachapro.ui.components.RachaBrandMark
import com.example.rachapro.ui.components.RachaCard
import com.example.rachapro.ui.components.RachaPrimaryButton
import com.example.rachapro.ui.components.RachaSecondaryButton
import com.example.rachapro.ui.components.RachaSoftGradient
import com.example.rachapro.ui.theme.RachaIndigo
import com.example.rachapro.ui.theme.RachaOnSurfaceMuted
import com.example.rachapro.ui.theme.RachaSurface

private data class OnboardingPage(
    val icon: String,
    val eyebrow: String,
    val title: String,
    val description: String
)

private val onboardingPages = listOf(
    OnboardingPage(
        icon = "✓",
        eyebrow = "Organización",
        title = "Organiza tus actividades",
        description = "Crea y gestiona todas tus tareas académicas en un solo lugar. Prioriza lo importante."
    ),
    OnboardingPage(
        icon = "◎",
        eyebrow = "Enfoque",
        title = "Estudia con enfoque",
        description = "Usa la técnica Pomodoro para mantener tu concentración y mejorar tu productividad."
    ),
    OnboardingPage(
        icon = "↗",
        eyebrow = "Constancia",
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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .background(RachaSoftGradient)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RachaBrandMark(size = 40.dp)
                Spacer(modifier = Modifier.size(10.dp))
                Text(
                    text = "RachaPro",
                    style = MaterialTheme.typography.titleLarge
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = "${currentPage + 1} de ${onboardingPages.size}",
                    style = MaterialTheme.typography.labelMedium,
                    color = RachaOnSurfaceMuted
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            AnimatedContent(
                targetState = currentPage,
                transitionSpec = {
                    val direction = if (targetState > initialState) 1 else -1
                    (fadeIn(tween(300)) + slideInHorizontally(
                        initialOffsetX = { direction * it / 6 },
                        animationSpec = tween(300)
                    )) togetherWith (fadeOut(tween(180)) + slideOutHorizontally(
                        targetOffsetX = { -direction * it / 6 },
                        animationSpec = tween(180)
                    ))
                },
                label = "onboardingPage"
            ) { index ->

                val current = onboardingPages[index]

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Box(
                        modifier = Modifier
                            .size(132.dp)
                            .clip(RoundedCornerShape(40.dp))
                            .background(RachaBrandGradient),
                        contentAlignment = Alignment.Center
                    ) {

                        Text(
                            text = current.icon,
                            color = RachaSurface,
                            fontSize = 56.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(28.dp))

                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(RachaIndigo.copy(alpha = 0.10f))
                            .padding(horizontal = 12.dp, vertical = 5.dp)
                    ) {
                        Text(
                            text = current.eyebrow.uppercase(),
                            style = MaterialTheme.typography.labelSmall,
                            color = RachaIndigo,
                            letterSpacing = 1.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = current.title,
                        style = MaterialTheme.typography.headlineLarge,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = current.description,
                        style = MaterialTheme.typography.bodyLarge,
                        color = RachaOnSurfaceMuted,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            PageIndicator(currentPage = currentPage)

            Spacer(modifier = Modifier.weight(1f))

            RachaCard(
                containerColor = MaterialTheme.colorScheme.surface,
                contentPadding = 16.dp
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {

                    if (currentPage == 0) {

                        RachaPrimaryButton(
                            text = "Siguiente",
                            onClick = {
                                currentPage++
                            }
                        )

                    } else {

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {

                            RachaSecondaryButton(
                                text = "Atrás",
                                onClick = {
                                    currentPage--
                                },
                                modifier = Modifier.weight(1f)
                            )

                            RachaPrimaryButton(
                                text = if (currentPage == onboardingPages.lastIndex) {
                                    "Empezar"
                                } else {
                                    "Siguiente"
                                },
                                onClick = {
                                    if (currentPage < onboardingPages.lastIndex) {
                                        currentPage++
                                    } else {
                                        onNext()
                                    }
                                },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Tu progreso académico, en un solo ritmo.",
                        style = MaterialTheme.typography.labelSmall,
                        color = RachaOnSurfaceMuted,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
private fun PageIndicator(
    currentPage: Int
) {

    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        repeat(onboardingPages.size) { index ->

            if (index == currentPage) {

                Box(
                    modifier = Modifier
                        .size(
                            width = 30.dp,
                            height = 8.dp
                        )
                        .background(
                            color = RachaIndigo,
                            shape = RoundedCornerShape(50)
                        )
                )

            } else {

                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(
                            color = MaterialTheme.colorScheme.outline,
                            shape = CircleShape
                        )
                )
            }
        }
    }
}
