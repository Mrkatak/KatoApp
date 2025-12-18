package com.example.katoapp.view.screens

import android.widget.Space
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.katoapp.view.component.SearchBar

@Composable
fun PromptSearchResultScreen(
    modifier: Modifier = Modifier,
    searchQuery : String,
    onQueryChange : (String) -> Unit,
    onSearchClicked : (String) -> Unit
) {
    Column(
        modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.background),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(modifier.height(36.dp))
        SearchBar(
            query = searchQuery ,
            onQueryChange = onQueryChange ,
            onSearchClicked = { onSearchClicked(searchQuery) } ,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 26.dp)
        )

        Spacer(modifier.height(16.dp))
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
                    text = "Gambar" , //sesuaikan dengan kategori umum yang dicari, atupun UsageCount/Rating
                    style = MaterialTheme.typography.headlineSmall ,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }

        Spacer(modifier.height(24.dp))
        //tampilkan daftar prompt berdasarkan pencarian di sini




    }




}


@Preview
@Composable
private fun View() {
    PromptSearchResultScreen(
        searchQuery = "Kucing lucu..",
        onQueryChange = {},
        onSearchClicked = {}
    )

}