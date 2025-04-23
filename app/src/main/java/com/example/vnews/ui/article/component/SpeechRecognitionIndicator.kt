package com.example.vnews.ui.article.component

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.InfiniteRepeatableSpec
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.unit.dp

@Composable
fun SpeechRecognitionIndicator(
    isListening: Boolean,
    modifier: Modifier = Modifier
) {
    if (!isListening) return

    Row(
        modifier = modifier
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Multiple dots with different animation phases
        PulsatingDot(phase = 0f)
        Spacer(modifier = Modifier.width(4.dp))
        PulsatingDot(phase = 0.2f)
        Spacer(modifier = Modifier.width(4.dp))
        PulsatingDot(phase = 0.4f)
    }
}

@Composable
private fun PulsatingDot(phase: Float) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1f,
        animationSpec = InfiniteRepeatableSpec(
            animation = tween(
                durationMillis = 1000,
                easing = FastOutSlowInEasing,
                delayMillis = (phase * 1000).toInt()
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    Box(
        modifier = Modifier
            .size(8.dp)
            .scale(scale)
            .background(
                color = MaterialTheme.colorScheme.primary,
                shape = CircleShape
            )
    )
} 