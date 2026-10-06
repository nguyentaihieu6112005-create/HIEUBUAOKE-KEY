package com.hieubuaoke.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.hieubuaoke.app.ui.SplashScreen
import com.hieubuaoke.app.ui.home.HomeScreen
import com.hieubuaoke.app.ui.theme.HieuBuaokeTheme
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HieuBuaokeTheme {
                AppRoot()
            }
        }
    }
}

@Composable
fun AppRoot() {
    var showSplash by remember { mutableStateOf(true) }

    LaunchedEffect(showSplash) {
        if (showSplash) {
            delay(1800)
            showSplash = false
        }
    }

    Surface(modifier = Modifier.fillMaxSize()) {
        if (showSplash) {
            SplashScreen()
        } else {
            HomeScreen()
        }
    }
}
