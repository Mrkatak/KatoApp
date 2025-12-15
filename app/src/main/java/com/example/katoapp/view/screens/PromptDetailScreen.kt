package com.example.katoapp.view.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.katoapp.R

data class PromptDetail(
    val id: String,
    val title: String,
    val imageUrl: String,
    val mainCategory: String,
    val subCategories: List<String>,
    val promptContent: String,
    val rating: String,
    val usageCount: Int,
    val aiModel: String,
    val modelVersion: String,
    val createdAt: String
)

@Composable
fun PromptDetailRoute(
    navController: NavController,
    promptId: String?
) {
    val dummyDetail = PromptDetail(
        id = "123",
        title = "Style Lukisan Klasik Eropa",
        imageUrl = "https://images.unsplash.com/photo-1579783902614-a3fb3927b6a5?q=80&w=600&auto=format&fit=crop",
        mainCategory = "Gambar",
        subCategories = listOf("Design", "Lukisan", "Klasik", "Wallpaper", "Art"),
        promptContent = "A mystical night-scene in an East Asian fantasy style. A lone monk wearing traditional robes walks along a stone path toward a small wooden temple lit warmly by lanterns. In the distance, a massive rock platform rises from the clouds...",
        rating = "4.5",
        usageCount = 30,
        aiModel = "Midjourney",
        modelVersion = "V7",
        createdAt = "11 November 2025"
    )

    PromptDetailScreen(
        data = dummyDetail,
        onBackClick = { navController.popBackStack() },
        onSaveClick = { println("Simpan ke koleksi") },
        onCopyClick = { println("Copy text") },
        onReportClick = { println("Lapor") }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PromptDetailScreen(
    data: PromptDetail,
    onBackClick: () -> Unit,
    onSaveClick: () -> Unit,
    onCopyClick: (String) -> Unit,
    onReportClick: () -> Unit
) {
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { }) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit", tint = Color.Gray)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = data.title,
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(24.dp))
            AsyncImage(
                model = data.imageUrl,
                contentDescription = data.title,
                contentScale = ContentScale.Crop,
                placeholder = painterResource(R.drawable.ic_image),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
                    .clip(RoundedCornerShape(24.dp))
            )

            Spacer(modifier = Modifier.height(24.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StatItem(label = "Kategori", value = data.mainCategory, icon = R.drawable.ic_teks)
                StatItem(label = "Rating", value = data.rating, iconVector = Icons.Default.Star, iconColor = Color(0xFFFFD700))
                StatItem(label = "Penggunaan", value = "${data.usageCount}", icon = R.drawable.ic_sharing)
            }

            Spacer(modifier = Modifier.height(24.dp))
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Kategori Umum",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(data.subCategories) { tag ->
                        SuggestionChip(
                            onClick = { },
                            label = { Text(tag) },
                            shape = RoundedCornerShape(100.dp),
                            border = BorderStroke(
                                width = 1.dp,
                                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Prompt",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f))
                        .padding(20.dp)
                ) {
                    Text(
                        text = data.promptContent,
                        style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 24.sp),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = onSaveClick,
                    modifier = Modifier.weight(1f).height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Icon(imageVector = Icons.Default.AccountBox, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Simpan")
                }

                Button(
                    onClick = {
                        clipboardManager.setText(AnnotatedString(data.promptContent))
                        Toast.makeText(context, "Prompt disalin!", Toast.LENGTH_SHORT).show()
                        onCopyClick(data.promptContent)
                    },
                    modifier = Modifier.weight(1f).height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                ) {
                    Icon(imageVector = Icons.Default.AddCircle, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Salin")
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Metadata", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                    Spacer(modifier = Modifier.height(12.dp))
                    MetadataRow("Model AI", data.aiModel)
                    MetadataRow("Versi Model AI", data.modelVersion)
                    MetadataRow("Tanggal Pembuatan", data.createdAt)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
            TextButton(onClick = onReportClick) {
                Text(
                    text = "Laporkan prompt",
                    color = Color.Gray,
                    style = MaterialTheme.typography.bodySmall,
                    textDecoration = androidx.compose.ui.text.style.TextDecoration.Underline
                )
            }
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}


@Composable
fun StatItem(
    label: String,
    value: String,
    icon: Int? = null,
    iconVector: androidx.compose.ui.graphics.vector.ImageVector? = null,
    iconColor: Color = MaterialTheme.colorScheme.primary
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
        Spacer(modifier = Modifier.height(4.dp))
        Surface(
            shape = RoundedCornerShape(50),
            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
            modifier = Modifier.height(32.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 12.dp)
            ) {
                if (iconVector != null) {
                    Icon(imageVector = iconVector, contentDescription = null, tint = iconColor, modifier = Modifier.size(16.dp))
                } else if (icon != null) {
                    Icon(painter = painterResource(id = icon), contentDescription = null, tint = iconColor, modifier = Modifier.size(16.dp))
                }
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = value, style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
            }
        }
    }
}

@Composable
fun MetadataRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
        Text(text = value, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium))
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun DetailPreview() {
    MaterialTheme {
        PromptDetailRoute(navController = androidx.navigation.compose.rememberNavController(), promptId = "1")
    }
}