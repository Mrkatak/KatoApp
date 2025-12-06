package com.example.katoapp

import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
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
import java.security.MessageDigest

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    // Panggil Viewmodel
    private val viewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        //install splashScreen
        val splashScreen = installSplashScreen()

        super.onCreate(savedInstanceState)

        checkAppSignature()



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

    private fun checkAppSignature() {
        try {
            // Gunakan bendera GET_SIGNATURES yang lama (kita Suppress warning-nya)
            @Suppress("DEPRECATION")
            val info = packageManager.getPackageInfo(
                packageName,
                PackageManager.GET_SIGNATURES
            )

            // PERBAIKAN: Cek apakah signatures tidak null sebelum diloop
            val signatures = info.signatures
            if (signatures != null) {
                for (signature in signatures) {
                    val md = MessageDigest.getInstance("SHA-1")
                    md.update(signature.toByteArray())
                    val digest = md.digest()
                    val hexString = StringBuilder()

                    // Format byte ke Hex
                    for (b in digest) {
                        hexString.append(String.format("%02X:", b))
                    }

                    // Tampilkan di Logcat
                    // dropLast(1) untuk membuang titik dua (:) terakhir
                    Log.d("CEK_SHA1", "SHA-1 ASLI: ${hexString.toString().dropLast(1)}")
                }
            }
        } catch (e: Exception) {
            Log.e("CEK_SHA1", "Error mengambil signature", e)
        }
    }
}
