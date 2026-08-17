package com.example.rachapro.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rachapro.ui.theme.RachaGradientEnd
import com.example.rachapro.ui.theme.RachaGradientStart
import com.example.rachapro.ui.theme.RachaIndigo
import kotlin.math.ceil

@Composable
fun PomodoroTimerRing(
    remainingMillis: Long,
    totalMillis: Long,
    modifier: Modifier = Modifier,
    isActive: Boolean = false
) {
    val progress =
        if (totalMillis > 0) {
            remainingMillis.toFloat() / totalMillis.toFloat()
        } else {
            1f
        }

    val animatedProgress by animateFloatAsState(
        targetValue = progress.coerceIn(0f, 1f),
        animationSpec = tween(
            durationMillis = if (isActive) 900 else 400,
            easing = FastOutSlowInEasing
        ),
        label = "timerProgress"
    )

    val totalSeconds =
        ceil(remainingMillis / 1000.0)
            .toLong()
            .coerceAtLeast(0L)

    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60

    Box(
        modifier = modifier.size(240.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(240.dp)) {
            val stroke = 14.dp.toPx()

            drawArc(
                color = RachaIndigo.copy(alpha = 0.12f),
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                style = Stroke(width = stroke, cap = StrokeCap.Round)
            )

            drawArc(
                brush = androidx.compose.ui.graphics.Brush.sweepGradient(
                    colors = listOf(RachaGradientStart, RachaGradientEnd)
                ),
                startAngle = -90f,
                sweepAngle = 360f * animatedProgress,
                useCenter = false,
                style = Stroke(width = stroke, cap = StrokeCap.Round)
            )
        }

        Text(
            text = String.format("%02d:%02d", minutes, seconds),
            fontSize = 52.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}
