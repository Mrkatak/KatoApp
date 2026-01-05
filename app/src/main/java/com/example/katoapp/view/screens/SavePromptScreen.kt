package com.example.katoapp.view.screens

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.katoapp.R
import com.example.katoapp.view.component.CategoryMapper
import com.example.katoapp.view.component.PromptCard
import com.example.katoapp.viewModel.SavePromptViewModel
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.navigation.NavController
import com.example.katoapp.data.model.Prompt
import com.example.katoapp.viewModel.state.SavePromptUiState

@Composable
fun SavePromptRoute(
    navController: NavController ,
    viewModel: SavePromptViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    SavePromptScreen(
        uiState = uiState,
        onFilterChanged = { filter ->
            viewModel.onFilterChanged(filter)
        },
        onRefresh = {
            viewModel.onRefresh()
        },
        // Event Klik Card -> Navigasi ke Detail
        onPromptClick = { promptId ->
            // Navigasi membawa ID Prompt sebagai argumen
            navController.navigate("PromptDetailScreen/$promptId")
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavePromptScreen(
    modifier: Modifier = Modifier,
    uiState: SavePromptUiState,
    onRefresh: () -> Unit,
    onFilterChanged: (String) -> Unit,
    onPromptClick: (String) -> Unit
) {

    Box(
        modifier
            .background(color = MaterialTheme.colorScheme.background)
            .fillMaxSize()
    ) {
        Column(
            modifier
                .fillMaxSize()
                .padding(top = 36.dp)
                .padding(horizontal = 26.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            //button private, sharing, simpan
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                FilterButton(
                    text = "Private",
                    icon = R.drawable.ic_teks,
                    isSelected = uiState.selectedFilter == "Private",
                    onClick = { onFilterChanged("Private") }
                )

                FilterButton(
                    text = "Sharing",
                    icon = R.drawable.ic_teks,
                    isSelected = uiState.selectedFilter == "Sharing",
                    onClick = { onFilterChanged("Sharing") }
                )

                FilterButton(
                    text = "Simpan",
                    icon = R.drawable.ic_teks,
                    isSelected = uiState.selectedFilter == "Simpan",
                    onClick = { onFilterChanged("Simpan") }
                )
            }
            Spacer(modifier = Modifier.height(24.dp))

            //swipe to refresh
            PullToRefreshBox(
                isRefreshing = uiState.isRefreshing,
                onRefresh = { onRefresh() },
                modifier = Modifier.weight(1f)
            ) {
                if (uiState.isLoading && !uiState.isRefreshing) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                } else if (uiState.prompts.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState()),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Belum ada prompt ${uiState.selectedFilter}\nTarik layar untuk menyegarkan",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.secondary,
                            textAlign = TextAlign.Center
                        )
                    }
                } else {

                    //prompt list
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(bottom = 16.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(uiState.prompts) { prompt ->
                            val displayCategory = CategoryMapper.getDisplayName(prompt.category)
                            PromptCard(
                                title = prompt.title,
                                imageUrl = prompt.imageUrl,
                                category = displayCategory,
                                rating = if (prompt.rating.isEmpty()) "New" else prompt.rating,
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

@Composable
fun FilterButton(
    text: String,
    icon: Int,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val containerColor = if (isSelected) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.onPrimary
    val contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimary
                            else MaterialTheme.colorScheme.secondary

    Button(
        onClick = onClick,
        modifier = Modifier
            .height(44.dp)
            .wrapContentWidth()
            .padding(vertical = 2.dp),
        contentPadding = PaddingValues(horizontal = 16.dp),
        elevation = ButtonDefaults.elevatedButtonElevation(2.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor
        )
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                painter = painterResource(icon),
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}



@Preview
@Composable
private fun View() {
    val dummyPrompts = listOf(
        Prompt(title = "Logo Kelompok Penerbang Roket" , category = "Text to Image" , rating = "4.5") ,
        Prompt(title = "Tutorial Menebang Pohon", category = "Text to Text", rating = "5.0"),
        Prompt(title = "Video Makan Bergizi Gratis", category = "Text to Video", rating = "New"),
    )

    val dummyState = SavePromptUiState(
        isLoading = false,
        selectedFilter = "Private",
        prompts = dummyPrompts
    )
    
    SavePromptScreen(
        uiState = dummyState,
        onFilterChanged = {},
        onRefresh = { },
        onPromptClick = { }
    )

}
