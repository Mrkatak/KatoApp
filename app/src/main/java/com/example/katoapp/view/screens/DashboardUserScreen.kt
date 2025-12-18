package com.example.katoapp.view.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.example.katoapp.R
import com.example.katoapp.data.model.Prompt
import com.example.katoapp.view.component.Carousel
import com.example.katoapp.view.component.CategoryMapper
import com.example.katoapp.view.component.MainCategoryButton
import com.example.katoapp.view.component.PromptCard
import com.example.katoapp.view.component.SearchBar
import com.example.katoapp.viewModel.DashboardUserViewModel
import com.example.katoapp.viewModel.PromptOrgViewModel

@Composable
fun DashboardUserRoute(
    navController: NavController,
    dashboardNavController: NavController,
    viewModel: DashboardUserViewModel = hiltViewModel(),
    promptOrgViewModel: PromptOrgViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val promptUiState by promptOrgViewModel.uiState.collectAsState()
    val username = if (uiState.isLoading) "Loading.."
                    else uiState.username

    DashboardUserScreen(
        username = username,
        topRatedPrompts = uiState.topRatedPrompts,
        categories = promptUiState.categories,
        popularPrompts = uiState.popularPrompts,
        onSearchClicked = {
            dashboardNavController.navigate("sharing") {
                popUpTo(dashboardNavController.graph.findStartDestination().id){
                    saveState = true
                }
                launchSingleTop = true
                restoreState = true
            }
        },
        onAddPromptClick = {
            navController.navigate("AddPromptScreen")
        },
        onCategoryClick = { categoryDbValue ->
            navController.navigate("PromptSearchResultScreen/$categoryDbValue")
        },
        onPromptClick = { promptId ->
            navController.navigate("PromptDetailScreen/$promptId")
        }

    )
}


@Composable
fun DashboardUserScreen(
    modifier: Modifier = Modifier ,
    username: String ,
    categories: List<String> ,
    popularPrompts: List<Prompt> ,
    topRatedPrompts: List<Prompt> ,
    onSearchClicked: (String) -> Unit ,
    onAddPromptClick: () -> Unit ,
    onCategoryClick: (String) -> Unit,
    onPromptClick: (String) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember {mutableStateOf("")}

    Column(
        modifier
            .fillMaxSize()
    ) {
        Column(
            modifier
                .fillMaxWidth()
                .background(color = MaterialTheme.colorScheme.background)
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
                modifier = Modifier
                    .padding(horizontal = 26.dp),
                query = searchQuery,
                onQueryChange = { },
                onSearchClicked = { },
                readOnly = true,
                onClick = {
                    onSearchClicked("")
                }
            )
        }


        Spacer(modifier.height(16.dp))
        Column(
            modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .background(color = MaterialTheme.colorScheme.background),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
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
                        onAddPromptClick()
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

            Spacer(modifier.height(16.dp))
            Column(
                modifier
                    .fillMaxWidth()
                    .padding(horizontal = 26.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Kategori Utama",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    categories.forEach { dbValue ->
                        MainCategoryButton(
                            text = CategoryMapper.getDisplayName(dbValue),
                            icon = CategoryMapper.getIcon(dbValue),
                            isSelected = (selectedCategory == dbValue),
                            onClick = {
                                selectedCategory = dbValue
                                onCategoryClick(dbValue)
                            }
                        )

                    }
                }
            }

            Spacer(modifier.height(16.dp))
            Column(
                modifier
                    .fillMaxWidth()
                    .padding(horizontal = 26.dp)
            ) {
                Row(
                    modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Absolute.SpaceBetween
                ) {
                    Text(
                        text = "Prompt Populer",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    TextButton(
                        onClick = {
                            // navigasi ke hasil pencarian
                        },
                        contentPadding = PaddingValues(vertical =0.dp),
                        modifier = Modifier.height(16.dp)
                    ) {
                        Text(
                            text = "Lainnya",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }

                Spacer(modifier.height(8.dp))
                if (popularPrompts.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Belum ada data populer.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                    }
                } else {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        items(popularPrompts) { prompt ->
                            val displayCategory = CategoryMapper.getDisplayName(prompt.category)
                            PromptCard(
                                title = prompt.title,
                                imageUrl = prompt.imageUrl,
                                category = displayCategory,
                                rating = if (prompt.rating.isEmpty()) "New"
                                            else prompt.rating,
                                onClick = {
                                    onPromptClick(prompt.id)
                                }
                            )
                        }
                    }
                }

            }

            Spacer(modifier.height(16.dp))
            Column(
                modifier
                    .fillMaxWidth()
                    .padding(horizontal = 26.dp)
            ) {
                Row(
                    modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Absolute.SpaceBetween
                ) {
                    Text(
                        text = "Rating Tertinggi",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    TextButton(
                        onClick = {
                            // navigasi ke hasil pencarian
                        },
                        contentPadding = PaddingValues(vertical =0.dp),
                        modifier = Modifier.height(16.dp)
                    ) {
                        Text(
                            text = "Lainnya",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }

                Spacer(modifier.height(8.dp))
                if (popularPrompts.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Belum ada data top Rated Prompt",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                    }
                } else {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        items(topRatedPrompts) { prompt ->
                            val displayCategory = CategoryMapper.getDisplayName(prompt.category)
                            PromptCard(
                                title = prompt.title,
                                imageUrl = prompt.imageUrl,
                                category = displayCategory,
                                rating = if (prompt.rating.isEmpty()) "New"
                                else prompt.rating,
                                onClick = {
                                    onPromptClick(prompt.id)
                                }
                            )
                        }
                    }
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
        onSearchClicked = {},
        onAddPromptClick = {},
        categories = listOf("Teks", "Gambar", "Video", "Suara"),
        popularPrompts = emptyList(),
        topRatedPrompts = emptyList(),
        onPromptClick = {},
        onCategoryClick = {}
    )

}