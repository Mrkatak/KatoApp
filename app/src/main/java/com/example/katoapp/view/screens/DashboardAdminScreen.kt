package com.example.katoapp.view.screens

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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
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
import com.example.katoapp.viewModel.AuthViewModel
import com.example.katoapp.viewModel.state.AdminTab

@Composable
fun DashboardAdminRoute(
    navController: NavController,
    authViewModel: AuthViewModel = hiltViewModel(),
    viewModel: AdminViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    DashboardAdminScreen(
        categories = uiState.categories,
        prompts = uiState.prompts,
        selectedMainCategory = uiState.selectedMainCategory,
        currentTab = uiState.currentTab,
        isLoading = uiState.isLoading,
        onTabSelected = { tab ->
            viewModel.changeTab(tab)
        },
        onCategoryClick = { cat ->
            viewModel.selectMainCategory(cat)
        },
        onPromptClick = { promptId->
            val isReportContext = (uiState.currentTab == AdminTab.REPORT)
            navController.navigate("PromptDetailsReportScreen/$promptId/$isReportContext")
        },
        onLogoutClick = {
            authViewModel.logout()
            navController.navigate("LoginScreen") {
//                popUpTo("DashboardAdminScreen") { inclusive = true }
                popUpTo(0)
            }
        }
    )

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardAdminScreen(
    modifier: Modifier = Modifier,
    categories: List<String> = emptyList(),
    prompts: List<Prompt> = emptyList(),
    selectedMainCategory : String,
    currentTab: AdminTab,
    isLoading: Boolean,
    onTabSelected: (AdminTab) -> Unit = {},
    onCategoryClick: (String) -> Unit = {},
    onLogoutClick: () -> Unit = {},
    onPromptClick: (String) -> Unit = {}
) {

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
                        onClick = onLogoutClick
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

                // tombol laporan
                AdminTabButton(
                    text = "Laporan Prompt",
                    isSelected = (currentTab == AdminTab.REPORT),
                    onClick = { onTabSelected(AdminTab.REPORT) },
                    modifier = Modifier.weight(1f)
                )

                // tombol meninjau
                AdminTabButton(
                    text = "Meninjau Prompt",
                    isSelected = (currentTab == AdminTab.REVIEW),
                    onClick = { onTabSelected(AdminTab.REVIEW) },
                    modifier = Modifier.weight(1f)
                )
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
                        .padding(horizontal = 26.dp)
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    categories.forEach { dbValue ->
                        MainCategoryButton(
                            text = CategoryMapper.getDisplayName(dbValue),
                            icon = CategoryMapper.getIcon(dbValue),
                            isSelected = (selectedMainCategory == dbValue),
                            onClick = {
                                onCategoryClick(dbValue)
                            }
                        )
                    }
                }
            }

            Spacer(modifier.height(24.dp))

            //prompt display
            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (prompts.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = if(currentTab == AdminTab.REPORT) "Tidak ada laporan." else "Belum ada prompt.",
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(start = 26.dp, end = 26.dp, bottom = 100.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(prompts) { prompt ->
                        val displayCategory = CategoryMapper.getDisplayName(prompt.category)
                        PromptCard(
                            title = prompt.title,
                            imageUrl = prompt.imageUrl,
                            category = displayCategory,
                            rating = prompt.rating.ifEmpty { "New" },
                            onClick = { onPromptClick(prompt.id) }
                        )
                    }
                }
            }
        }

    }

}


@Composable
fun AdminTabButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val containerColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
    val contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant

    Button(
        onClick = onClick,
        shape = RoundedCornerShape(100.dp),
        modifier = modifier.height(40.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor
        ),
        elevation = ButtonDefaults.elevatedButtonElevation(
            if (isSelected) 4.dp else 0.dp
        )
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            maxLines = 1
        )
    }
}


@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun View() {
    val dummyCategories = listOf(
        "Text to Text",
        "Text to Image",
        "Text to Video",
        "Text to Speech"
    )

    val dummyPrompts = listOf(
        Prompt(
            id = "1",
            title = "Teks Cara Menjadi Presiden",
            imageUrl = "",
            category = "Text to Text",
            rating = "4.5",
            status = "sharing"
        ),
        Prompt(
            id = "2",
            title = "Gambar Indonesia Era Kegelapan",
            imageUrl = "",
            category = "Text to Image",
            rating = "New",
            status = "sharing"
        ),
        Prompt(
            id = "3",
            title = "Video Mengenakan Setelan Jas dan Dasi",
            imageUrl = "",
            category = "Text to Video",
            rating = "3.0",
            status = "sharing"
        )
    )

    DashboardAdminScreen(
        categories = dummyCategories,
        prompts = dummyPrompts,
        selectedMainCategory = "Text to Text",
        currentTab = AdminTab.REPORT,
        isLoading = false,
        onTabSelected = {},
        onCategoryClick = {},
        onLogoutClick = {},
        onPromptClick = {}
    )
}

