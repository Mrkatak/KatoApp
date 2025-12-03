package com.example.katoapp.view.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun HomeScreen(modifier: Modifier) {
    Column(modifier
        .fillMaxSize()
        .padding(top = 48.dp)
        .background(MaterialTheme.colorScheme.background))
    {
        Text(
            text = "Ini Dashboard",
            style = MaterialTheme.typography.headlineMedium
        )

        OutlinedTextField(
            value = "",
            onValueChange = {},
            label = {Text("Masukkan Email")},
            placeholder = {Text("example@gmail.com")},
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = MaterialTheme.colorScheme.secondary,
                focusedBorderColor = MaterialTheme.colorScheme.secondary,
                unfocusedTextColor = MaterialTheme.colorScheme.secondary
            )


        )
    }



}