package com.example.katoapp.view.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.zIndex
import com.example.katoapp.data.model.Prompt
import com.example.katoapp.view.component.CategoryMapper
import com.example.katoapp.view.component.GeneralCategoryButton
import com.example.katoapp.view.component.MainCategoryButton
import com.example.katoapp.view.component.PromptCard
import com.example.katoapp.view.component.SearchBar
import com.example.katoapp.viewModel.SharingPromptViewModel
import com.example.katoapp.R

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SharingPromptRoute(
    navController: NavController,
    viewModel: SharingPromptViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    SharingPromptScreen(
        searchQuery = uiState.searchQuery,
        // data list
        categories = uiState.categories,
        generalCategories = uiState.generalCategories,
        //data seleksi
        selectedMainCategory = uiState.selectedMainCategory,
        selectedCategories = uiState.selectedCategories,

        promptList = uiState.prompts,
        isLoading = uiState.isLoading,
        suggestions = uiState.suggestions,
        onQueryChange = { viewModel.onQueryChange(it) },

        onSuggestionClick = { suggestion ->
            viewModel.onSuggestionClick(suggestion)
             val cleanQuery = suggestion.trim()
             val searchParam = "SEARCH_NATURAL:$cleanQuery"
             navController.navigate("PromptSearchResultScreen/$searchParam")
        },

        onMainCategoryClick = { dbValue ->
            viewModel.setMainCategory(dbValue)
        },
        onCategoryToggle = { category ->
            viewModel.toggleCategory(category)
        },
        //search
        onSearchClicked = { query ->
            if (query.isNotBlank()) {
                val cleanQuery = query.trim()
                val searchParam = "SEARCH_NATURAL:$cleanQuery"
                navController.navigate("PromptSearchResultScreen/$searchParam")
            }
        },

        //navigasi
        onPromptClick = { id -> navController.navigate("PromptDetailScreen/$id") }
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SharingPromptScreen(
    searchQuery: String,
    suggestions: List<String>,
    categories: List<String>,
    generalCategories: List<String>,
    selectedMainCategory: String,
    selectedCategories: List<String>,
    promptList: List<Prompt>,
    isLoading: Boolean,
    onQueryChange: (String) -> Unit,
    onSearchClicked: (String) -> Unit,
    onSuggestionClick: (String) -> Unit,
    onMainCategoryClick: (String) -> Unit,
    onCategoryToggle: (String) -> Unit,
    onPromptClick: (String) -> Unit,
    modifier: Modifier = Modifier

) {

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.background),
        horizontalAlignment = Alignment.Start
    ) {
        Spacer(modifier = Modifier.height(36.dp))

        //search bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 26.dp)
                .zIndex(1f)
        ) {
            Column {
                SearchBar(
                    query = searchQuery,
                    onQueryChange = onQueryChange,
                    onSearchClicked = { onSearchClicked(searchQuery) },
                    modifier = Modifier.fillMaxWidth()
                )

                //suggestion list
                if (suggestions.isNotEmpty() && searchQuery.isNotEmpty()) {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp),
                        shape = RoundedCornerShape(12.dp),
                        shadowElevation = 4.dp,
                        color = MaterialTheme.colorScheme.onPrimary
                    ) {
                        Column {
                            suggestions.forEach { suggestion ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { onSuggestionClick(suggestion) }
                                        .padding(horizontal = 16.dp, vertical = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        painter = painterResource(R.drawable.ic_search) ,
                                        contentDescription = null,
                                        tint = Color.Gray,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(
                                        text = suggestion,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }

                                if (suggestion != suggestions.last()) {
                                    HorizontalDivider(
                                        thickness = 0.5.dp ,
                                        color = Color.LightGray.copy(alpha = 0.5f)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier.height(16.dp))
        Column(
            modifier
                .fillMaxSize()
//                .verticalScroll(rememberScrollState())
        ) {
            //main category button
            Column(
                modifier
                    .fillMaxWidth()
                    .padding(start = 26.dp),
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
                            isSelected = (selectedMainCategory == dbValue),
                            onClick = { onMainCategoryClick(dbValue)}
                        )

                    }
                }
            }
            Spacer(modifier.height(16.dp))

            //general category chip button
            Column(
                modifier
                    .fillMaxWidth()
                    .padding(horizontal = 26.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Kategori Umum",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )

                if (isLoading && generalCategories.isEmpty()) {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp))
                    }
                } else {
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        generalCategories.forEach { category ->
                            val isSelected = selectedCategories.contains(category)

                            GeneralCategoryButton(
                                text = category,
                                isSelected = isSelected,
                                onClick = { onCategoryToggle(category) }
                            )
                        }
                    }
                }
            }
            Spacer(modifier.height(16.dp))

            //list prompt terbaru
            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (promptList.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Belum ada prompt.", color = Color.Gray)
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(start = 26.dp, end = 26.dp, bottom = 16.dp, top = 16.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(promptList) { prompt ->
                        PromptCard(
                            title = prompt.title,
                            imageUrl = prompt.imageUrl,
                            category = CategoryMapper.getDisplayName(prompt.category),
                            rating = prompt.rating.ifEmpty { "New" },
                            onClick = { onPromptClick(prompt.id) },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

        }


    }
}


val dummyPromptsForPreview = listOf(
    Prompt(
        id = "1",
        title = "Pemandangan Cyberpunk",
        category = "Text to Image",
        rating = "4.8",
        imageUrl = "",
        usageCount = 120
    ),
    Prompt(
        id = "2",
        title = "Logo Kelompok Penerbang Roket",
        category = "Text to Image",
        rating = "4.5",
        imageUrl = "",
        usageCount = 85
    ),
    Prompt(
        id = "3",
        title = "Cara Menebang Hutan",
        category = "Text to Text",
        rating = "5.0",
        imageUrl = "",
        usageCount = 200
    )
)

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun View() {
    SharingPromptScreen(
        searchQuery = "Logo",
        categories = listOf("Text to Text", "Text to Image", "Text to Video", "Text to Speech"),
        generalCategories = listOf("Bisnis", "Hiburan", "Pendidikan", "Teknologi", "Seni"),
        selectedMainCategory = "Text to Image",
        selectedCategories = listOf("Bisnis"),
        isLoading = false,
        onQueryChange = {},
        onSearchClicked = {},
        onMainCategoryClick = {},
        onCategoryToggle = {},
        onPromptClick = {},
        promptList = dummyPromptsForPreview,
        suggestions = emptyList(),
        onSuggestionClick = {}
    )
}

