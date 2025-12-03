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
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ModifierLocalBeyondBoundsLayout
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.katoapp.R
import com.example.katoapp.viewModel.AuthViewModel

@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    viewModel: AuthViewModel
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("")}

    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    //side effects
    LaunchedEffect(uiState.errorMessage , uiState.loginSuccess) {
        //if error
        if (uiState.errorMessage != null) {
            Toast.makeText(context , uiState.errorMessage , Toast.LENGTH_SHORT).show()
            viewModel.clearError() //clear error message
        }
        //if success
        if (uiState.loginSuccess) {
            Toast.makeText(context , uiState.successMessage , Toast.LENGTH_SHORT).show()
            navController.navigate("DashboardUserScreen") {
                popUpTo("LoginScreen") { inclusive = true }
            }
            viewModel.resetState() //reset state
        }
    }

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
                text = "Selamat Datang!" ,
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier.height(4.dp))
            Text(
                text = "Masuk ke akun anda",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground
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
                        painter = painterResource(R.drawable.ic_outlined_email),
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

            Spacer(modifier.height(16.dp))
            OutlinedTextField(
                value = password,
                onValueChange = {password  =it},
                shape = RoundedCornerShape(4.dp),
                placeholder = {
                    Text(
                        text = "Masukan Password Anda" ,
                        style = MaterialTheme.typography.bodyLarge
                    )},
                leadingIcon = {
                    Icon(
                        painter = painterResource(R.drawable.ic_outlined_password) ,
                        contentDescription = "ic pass"
                    )
                },
                label = {
                    Text(
                        text = "Password" ,
                        style = MaterialTheme.typography.bodySmall
                    )},
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
                    imeAction = ImeAction.Done
                ),
                maxLines = 1
            )

            Spacer(modifier.height(8.dp))
            Row(
                modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(
                    onClick = {
                        viewModel.resetState()
                        navController.navigate("ResetPassScreen")
                    }
                ) {
                    Text(
                        text = "Lupa Password" ,
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier.height(8.dp))

            if (uiState.isLoading) {
                CircularProgressIndicator()
            } else {
                Button(
                    onClick = {
                    viewModel.login(email, password)
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
                        text = "Masuk",
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
                Text(
                    text = "Belum memiliki akun?",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.outline
                )
                TextButton(
                    onClick = {
                        viewModel.resetState()
                        navController.navigate("RegisterScreen")
                    },
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text(
                        text = "Daftar",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary,
                        textDecoration = TextDecoration.Underline
                    )
                }
            }

            Spacer(Modifier.height(8.dp))
            Row(
                modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                HorizontalDivider(
                    thickness = 1.dp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .weight(1f)
                )

                Text(
                    text = "Atau",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )

                HorizontalDivider(
                    thickness = 1.dp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .weight(1f)
                )
            }

            Spacer(modifier.height(16.dp))
            Button(
                onClick = {
                    //login google
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                ),
                modifier = Modifier
                    .height(44.dp)
                    .wrapContentWidth()
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_outlined_google),
                    contentDescription = "ic google",
                    tint = MaterialTheme.colorScheme.onSecondaryContainer
                )

                Text(
                    text = "Masuk dengan Google",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    modifier = Modifier
                        .padding(start = 8.dp)
                )
            }





        }



    }

}

//@Preview
//@Composable
//private fun View() {
//    val navController = rememberNavController()
//    LoginScreen(navController = navController)
//}