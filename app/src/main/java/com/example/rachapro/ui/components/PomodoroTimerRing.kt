package com.example.rachapro.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rachapro.ui.theme.RachaGradientEnd
import com.example.rachapro.ui.theme.RachaGradientStart
import com.example.rachapro.ui.theme.RachaIndigo
import com.example.rachapro.ui.theme.RachaOnSurfaceMuted
import com.example.rachapro.ui.theme.RachaSurface
import kotlin.math.ceil

@Composable
fun PomodoroTimerRing(
    remainingMillis: Long,
    totalMillis: Long,
    modifier: Modifier = Modifier,
    isActive: Boolean = false,
    label: String = "restante"
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
    val timeText = String.format("%02d:%02d", minutes, seconds)

    Box(
        modifier = modifier
            .size(240.dp)
            .semantics { contentDescription = "Temporizador, quedan $minutes minutos y $seconds segundos" },
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(240.dp)) {
            val stroke = 16.dp.toPx()
            val diameter = size.minDimension - stroke
            val topLeft = androidx.compose.ui.geometry.Offset(
                (size.width - diameter) / 2,
                (size.height - diameter) / 2
            )
            val arcSize = androidx.compose.ui.geometry.Size(diameter, diameter)

            drawArc(
                color = RachaIndigo.copy(alpha = 0.10f),
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = stroke, cap = StrokeCap.Round)
            )

            drawArc(
                brush = Brush.sweepGradient(
                    colors = listOf(RachaGradientStart, RachaGradientEnd, RachaGradientStart)
                ),
                startAngle = -90f,
                sweepAngle = 360f * animatedProgress,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = stroke, cap = StrokeCap.Round)
            )
        }

        Box(
            modifier = Modifier
                .size(188.dp)
                .clip(CircleShape)
                .background(RachaSurface),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = timeText,
                    fontSize = 54.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall,
                    color = RachaOnSurfaceMuted
                )
            }
        }
    }
}
