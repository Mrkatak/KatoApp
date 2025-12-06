package com.example.katoapp.view.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.katoapp.R
import kotlinx.coroutines.delay

data class CarouselItem(
    val imageRes: Int,
    val title: String,
    val subtitle: String
)

@Composable
fun Carousel(
    modifier: Modifier = Modifier,
    items: List<CarouselItem> = listOf(
        CarouselItem(
            R.drawable.carousel_1,
            "KATO Premium",
            "Penyimpanan extra\nPremium Promtp"
        ),
        CarouselItem(
            R.drawable.carousel_2,
            "Sora",
            "Cute Miniature Prompt"
        ),
        CarouselItem(
            R.drawable.carousel_3,
            "Gemini",
            "Cute Cat Prompt"
        )
    )
) {
    //item size
    val itemWidth = 314.dp
    val itemHeight = 132.dp
    val itemSpacing = 8.dp
    val cornerRadius = 18.dp

    BoxWithConstraints(
        modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp)
    ) {
        //count padding
        val screenWidth = maxWidth
        val horizontalPadding = (screenWidth - itemWidth) / 2

        //infinity loop
        val infiniteCount = Int.MAX_VALUE
        //mulai dari tengah
        val initialPage = (infiniteCount / 2) - ((infiniteCount / 2) % items.size)

        val pagerState = rememberPagerState(
            initialPage = initialPage,
            pageCount = { infiniteCount }
        )

        //side effect auto scroll
        LaunchedEffect(Unit) {
            while (true) {
                delay(5000) //5 detik auto scroll
                if (!pagerState.isScrollInProgress) {
                    pagerState.animateScrollToPage(pagerState.currentPage + 1) //manual scroll
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            HorizontalPager(
                state = pagerState,
                contentPadding = PaddingValues(horizontal = horizontalPadding),
                pageSpacing = itemSpacing,
                modifier = Modifier.fillMaxWidth()
            ) { index ->
                val realIndex = index % items.size
                val item = items[realIndex]

                Card(
                    modifier = Modifier
                        .width(itemWidth)
                        .height(itemHeight)
                        .clip(RoundedCornerShape(cornerRadius)),
                    elevation = CardDefaults.cardElevation(8.dp)
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        Image(
                            painter = painterResource(id = item.imageRes),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )

                        //gradient
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.horizontalGradient(
                                        colors = listOf(
                                            Color.Black.copy(alpha = 0.6f),
                                            Color.Black.copy(alpha = 0.3f),
                                            Color.Black.copy(alpha = 0.6f)
                                        )
                                    )
                                )
                        )

                        Column(
                            modifier = Modifier.align(Alignment.Center),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = item.title,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = item.subtitle,
                                lineHeight = 16.sp,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Normal,
                                color = Color.White
                            )
                        }
                    }
                }




            }







        }
    }


}




@Preview(showBackground = true)
@Composable
fun PreviewCarousel() {
    Carousel()
}