package com.example.katoapp.viewModel.state

import com.example.katoapp.view.component.ChartData

data class PromptHistoryUiState(
    val isLoading: Boolean = false ,
    val usageChartData: List<ChartData> = emptyList() ,
    val totalUsage: Int = 0 ,
    val createdChartData: List<ChartData> = emptyList() ,
    val error: String? = null
)
