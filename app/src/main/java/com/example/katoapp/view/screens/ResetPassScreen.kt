package com.example.katoapp.view.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.katoapp.R
import com.example.katoapp.viewModel.AuthViewModel
import com.example.katoapp.viewModel.state.AuthUiState


@Composable
fun ResetPassRoute(
    navController: NavController,
    viewModel: AuthViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    //side effects
    LaunchedEffect(uiState.errorMessage, uiState.resetPassSuccess) {
        //if error
        if (uiState.errorMessage != null) {
            Toast.makeText(context, uiState.errorMessage, Toast.LENGTH_SHORT).show()
            viewModel.clearError() //clear error message
        }

        //if success
        if (uiState.resetPassSuccess) {
            Toast.makeText(context, uiState.successMessage, Toast.LENGTH_LONG).show()
            viewModel.resetState() //reset state
            navController.popBackStack()
        }
    }

    ResetPassScreen(
        uiState = uiState,
        onResetPassClick = {email ->
            viewModel.resetPassword(email)
        },
        onLoginClick = {
            viewModel.resetState()
            navController.navigate("LoginScreen")
        }
    )

}


@Composable
fun ResetPassScreen(
    modifier: Modifier = Modifier ,
    uiState: AuthUiState,
    onResetPassClick: (String) -> Unit,
    onLoginClick: () -> Unit
) {
    var email by remember { mutableStateOf("") }



    Box(
        modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 25.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier
                .fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                painter = painterResource(R.drawable.logo_without_text),
                contentDescription = "logo kato",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(85.dp),

                )

            Spacer(modifier.height(24.dp))
            Text(
                text = "Lupa password?" ,
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier.height(4.dp))
            Text(
                text = "Masukkan email Anda,\n" +
                        " kami akan mengirimkan link untuk mereset password.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center
            )

            Spacer(modifier.height(48.dp))
            OutlinedTextField(
                value = email,
                onValueChange = {email = it},
                shape = RoundedCornerShape(4.dp),
                placeholder = {
                    Text(
                        text = "Masukan Email Anda",
                        style = MaterialTheme.typography.bodyLarge)
                },
                leadingIcon = {
                    Icon(
                        painter = painterResource(R.drawable.ic_email),
                        contentDescription = "ic Email"
                    )
                },
                label = {
                    Text(
                        text = "Email",
                        style = MaterialTheme.typography.bodySmall
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.secondary,
                    focusedTextColor = MaterialTheme.colorScheme.primary,
                    unfocusedTextColor = MaterialTheme.colorScheme.secondary,
                    focusedLabelColor = MaterialTheme.colorScheme.primary,
                    unfocusedLabelColor = MaterialTheme.colorScheme.secondary,
                    focusedLeadingIconColor = MaterialTheme.colorScheme.primary,
                    unfocusedLeadingIconColor = MaterialTheme.colorScheme.secondary,
                    unfocusedPlaceholderColor = MaterialTheme.colorScheme.secondary,
                    cursorColor = MaterialTheme.colorScheme.primary
                ),
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next
                ),
                maxLines = 1
            )

            Spacer(modifier.height(24.dp))
            if (uiState.isLoading) {
                CircularProgressIndicator()
            } else {
                Button(
                    onClick = {
                        onResetPassClick(email)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp),
                    shape = RoundedCornerShape(100.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Text(
                        text = "Kirim Link Reset",
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }

            Spacer(modifier.height(8.dp))
            Row(
                modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(
                    onClick = onLoginClick,
                    contentPadding = PaddingValues(4.dp)
                ) {
                    Text(
                        text = "Kembali",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary,
                        textDecoration = TextDecoration.Underline
                    )
                }
            }



        }



    }
}


@Preview
@Composable
private fun View() {
    ResetPassScreen(
        uiState = AuthUiState(),
        onResetPassClick = {},
        onLoginClick = {}
    )

}