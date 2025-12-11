package com.example.katoapp.view.component

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ModifierLocalBeyondBoundsLayout
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.katoapp.R

@Composable
fun DetailPromptButton(
    modifier: Modifier = Modifier
) {

    Button(
        onClick = { },
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_teks),
            contentDescription = "ic text",
            tint = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier.size(24.dp)
        )
        Text(
            text = "Teks",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onPrimary

        )
    }

}