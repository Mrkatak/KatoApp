package com.example.katoapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.example.katoapp.ui.theme.AppTheme
import dagger.hilt.android.AndroidEntryPoint

import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Opsional: Tahan Splash Screen lebih lama jika sedang memuat data
        var isKeepSplash = true
        splashScreen.setKeepOnScreenCondition { isKeepSplash }

        // Simulasi loading data (misal check login status)
        lifecycleScope.launch {
            delay(2000) // Tahan selama 2 detik
            isKeepSplash = false
        }
        setContent {
            AppTheme {
                Navigation()
            }
        }
    }
}
