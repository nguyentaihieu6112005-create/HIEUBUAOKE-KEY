package com.hieubuaoke.app.ui

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hieubuaoke.app.ui.theme.HieuBuaokeColors

@Composable
fun SplashScreen() {
    var progress by remember { mutableFloatStateOf(0f) }
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 1400, easing = FastOutSlowInEasing),
        label = "splash_progress"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        HieuBuaokeColors.Background,
                        HieuBuaokeColors.Background2,
                        HieuBuaokeColors.Background
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "HIEUBUAOKE",
                color = HieuBuaokeColors.NeonGreen,
                fontSize = 28.sp,
                style = MaterialTheme.typography.headlineLarge
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "BỘ CÔNG CỤ ASSET UNITY",
                color = HieuBuaokeColors.TextSecondary,
                fontSize = 14.sp,
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(28.dp))
            LinearProgressIndicator(
                progress = { animatedProgress },
                modifier = Modifier.width(220.dp),
                color = HieuBuaokeColors.Cyan,
                trackColor = HieuBuaokeColors.Card
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "${(animatedProgress * 100).toInt()}%",
                color = HieuBuaokeColors.TextPrimary,
                fontSize = 12.sp
            )
        }
    }

    LaunchedEffect(Unit) {
        progress = 1f
    }
}
