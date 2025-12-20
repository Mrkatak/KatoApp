package com.example.katoapp.view.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.katoapp.R

@Composable
fun SearchBar(
    modifier: Modifier = Modifier,
    query: String,
    onQueryChange: (String) -> Unit,
    onSearchClicked: () -> Unit,
    readOnly: Boolean = false,
    onClick: (() -> Unit)? = null
) {
    val focusManager = LocalFocusManager.current //control input keyboard
    val pillShape: RoundedCornerShape = CircleShape

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .shadow(
                elevation = 8.dp ,
                shape = pillShape ,
                spotColor = Color.Black.copy(alpha = 0.1f)
            ),
        shape = pillShape,
        color = MaterialTheme.colorScheme.onPrimary,
    ) {
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 26.dp)
            ) {
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.CenterStart
                ) {
                    //jika query kosong tampilkan "kucing lucu"
                    if (query.isEmpty()) {
                        Text(
                            text = "Kucing Lucu..",
                            color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.7f),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }

                    BasicTextField(
                        value = query,
                        onValueChange = {
                            if (!readOnly) onQueryChange(it)
                        },
                        textStyle = MaterialTheme.typography.bodyLarge.copy(
                            color = MaterialTheme.colorScheme.secondary
                        ),
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Search
                        ) ,
                        keyboardActions = KeyboardActions(
                            onSearch = {
                                onSearchClicked()
                                focusManager.clearFocus()
                            }
                        ),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Icon(
                    painter = painterResource(R.drawable.ic_search) ,
                    contentDescription = "Search Icon",
                    tint = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier
                        .size(40.dp)
                        .padding(start = 8.dp)
                )
            }

            if (readOnly && onClick != null){
                Box(
                    modifier
                        .matchParentSize()
                        .clickable(onClick = onClick)
                )
            }
        }
    }
}


@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
fun KatoSearchBarPreview() {
    MaterialTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            SearchBar(
                query = "",
                onQueryChange = {},
                onSearchClicked = {}
            )
        }
    }
}