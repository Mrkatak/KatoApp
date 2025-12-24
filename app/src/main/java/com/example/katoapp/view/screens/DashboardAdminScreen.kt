package com.example.katoapp.view.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.katoapp.R
import com.example.katoapp.data.model.Prompt
import com.example.katoapp.view.component.CategoryMapper
import com.example.katoapp.view.component.MainCategoryButton
import com.example.katoapp.view.component.PromptCard
import com.example.katoapp.viewModel.AdminViewModel

@Composable
fun DashboardAdminRoute(
    navController: NavController,
    viewModel: AdminViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    DashboardAdminScreen(
        categories = uiState.categories,
        onPromptClick = { prompt->
            navController.navigate("PromptDetailScreen/$prompt")
        }
    )

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardAdminScreen(
    modifier: Modifier = Modifier,
    categories: List<String> = emptyList(),
    prompts: List<Prompt> = emptyList(),
    onPromptClick: (String) -> Unit = {}
) {

    var selectedCategory by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                modifier = Modifier.padding(top = 16.dp),
                title = {
                    Text(
                        text = "Dashboard Admin" ,
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                },
                actions = {
                    IconButton(
                        onClick = {}
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_logout),
                            contentDescription = "ic logout",
                            tint = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                },

                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )

        }
    ) { innerPadding ->
        Column(
            modifier
                .padding(innerPadding)
                .padding(top = 24.dp)
        ) {
            //tombol laporan dan peninjau
            Row(
                modifier
                    .padding(horizontal = 26.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Button(
                    onClick = {},
                    shape = RoundedCornerShape(100.dp),
                    modifier = Modifier
                        .height(40.dp)
                        .weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    ),
                    elevation = ButtonDefaults.elevatedButtonElevation(2.dp)
                ) {
                    Text(
                        text = "Laporan Prompt" ,
                        style = MaterialTheme.typography.labelLarge ,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }

                Spacer(modifier.width(12.dp))
                Button(
                    onClick = {},
                    shape = RoundedCornerShape(100.dp),
                    modifier = Modifier
                        .height(40.dp)
                        .weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    elevation = ButtonDefaults.elevatedButtonElevation(2.dp)
                ) {
                    Text(
                        text = "Meninjau Prompt" ,
                        style = MaterialTheme.typography.labelLarge ,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }

            Spacer(modifier.height(16.dp))
            Column(
                modifier
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = "Kategori Utama" ,
                    style = MaterialTheme.typography.labelMedium ,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 26.dp),
                    textAlign = TextAlign.Start
                )

                Row(
                    modifier
                        .fillMaxWidth()
                        .padding(horizontal = 26.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    categories.forEach { dbValue ->
                        MainCategoryButton(
                            text = CategoryMapper.getDisplayName(dbValue.toString()),
                            icon = CategoryMapper.getIcon(dbValue.toString()),
                            isSelected = selectedCategory,
                            onClick = {
                                //function filter prompt
                            }
                        )
                    }
                }
            }

            Spacer(modifier.height(24.dp))
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(prompts) { prompt->
                    PromptCard(
                        title = prompt.title,
                        imageUrl = prompt.imageUrl,
                        category = prompt.category,
                        rating = prompt.rating,
                        onClick = {
                            onPromptClick(prompt.id)
                        }
                    )
                }
            }
        }

    }

}


@Preview
@Composable
private fun View() {
    DashboardAdminScreen(
        categories = listOf("Teks", "Gambar", "Video", "Suara")
    )


}