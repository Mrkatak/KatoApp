package com.example.katoapp.view.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import com.example.katoapp.view.component.SearchBar

@Composable
fun SearchPromptScreen(
    modifier: Modifier = Modifier,
    onQueryChange: (String) -> Unit,
    onSearchClicked: () -> Unit,
    onSearch: String
) {

    val focusManager = LocalFocusManager.current //control input keyboard
    val pillShape: RoundedCornerShape = CircleShape
    Column(
        modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        SearchBar(
            query = onSearch,
            onQueryChange = onQueryChange,
            onSearchClicked = onSearchClicked
        )
    }

}