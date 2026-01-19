package com.example.katoapp

import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.katoapp.ui.theme.AppTheme
import com.example.katoapp.viewModel.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.security.MessageDigest

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    // Panggil Viewmodel
    private val viewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        //install splashScreen
        val splashScreen = installSplashScreen()

        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        viewModel.checkUserSession()
        splashScreen.setKeepOnScreenCondition {
            viewModel.startDestination.value == null
        }


        setContent {
            AppTheme {
                val startScreen by viewModel.startDestination.collectAsState()
                if (startScreen != null) {
                    Navigation(startDestination = startScreen!!)
                }

            }
        }

    }

}
