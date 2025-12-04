package com.example.katoapp

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.katoapp.view.screens.DashboardUserScreen
import com.example.katoapp.view.screens.LoginScreen
import com.example.katoapp.view.screens.RegisterScreen
import com.example.katoapp.view.screens.ResetPassScreen
import com.example.katoapp.viewModel.AuthViewModel

@Composable
fun Navigation() {
    val navController = rememberNavController()
    val viewModel = hiltViewModel<AuthViewModel>()

    //nanti pake scaffold
    NavHost(
        navController = navController,
        startDestination = "LoginScreen"
    ) {
        composable( "LoginScreen") { LoginScreen(
                navController = navController,
                viewModel = viewModel
        ) }

        composable( "RegisterScreen") { RegisterScreen(
            navController = navController,
            viewModel = viewModel
        ) }

        composable("ResetPassScreen") { ResetPassScreen(
            navController = navController,
            viewModel = viewModel
        ) }

        composable("DashboardUserScreen") { DashboardUserScreen(
            navController = navController,
            viewModel = viewModel
        ) }
    }
}