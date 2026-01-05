package com.example.katoapp.view.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.katoapp.R
import com.example.katoapp.data.model.Prompt
import com.example.katoapp.view.component.CategoryMapper
import com.example.katoapp.view.component.PromptCard
import com.example.katoapp.view.component.SearchBar
import com.example.katoapp.viewModel.SearchPromptViewModel

@Composable
fun PromptSearchResultRoute(
    navController: NavController ,
    categoryName: String,
    viewModel: SearchPromptViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(categoryName) {
        viewModel.searchByCategory(categoryName)
    }

    //display title
    val displayTitle = remember(categoryName) {
        when {
            categoryName.startsWith("SEARCH_NATURAL:") -> {
                val query = categoryName.removePrefix("SEARCH_NATURAL:").lowercase()
                //display title sesuai query
                if (query.contains("gambar") || query.contains("image")) "Gambar"
                else if (query.contains("video") || query.contains("film")) "Video"
                else if (query.contains("teks") || query.contains("text")) "Teks"
                else if (query.contains("suara") || query.contains("audio")) "Suara"
                else "Hasil Pencarian"
            }
            categoryName == "Popular" -> "Prompt Populer"
            categoryName == "Rating" -> "Rating Tertinggi"

            else -> CategoryMapper.getDisplayName(categoryName)
        }
    }

    PromptSearchResultScreen(
        categoryTitle = displayTitle,
        searchQuery = uiState.searchQuery,
        suggestions = uiState.suggestions,
        isLoading = uiState.isLoading,
        prompts = uiState.searchResults,
        onQueryChange = {
            viewModel.onQueryChange(it)
        },
        onSuggestionClick = { suggestion ->
            viewModel.onSuggestionClick(suggestion)
            val searchParam = "SEARCH_NATURAL:${suggestion.trim()}"
            navController.navigate("PromptSearchResultScreen/$searchParam") {
                popUpTo("PromptSearchResultScreen/{categoryName}") { inclusive = false }
            }
        },
        onSearchClicked = { query ->
            if (query.isNotBlank()) {
                val cleanQuery = query.trim()
                val searchParam = "SEARCH_NATURAL:$cleanQuery"
                //nav ke halaman sendiri utk refresh
                navController.navigate("PromptSearchResultScreen/$searchParam") {
                    popUpTo("PromptSearchResultScreen/{categoryName}") { inclusive = false }
                }
            }
        },
        onPromptClick = { promptId ->
            navController.navigate("PromptDetailScreen/$promptId")
        }
    )

}




@Composable
fun PromptSearchResultScreen(
    modifier: Modifier = Modifier ,
    categoryTitle: String ,
    searchQuery : String ,
    suggestions: List<String>,
    isLoading: Boolean ,
    prompts: List<Prompt>,
    onQueryChange : (String) -> Unit ,
    onSuggestionClick : (String) -> Unit,
    onSearchClicked : (String) -> Unit,
    onPromptClick: (String) -> Unit
) {
    Column(
        modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.background),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(modifier.height(72.dp))

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
                                        thickness = 0.5.dp,
                                        color = Color.LightGray.copy(alpha = 0.5f))
                                }
                            }
                        }
                    }
                }
            }
        }
        Spacer(modifier.height(16.dp))

        //category title
        Surface(
            modifier
                .height(61.dp)
                .fillMaxWidth()
                .padding(horizontal = 26.dp),
            color = MaterialTheme.colorScheme.onPrimary,
            shape = RoundedCornerShape(10.dp),
            shadowElevation = 2.dp

        ) {
            Box(
                modifier
                    .fillMaxSize()
                    .padding(horizontal = 26.dp)
                    .background(color = MaterialTheme.colorScheme.onPrimary),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = categoryTitle,
                    style = MaterialTheme.typography.headlineSmall ,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
        Spacer(modifier.height(24.dp))

        //prompt card
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (prompts.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Tidak ada prompt dengan kategori ini",
                    color = Color.Gray,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(start = 26.dp, end = 26.dp, bottom = 60.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(prompts) { prompt ->
                    val displayCategory = CategoryMapper.getDisplayName(prompt.category)
                    PromptCard(
                        title = prompt.title,
                        imageUrl = prompt.imageUrl,
                        category = displayCategory,
                        rating = if (prompt.rating.isEmpty()) "New" else prompt.rating,
                        onClick = { onPromptClick(prompt.id) }
                    )
                }
            }
        }




    }




}


@Preview
@Composable
private fun View() {
    PromptSearchResultScreen(
        searchQuery = "Kucing lucu..",
        onQueryChange = {},
        onSearchClicked = {},
        isLoading = false,
        prompts = listOf(),
        onPromptClick = {},
        categoryTitle = "Kategori Utama",
        suggestions = emptyList(),
        onSuggestionClick = {}
    )

}