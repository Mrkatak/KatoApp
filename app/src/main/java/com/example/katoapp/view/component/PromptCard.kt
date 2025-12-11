package com.example.katoapp.view.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.katoapp.R

@Composable
fun PromptCard(
    title: String,
    imageUrl: Int, //Harusnya String
    category: String,
    rating: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val overlayColor = Color(0xFF1D1B20).copy(alpha = 0.7f)

    Card(
        modifier = modifier
            .width(174.dp)
            .height(226.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(18.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            AsyncImage(
                model = imageUrl,
                contentDescription = title,
                contentScale = ContentScale.Crop,
                placeholder = painterResource(R.drawable.dummy_card_image),
                error = painterResource(R.drawable.ic_image),
                modifier = Modifier.fillMaxSize()
            )

            Column(
                modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomStart)
                    .background(overlayColor),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Column(
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .padding(top = 8.dp, bottom = 14.dp)
                        .fillMaxWidth()
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.labelLarge.copy(
                            color = Color.White),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_star),
                                contentDescription = "Rating",
                                tint = Color(0xFFD9C334),
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = rating,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.secondaryContainer
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(18.dp),
                            color = Color.Transparent,
                            border = BorderStroke(
                                width = 1.dp,
                                color  = MaterialTheme.colorScheme.primaryContainer
                            ),
                            modifier = Modifier.height(20.dp)
                        ) {
                            Box(
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = category,
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        color = Color.White
                                    ),
                                    modifier = Modifier
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                }
            }





        }








    }
}

@Preview
@Composable
fun PromptCardPreview() {
    MaterialTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            PromptCard(
                title = "Prompt mengatasi code error dan penjelasan",
                imageUrl = R.drawable.dummy_card_image,
                category = "Gambar",
                rating = "4.5",
                onClick = {}
            )
        }
    }
}