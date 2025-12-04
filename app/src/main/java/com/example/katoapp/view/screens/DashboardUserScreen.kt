package com.example.katoapp.view.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.katoapp.viewModel.AuthViewModel

@Composable
fun DashboardUserScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    viewModel: AuthViewModel
) {
    Column(
        modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Selamat Datang di KATO!" ,
            style = MaterialTheme.typography.headlineLarge
        )

        Spacer(modifier.height(32.dp))

        Button(
            onClick = {
                viewModel.logout()
                navController.navigate("LoginScreen") {
                    popUpTo("DashboardUserScreen") { inclusive = true}
                }
            }
        ) {
            Text("Logout")
        }
    }
}