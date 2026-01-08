package com.example.katoapp.view.component


import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import com.example.katoapp.utils.shimmerEffect

@Composable
fun MainCategorySkeleton() {
    Box(
        modifier = Modifier
            .padding(vertical = 2.dp)
            .height(44.dp)
            .width(100.dp)
            .clip(CircleShape)
            .shimmerEffect()
    )
}

@Composable
fun PromptCardSkeleton() {
    Box(
        modifier = Modifier
            .width(174.dp)
            .height(226.dp)
            .clip(RoundedCornerShape(18.dp))
            .shimmerEffect()
    )
}

@Composable
fun AsyncImageSkeleton() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp)
            .padding(horizontal = 26.dp)
            .clip(RoundedCornerShape(18.dp))
            .shimmerEffect()
    )
}