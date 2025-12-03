package com.example.katoapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.katoapp.ui.theme.AppTheme

import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

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
                HomeScreen(modifier = Modifier)
            }
        }
    }
}


@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "splash_screen") {
        composable("splash_screen") {
            SplashScreen(navController = navController)
        }
        composable("home_screen") {
            HomeScreen(modifier = Modifier) // Ganti dengan screen utama Anda
        }
    }
}