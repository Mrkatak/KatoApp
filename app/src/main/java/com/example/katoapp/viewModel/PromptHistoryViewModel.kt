package com.example.katoapp.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.katoapp.data.repository.PromptRepository
import com.example.katoapp.view.component.CategoryMapper
import com.example.katoapp.view.component.ChartData
import com.example.katoapp.viewModel.state.PromptHistoryUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class PromptHistoryViewModel @Inject constructor(
    private val repository: PromptRepository
): ViewModel() {

    private val _uiState = MutableStateFlow(PromptHistoryUiState())
    val uiState: StateFlow<PromptHistoryUiState> = _uiState.asStateFlow()

    init {
        loadHistoryData("7 Hari Terakhir")
    }


    //function filter change
    fun onFilterChanged(filterType: String) {
        loadHistoryData(filterType)
    }

    //function load history data
    private fun loadHistoryData(filterType: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            //hitung tanggal
            val minDate: Date? = when (filterType) {
                "7 Hari Terakhir" -> getDateDaysAgo(7)
                "30 Hari Terakhir" -> getDateDaysAgo(30)
                "Semua Waktu" -> null
                else -> null
            }
            val usageMap = repository.getPromptLog(minDate)
            val createdMap = repository.getCreatedStats(minDate)
            val usageList = mapMapToChartData(usageMap)
            val createdList = mapMapToChartData(createdMap)
            val totalUsage = usageList.sumOf { it.value.toInt() }

            _uiState.update {
                it.copy(
                    isLoading = false,
                    usageChartData = usageList,
                    totalUsage = totalUsage,
                    createdChartData = createdList
                )
            }
        }
    }

    //hitung tanggal mundur
    private fun getDateDaysAgo(days: Int): Date {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, -days)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        return calendar.time
    }

    //map db to chart data
    private fun mapMapToChartData(dataMap: Map<String, Int>): List<ChartData> {
        return dataMap.map { (dbCategory, count) ->
            ChartData(
                label = CategoryMapper.getDisplayName(dbCategory),
                value = count.toFloat()
            )
        }
    }

}