package com.example.katoapp.view.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.katoapp.R

@Composable
fun MainCategoryButton(
    text: String ,
    icon: Int ,
    isSelected: Boolean ,
    onClick: () -> Unit ,
    modifier: Modifier = Modifier
) {
    val containerColor = if (isSelected) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.onPrimary
    val contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimary
                            else MaterialTheme.colorScheme.secondary

    Surface(
        modifier
            .clickable(onClick = onClick)
            .padding(vertical = 2.dp),
        shape = CircleShape,
        color = containerColor,
        shadowElevation = if (isSelected) 2.dp else 2.dp
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .height(44.dp)
        ) {
            Icon(
                painter = painterResource(icon),
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier.width(8.dp))

            Text(
                text = text,
                color = contentColor,
                style = MaterialTheme.typography.labelLarge
            )
        }
    }



}


object CategoryMapper {
    //get name
    fun getDisplayName(dbValue: String): String {
        return when (dbValue) {
            "Text to Text" -> "Teks"
            "Text to Image" -> "Gambar"
            "Text to Video" -> "Video"
            "Text to Speech" -> "Suara"
            else -> dbValue
        }
    }

    //get icon
    fun getIcon(dbValue: String): Int {
        return when (dbValue) {
            "Text to Text" -> R.drawable.ic_teks
            "Text to Image" -> R.drawable.ic_image
            "Text to Video" -> R.drawable.ic_video
            "Text to Speech" -> R.drawable.ic_speech
            else -> R.drawable.ic_help
        }
    }
}


@Preview
@Composable
fun PreviewCategoryButton() {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        MainCategoryButton(
            text = "Teks",
            icon = R.drawable.ic_teks,
            isSelected = true,
            onClick = {}
        )
        MainCategoryButton(
            text = "Gambar",
            icon = R.drawable.ic_image,
            isSelected = false,
            onClick = {}
        )
    }
}