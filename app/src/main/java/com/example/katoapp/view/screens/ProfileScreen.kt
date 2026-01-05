package com.example.katoapp.view.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.unpackInt1
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.katoapp.R
import com.example.katoapp.viewModel.AuthViewModel
import com.example.katoapp.viewModel.DashboardUserViewModel

@Composable
fun ProfileRoute(
    navController: NavController,
    viewModel: DashboardUserViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel()
) {

    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadUserStats()
    }

    ProfileScreen(
        username = uiState.username,
        email = uiState.email,
        sharedCount = uiState.sharedCount,
        savedCount = uiState.savedCount,
        likedCount = 0,
        onHelpClick = {
            //navigasi ke halaman bantuan
        },
        onLogoutClick = {
            authViewModel.logout()
            navController.navigate("LoginScreen") {
                popUpTo("MainUserScreen") { inclusive = true}
            }
        }
    )

}

@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    username: String,
    email: String,
    sharedCount: Int,
    savedCount: Int,
    likedCount: Int = 0,
    onHelpClick: () -> Unit,
    onLogoutClick: () -> Unit
) {

    var showLogoutDialog by remember { mutableStateOf(false) }

    //alert dialog logout
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false},
            title = {
                Text(
                    text = "Konfirmasi Logout" ,
                    style = MaterialTheme.typography.titleMedium
                )
            },
            text = {
                Text(text = "Apakah Anda yakin ingin logout akun?")
            },
            confirmButton = {
                Button(
                    onClick = {
                        showLogoutDialog = false
                        onLogoutClick()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Text(
                        text = "Keluar" ,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showLogoutDialog = false}
                ) {
                    Text(
                        text = "Batal" ,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

        )
    }

    Column(
        modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)

    ) {
        Column(
            modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            //title
            Text(
                modifier = Modifier.padding(bottom = 24.dp),
                text = "Profil",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground,

            )

            //profile image
            Image(
                painter = painterResource(R.drawable.default_profile),
                contentDescription = "default profile",
                modifier = Modifier
                    .size(108.dp)
                    .clip(
                        shape = RoundedCornerShape(100.dp)
                    )
                    .background(
                        color = MaterialTheme.colorScheme.secondary ,
                        shape = CircleShape
                    ),
                contentScale = ContentScale.Crop
            )
            Spacer(Modifier.padding(vertical = 8.dp))

            //username
            Text(
                text = username,
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(Modifier.padding(vertical = 4.dp))

            //email
            Text(
                text = email,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier.padding(bottom = 24.dp))

            //prompt stats
            Row(modifier
                .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
            ){
                ProfileItem(number = likedCount.toString(), label = "Prompt\nDisukai")
                ProfileItem(number = sharedCount.toString(), label = "Prompt\nDibagikan")
                ProfileItem(number = savedCount.toString(), label = "Prompt\nDisiman")
            }

            HorizontalDivider(
                thickness = 1.dp,
                modifier = Modifier
                .padding(vertical = 24.dp),
                color = MaterialTheme.colorScheme.outline
            )

            //button help
            Button(
                onClick = {
                    onHelpClick
                },
                shape = RoundedCornerShape(100.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.onPrimary,
                    contentColor = MaterialTheme.colorScheme.secondary
                ),
                elevation = ButtonDefaults.elevatedButtonElevation(2.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 2.dp)
                    .height(44.dp)

            ) {
                Row(
                    modifier
                        .fillMaxWidth() ,
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_help),
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier.width(8.dp))

                    Text(
                        text = "Bantuan Pengguna",
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
            Spacer(modifier.height(8.dp))

            //button logout
            Button(
                onClick = {
                    showLogoutDialog = true
                },
                shape = RoundedCornerShape(100.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.onPrimary,
                    contentColor = MaterialTheme.colorScheme.secondary
                ),
                elevation = ButtonDefaults.elevatedButtonElevation(2.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 2.dp)
                    .height(44.dp)
            ) {
                Row(
                    modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_logout),
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier.width(8.dp))

                    Text(
                        text = "Logout",
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }





        }



    }

}

@Composable
fun ProfileItem(number: String, label: String) {
    Column(
        modifier = Modifier
            .width(92.dp) // Agar lebar terbagi rata
            .padding(horizontal = 4.dp)
            .height(92.dp) // Tinggi kotak
            .background(
                MaterialTheme.colorScheme.primaryContainer ,
                RoundedCornerShape(18.dp)
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center

    ) {
        Text(
            text = number,
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center,
            lineHeight = 14.sp
        )
    }
}

@Preview
@Composable
private fun View() {
    ProfileScreen(
        onLogoutClick = {},
        onHelpClick = {},
        username = "Pengguna01",
        email = "pengguna01@gmail.com",
        sharedCount = 99,
        savedCount = 99,
        likedCount = 99
    )
    
}