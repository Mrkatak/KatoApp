package com.example.katoapp.view.screens

import android.view.animation.OvershootInterpolator
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import com.example.katoapp.R
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavController) {
    val scale = remember { Animatable(0f) }

    // Animasi dan Navigasi
    LaunchedEffect(key1 = true) {
        // Animasi Logo membesar
        scale.animateTo(
            targetValue = 0.7f,
            animationSpec = tween(
                durationMillis = 800,
                easing = { OvershootInterpolator(4f).getInterpolation(it) }
            )
        )
        // Delay sebentar agar user melihat logo
        delay(1500L)

        // Pindah ke Home dan hapus Splash dari backstack agar tidak bisa kembali
        navController.navigate("home_screen") {
            popUpTo("splash_screen") { inclusive = true }
        }
    }

    // UI Splash Screen
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize().background(Color.White)
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_logo_app),
            contentDescription = "Logo",
            modifier = Modifier.scale(scale.value)
        )
    }
}