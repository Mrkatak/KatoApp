package com.example.katoapp.view.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.katoapp.data.model.Prompt
import com.example.katoapp.view.component.CategoryMapper
import com.example.katoapp.view.component.GeneralCategoryButton
import com.example.katoapp.view.component.MainCategoryButton
import com.example.katoapp.view.component.PromptCard
import com.example.katoapp.view.component.SearchBar
import com.example.katoapp.viewModel.PromptOrgViewModel
import com.example.katoapp.viewModel.SearchPromptViewModel
import com.example.katoapp.viewModel.SharingPromptViewModel

@Composable
fun SharingPromptRoute(
    navController: NavController,
    viewModel: SharingPromptViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()


    SharingPromptScreen(
        searchQuery = uiState.searchQuery,
        generalCategories = uiState.generalCategories,
        selectedCategories = uiState.selectedCategories,
        isLoading = uiState.isLoading,
        onQueryChange = { viewModel.onQueryChange(it) },
        onSearchClicked = {
            //function search
            println("Searching: ${uiState.searchQuery}")
        },
        onCategoryToggle = { category ->
            viewModel.toggleCategory(category)
        },
        categories = uiState.categories,
        popularPrompts = uiState.popularPrompts,
        topRatedPrompts = uiState.topRatedPrompts,
        onPopularClick = {
            navController.navigate("PromptSearchResultScreen/Popular")
        },
        onTopRatedClick = {
            navController.navigate("PromptSearchResultScreen/Rating")
        },
        onPromptClick = { popularId ->
            navController.navigate("PromptDetailScreen/$popularId")
        }
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SharingPromptScreen(
    modifier: Modifier = Modifier ,
    searchQuery: String ,
    generalCategories: List<String> ,
    selectedCategories: List<String> ,
    isLoading: Boolean ,
    categories: List<String> ,
    onQueryChange: (String) -> Unit ,
    onSearchClicked: (String) -> Unit ,
    onCategoryToggle: (String) -> Unit ,
    popularPrompts: List<Prompt>,
    topRatedPrompts: List<Prompt>,
    onPopularClick: () -> Unit,
    onTopRatedClick: () -> Unit,
    onPromptClick: (String) -> Unit

) {
    //default selected index 0
    var selectedCategory by remember(categories) {
        mutableStateOf(if (categories.isNotEmpty()) categories[0] else "")
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.background),
        horizontalAlignment = Alignment.Start
    ) {

        Spacer(modifier = Modifier.height(36.dp))

        //Search Bar
        SearchBar(
            query = searchQuery,
            onQueryChange = onQueryChange,
            onSearchClicked = { onSearchClicked(searchQuery) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 26.dp)
        )

        Spacer(modifier.height(16.dp))
        Column(
            modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
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
                            isSelected = (selectedCategory == dbValue),
                            onClick = { selectedCategory = dbValue}
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

            //list top 5 prompt popular
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
                        onClick = onPopularClick,
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
                            rating = prompt.rating,
                            onClick = {
                                onPromptClick(prompt.id)
                            }
                        )
                    }
                }

            }

            Spacer(modifier.height(16.dp))

            //List top 5 prompt Rating
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
                        onClick = onTopRatedClick,
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
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun SharingPromptScreenPreview() {
    SharingPromptScreen(
        searchQuery = "Kucing lucu..",
        generalCategories = listOf(
            "Teknologi", "Seni Digital", "Bisnis",
            "Pendidikan", "Hiburan", "Fotografi"
        ),
        selectedCategories = listOf("Seni Digital"),
        isLoading = false,
        onQueryChange = {},
        onSearchClicked = {},
        onCategoryToggle = {},
        categories = listOf("Text to Text", "Text to Image", "Text to Video"),
        popularPrompts = emptyList(),
        topRatedPrompts = emptyList(),
        onPopularClick = {},
        onTopRatedClick = {},
        onPromptClick = {}
    )
}

