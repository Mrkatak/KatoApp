package com.example.katoapp.view.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.ModifierLocalBeyondBoundsLayout
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.FontScaling
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.katoapp.R
import com.example.katoapp.view.component.Carousel
import com.example.katoapp.view.component.SearchBar
import com.example.katoapp.viewModel.AuthViewModel
import com.example.katoapp.viewModel.DashboardUserViewModel

@Composable
fun DashboardUserRoot(
    viewModel: DashboardUserViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val username = if (uiState.isLoading) "Loading.."
                    else uiState.username

    DashboardUserScreen(
        username = username,
        onSearchClicked = {
            //function search
        }
    )
}

@Composable
fun DashboardUserScreen(
    modifier: Modifier = Modifier,
    username: String,
    onSearchClicked: (String) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }

    Column(
        modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.background),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Row(
            modifier
                .fillMaxWidth()
                .padding(top = 36.dp)
                .padding(start = 26.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Image(
                painter = painterResource(R.drawable.default_profile),
                contentDescription = "default profile",
                modifier = Modifier
                    .size(42.dp)
                    .clip(
                        shape = RoundedCornerShape(100.dp)
                    )
                    .background(
                        color = MaterialTheme.colorScheme.secondary ,
                        shape = CircleShape
                    ),
                contentScale = ContentScale.Crop
            )

            Text(
                text = "Hallo,\n" +
                        "${username}!" ,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.secondary
            )
        }

        Spacer(modifier.height(16.dp))
        SearchBar(
            modifier = Modifier.padding(horizontal = 26.dp),
            query = searchQuery,
            onQueryChange = { newText ->
                searchQuery = newText
            },
            onSearchClicked = {
                onSearchClicked(searchQuery)
            }
        )

        Spacer(modifier.height(16.dp))
        Carousel()

        Column(
            modifier
                .fillMaxWidth()
                .padding(horizontal = 26.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Tambah Prompt",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onBackground
            )

            Button(
                onClick = {

                },
                modifier = Modifier
                    .height(44.dp)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(100.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Row(
                    modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_teks),
                        contentDescription = "ic text",
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier.width(8.dp))
                    Text(
                        text = "Klik untuk menambahkan prompt" ,
                        style = MaterialTheme.typography.labelLarge ,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        }





    }
}


@Preview
@Composable
private fun View() {
    DashboardUserScreen(
        username = "Pengguna01",
        onSearchClicked = {}
    )

}