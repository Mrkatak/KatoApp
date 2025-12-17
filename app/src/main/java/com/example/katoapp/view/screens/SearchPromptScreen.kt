package com.example.katoapp.view.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.example.katoapp.viewModel.SearchPromptViewModel

@Composable
fun SearchPromptRoute(
    navController: NavController,
    viewModel: SearchPromptViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    SearchPromptScreen()
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SearchPromptScreen(
    modifier: Modifier = Modifier
) {
    Box(
        modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Search Screen",
            fontSize = 34.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Preview
@Composable
private fun View() {
    SearchPromptScreen()

}