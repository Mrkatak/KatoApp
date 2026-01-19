package com.example.katoapp.view.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.katoapp.R
import com.example.katoapp.view.component.BarChartCard
import com.example.katoapp.view.component.BarChartSkeleton
import com.example.katoapp.view.component.DonutChart
import com.example.katoapp.view.component.ChartData
import com.example.katoapp.view.component.DonutChartSkeleton
import com.example.katoapp.view.component.HistoryFilterButton
import com.example.katoapp.viewModel.PromptHistoryViewModel

@Composable
fun PromptHistoryRoute(
    navController: NavController,
    viewModel: PromptHistoryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    PromptHistoryScreen(
        promptUsed = uiState.usageChartData,
        promptCreated = uiState.createdChartData,
        totalUsage = uiState.totalUsage,
        isLoading = uiState.isLoading,
        onBackClick = {
            navController.popBackStack()
        },
        onFilteredChange = { filter ->
            viewModel.onFilterChanged(filter)
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PromptHistoryScreen(
    modifier: Modifier = Modifier ,
    promptUsed: List<ChartData>,
    promptCreated: List<ChartData>,
    totalUsage: Int,
    isLoading: Boolean,
    onBackClick: () -> Unit,
    onFilteredChange: (String) -> Unit
) {
    var selectedFilter by remember { mutableStateOf("7 Hari Terakhir") }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Laporan",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            onBackClick()
                        }
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_back),
                            contentDescription = "ic back",
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 26.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                //filter button
                Column(
                    modifier
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    HistoryFilterButton(
                        selectedFilter = selectedFilter,
                        onFilteredSelected = { newFilter ->
                            selectedFilter = newFilter
                            onFilteredChange(newFilter)
                        }
                    )
                }

                //donut chart
                if (isLoading) {
                    DonutChartSkeleton()
                } else{
                    DonutChart(
                        data = promptUsed,
                        totalValue = totalUsage
                    )
                }

                //bar chart
                if (isLoading) {
                    BarChartSkeleton()
                } else{
                    BarChartCard(
                        data = promptCreated
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))
            }
        }




    }
}


@Preview
@Composable
private fun View() {
    val chartData = listOf(
        ChartData("Teks", 12f),
        ChartData("Gambar", 8f),
        ChartData("Video", 5f),
        ChartData("Suara", 5f)
    )
    PromptHistoryScreen(
        promptUsed = chartData,
        promptCreated = chartData,
        totalUsage = 20,
        isLoading = false,
        onBackClick = {},
        onFilteredChange = {}
    )
}