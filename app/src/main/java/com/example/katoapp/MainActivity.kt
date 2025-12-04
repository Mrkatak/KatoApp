package com.example.katoapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.example.katoapp.ui.theme.AppTheme
import com.example.katoapp.viewModel.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint

import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    // Panggil Viewmodel
    private val viewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        //install splashScreen
        val splashScreen = installSplashScreen()

        super.onCreate(savedInstanceState)

        // Cek sesi Login
        val startScreen = if (viewModel.isUserLoggedIn()) {
            "MainUserScreen"
        } else {
            "LoginScreen"
        }

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
                Navigation(startDestination = startScreen)
            }
        }
    }
}
