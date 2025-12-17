package com.example.katoapp.view.screens

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
import com.example.katoapp.view.component.CategoryMapper
import com.example.katoapp.view.component.GeneralCategoryButton
import com.example.katoapp.view.component.MainCategoryButton
import com.example.katoapp.view.component.PromptCard
import com.example.katoapp.view.component.SearchBar
import com.example.katoapp.viewModel.PromptOrgViewModel
import com.example.katoapp.viewModel.SearchPromptViewModel

@Composable
fun SharingPromptRoute(
    navController: NavController,
    viewModel: SearchPromptViewModel = hiltViewModel(),
    promptOrgViewModel: PromptOrgViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val promptUiState by promptOrgViewModel.uiState.collectAsState()



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
        categories = promptUiState.categories,
        prompts = promptList,
    )
}

data class DummyPromptList(
    val title: String,
    val imageUrl: String,
    val category: String,
    val rating: String
)

val promptList = listOf(
    DummyPromptList("Style Lukisan Klasik Eropa", "", "Gambar", "4.5"),
    DummyPromptList("Prompt cara mengelola keuangan pribadi", "", "Video", "4.5"),
    DummyPromptList("Midjourney Ilustrasi Fantasi ", "R.drawable.dummy_card_image", "Teks", "4.5"),
    DummyPromptList("Style Lukisan Klasik Eropa", "R.drawable.dummy_card_image", "Gambar", "4.5"),
    DummyPromptList("Style Lukisan Klasik Eropa", "R.drawable.dummy_card_image", "Gambar", "4.5")

)


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
    prompts: List<DummyPromptList> = emptyList()

) {
    var selectedCategory by remember(categories) {
        mutableStateOf(if (categories.isNotEmpty()) categories[0] else "")
    }


    Column(
        modifier = modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.Start
    ) {

        Spacer(modifier = Modifier.height(36.dp))
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
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    items(prompts) { prompt ->
                        PromptCard(
                            title = prompt.title,
                            imageUrl = prompt.imageUrl,
                            category = prompt.category,
                            rating = prompt.rating,
                            onClick = {
                                println("di klik")
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
                        text = "Prompt Gambar",
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

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    items(prompts) { prompt ->
                        PromptCard(
                            title = prompt.title,
                            imageUrl = prompt.imageUrl,
                            category = prompt.category,
                            rating = prompt.rating,
                            onClick = {
                                println("di klik")
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
        categories = listOf("Text to Text", "Text to Image", "Text to Video")
    )
}

