package com.example.katoapp

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.katoapp.view.screens.DashboardUserScreen
import com.example.katoapp.view.screens.LoginScreen
import com.example.katoapp.view.screens.RegisterScreen
import com.example.katoapp.view.screens.ResetPassScreen
import com.example.katoapp.viewModel.AuthViewModel

@Composable
fun Navigation(
    stratDestination: String
) {
    val navController = rememberNavController()
    val viewModel = hiltViewModel<AuthViewModel>()


    //nanti pake scaffold
    NavHost(
        navController = navController,
        startDestination = stratDestination
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