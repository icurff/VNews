    package com.example.vnews.ui.article.component
    
    import androidx.compose.foundation.background
    import androidx.compose.foundation.layout.Arrangement
    import androidx.compose.foundation.layout.Box
    import androidx.compose.foundation.layout.Column
    import androidx.compose.foundation.layout.Row
    import androidx.compose.foundation.layout.Spacer
    import androidx.compose.foundation.layout.fillMaxWidth
    import androidx.compose.foundation.layout.height
    import androidx.compose.foundation.layout.padding
    import androidx.compose.foundation.layout.width
    import androidx.compose.foundation.shape.RoundedCornerShape
    import androidx.compose.material.icons.Icons
    import androidx.compose.material.icons.filled.PlayArrow
    import androidx.compose.material3.Button
    import androidx.compose.material3.Icon
    import androidx.compose.material3.MaterialTheme
    import androidx.compose.material3.Slider
    import androidx.compose.material3.Text
    import androidx.compose.runtime.Composable
    import androidx.compose.runtime.getValue
    import androidx.compose.runtime.mutableFloatStateOf
    import androidx.compose.runtime.remember
    import androidx.compose.runtime.setValue
    import androidx.compose.ui.Alignment
    import androidx.compose.ui.Modifier
    import androidx.compose.ui.draw.clip
    import androidx.compose.ui.text.font.FontWeight
    import androidx.compose.ui.unit.dp
    
    @Composable
    fun SpeedControlSheet(
        currentSpeed: Float,
        minSpeed: Float = 0.5f,
        maxSpeed: Float = 2f,
        step: Float = 0.25f,
        onSpeedChange: (Float) -> Unit,
        onDismiss: () -> Unit,
        onResume: () -> Unit
    ) {
        var sliderPosition by remember { mutableFloatStateOf(currentSpeed) }
    
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Box(
                modifier = Modifier
                    .width(32.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f))
                    .align(Alignment.CenterHorizontally)
            )
    
            Spacer(modifier = Modifier.height(16.dp))
    
            // Title and value
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Playback Speed",
                    style = MaterialTheme.typography.titleMedium
                )
    
                Text(
                    text = "${sliderPosition}x",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.primary
                )
            }
    
            // Slider
            Slider(
                value = sliderPosition,
                onValueChange = { sliderPosition = it },
                valueRange = minSpeed..maxSpeed,
                steps = ((maxSpeed - minSpeed) / step).toInt() - 1,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )
    
            // Speed labels
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${minSpeed}x",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "${maxSpeed}x",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
    
            Spacer(modifier = Modifier.height(16.dp))
    
            // Action buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Apply and resume button
                Button(
                    onClick = {
                        onSpeedChange(sliderPosition)
                        onDismiss()
                        onResume()
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = null,
                        modifier = Modifier.padding(end = 4.dp)
                    )
                    Text("Apply & Resume")
                }
            }
        }
    } 