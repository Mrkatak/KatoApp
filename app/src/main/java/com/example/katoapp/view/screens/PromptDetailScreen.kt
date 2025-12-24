package com.example.katoapp.view.screens

import android.widget.Toast
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.katoapp.R
import com.example.katoapp.data.model.Prompt
import com.example.katoapp.view.component.CategoryMapper
import com.example.katoapp.view.component.GeneralCategoryButton
import com.example.katoapp.viewModel.PromptDetailViewModel
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun PromptDetailRoute(
    navController: NavController,
    viewModel: PromptDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    //side effect utk toast bookmark
    LaunchedEffect(uiState.bookmarkMessage) {
        uiState.bookmarkMessage?.let { msg ->
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
        }
    }

    // Handle Loading & Error
    if (uiState.isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else if (uiState.error != null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(text = uiState.error ?: "Terjadi Kesalahan", color = Color.Red)
        }
    } else if (uiState.prompt != null) {
        //jika prompt tidak != null tampilkan Screen
        PromptDetailScreen(
            data = uiState.prompt!!,
            isOwner = uiState.isOwner,
            isSaved = uiState.isSaved,
            onEditClick = {
                navController.navigate("PromptEditScreen")
            },
            onBackClick = { navController.popBackStack() },
            onSaveClick = {
                viewModel.toggleBookmark()
            },
            onCopyClick = { text ->
                // nanti logic copy
            },
            onReportClick = {
                //nanti logic laporan
                Toast.makeText(context, "Laporan terkirim", Toast.LENGTH_SHORT).show()
            }
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PromptDetailScreen(
    modifier : Modifier = Modifier ,
    data: Prompt,
    isOwner: Boolean,
    isSaved: Boolean,
    onEditClick: () -> Unit,
    onBackClick: () -> Unit,
    onSaveClick: () -> Unit,
    onCopyClick: (String) -> Unit,
    onReportClick: () -> Unit
) {
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current

    //format tanggal
    val dateString = try {
        val formatter = SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID"))
        data.createdAt?.let { formatter.format(it) } ?: "-"
    } catch (e: Exception) { "-" }

    //mapping icon dan nama category
    val categoryIcon = CategoryMapper.getIcon(data.category)
    val displayCategory = CategoryMapper.getDisplayName(data.category)

    var isPromptExpanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(
                        onClick = onBackClick
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_back),
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                },
                actions = {
                    if (data.status == "private" && isOwner) {
                        IconButton(
                            onClick = {
                                //nanti navigasi ke edit prompt screen
                            }
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_edit),
                                contentDescription = "Edit",
                                tint = MaterialTheme.colorScheme.onBackground,
                                modifier = Modifier.size(24.dp)
                            )
                        }
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

            // Judul prompt
            Text(
                text = data.title,
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(24.dp))

            //gambar prompt
            AsyncImage(
                model = data.imageUrl,
                contentDescription = data.title,
                contentScale = ContentScale.Crop,
                placeholder = painterResource(R.drawable.ic_image),
                error = painterResource(R.drawable.ic_image),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
                    .clip(RoundedCornerShape(18.dp))
            )

            Spacer(modifier = Modifier.height(24.dp))

            //stats
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                //kategori
                Column(
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Kategori" ,
                        style = MaterialTheme.typography.labelMedium ,
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    Button(
                        onClick = {},
                        modifier = Modifier
                            .height(40.dp)
                            .width(116.dp),
                        contentPadding = PaddingValues(0.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    ) {
                        StatItem(
                            value = displayCategory,
                            icon = categoryIcon
                        )
                    }
                }

                //rating
                Column(
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Rating" ,
                        style = MaterialTheme.typography.labelMedium ,
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    Button(
                        onClick = {
                            //update rating prompt
                        },
                        modifier = Modifier
                            .height(40.dp)
                            .width(116.dp),
                        contentPadding = PaddingValues(0.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    ) {
                        StatItem(
                            value = data.rating,
                            iconPainter = painterResource(R.drawable.ic_star)
                        )
                    }
                }

                //penggunaan
                Column(
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Penggunaan" ,
                        style = MaterialTheme.typography.labelMedium ,
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    Button(
                        onClick = {  },
                        modifier = Modifier
                            .height(40.dp)
                            .width(116.dp),
                        contentPadding = PaddingValues(0.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    ) {
                        StatItem(
                            value = data.usageCount.toString(),
                            iconPainter = painterResource(R.drawable.ic_usses)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (!isOwner && data.username.isNotEmpty()) {
                Text(
                    text = "Dibuat oleh: ${data.username}",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.align(Alignment.Start)
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            //kategori umum
            if (data.subCategories.isNotEmpty()) {
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Kategori Umum",
                        style = MaterialTheme.typography.labelMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(data.subCategories) { tag ->
                            GeneralCategoryButton(
                                text = tag,
                                isSelected = false,
                                onClick = {}
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }

            //prompt
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Prompt",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .padding(20.dp)
                        .animateContentSize()
                ) {
                    Column {
                        Text(
                            text = data.content ,
                            style = MaterialTheme.typography.bodyMedium ,
                            color = MaterialTheme.colorScheme.onBackground,
                            maxLines = if (isPromptExpanded) Int.MAX_VALUE else 4,
                            overflow = TextOverflow.Ellipsis
                        )

                        Spacer(modifier.height(8.dp))
                        if (data.content.length > 200) {
                            Text(
                                text = if (isPromptExpanded) "Tutup" else "Selengkapnnya",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onBackground,
                                modifier = Modifier
                                    .clickable { isPromptExpanded = !isPromptExpanded}
                                    .padding(vertical = 4.dp)
                                    .align(Alignment.End)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // tombol simpan
                if (!isOwner) {
                    val buttonColor = if (isSaved) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.primary
                    val contentColor = if (isSaved) MaterialTheme.colorScheme.onErrorContainer else MaterialTheme.colorScheme.onPrimary
                    val buttonText = if (isSaved) "Unsave" else "Simpan"

                    Button(
                        onClick = onSaveClick,
                        modifier = Modifier
                            .weight(1f).
                            height(40.dp),
                        shape = RoundedCornerShape(100.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = buttonColor,
                            contentColor = contentColor
                        )
                    ) {
                        Icon(
                            painter = if (isSaved) painterResource(R.drawable.ic_help)
                                        else painterResource(R.drawable.ic_save),
                            contentDescription = "ic save",
                            modifier = Modifier
                                .size(20.dp),
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = buttonText ,
                            style = MaterialTheme.typography.labelLarge ,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }

                //tombol salin prompt
                Button(
                    onClick = {
                        clipboardManager.setText(AnnotatedString(data.content))
                        Toast.makeText(context, "Prompt disalin!", Toast.LENGTH_SHORT).show()
                        onCopyClick(data.content)
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(40.dp),
                    shape = RoundedCornerShape(100.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.tertiary
                    )
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_copy) ,
                        contentDescription = "ic copy",
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier
                            .size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Salin Prompt" ,
                        style = MaterialTheme.typography.labelLarge ,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
            Spacer(modifier = Modifier.height(32.dp))

            //metadata
            Column(
                modifier
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Metadata" ,
                    style = MaterialTheme.typography.labelMedium ,
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Start,
                    modifier = Modifier.fillMaxWidth()
                )
                Card(
                    modifier
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    elevation = CardDefaults.elevatedCardElevation(2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        //model AI
                        Row(
                            modifier = Modifier
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "Model AI\t\t\t\t\t\t\t\t\t:",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.secondary
                            )
                            Text(
                                text = data.aiModel,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }

                        //versi AI
                        Row(
                            modifier = Modifier
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "Versi Model AI\t\t\t\t\t:",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.secondary
                            )
                            Text(
                                text = data.modelVersion,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }

                        //tanggal
                        Row(
                            modifier = Modifier
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "Tanggal Penbuatan\t:",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.secondary
                            )
                            Text(
                                text = dateString,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            //tombol report
            if (!isOwner) {
                TextButton(
                    onClick = onReportClick
                ) {
                    Text(
                        text = "Laporkan prompt",
                        color = MaterialTheme.colorScheme.secondary,
                        style = MaterialTheme.typography.bodyMedium,
                        textDecoration = TextDecoration.Underline
                    )
                }
            }
            Spacer(modifier = Modifier.height(32.dp))




        }
    }
}


@Composable
fun StatItem(
    value: String ,
    icon: Int? = null ,
    iconPainter: Painter? = null
) {

    Row(
        modifier = Modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        if (iconPainter != null) {
            Icon(
                painter = iconPainter ,
                contentDescription = null ,
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.size(24.dp)
            )
        } else if (icon != null) {
            Icon(
                painter = painterResource(id = icon) ,
                contentDescription = null ,
                tint = MaterialTheme.colorScheme.onPrimaryContainer ,
                modifier = Modifier.size(24.dp)
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = value ,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}

@Composable
fun MetadataRow(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.secondary
        )
        Text(
            text = value ,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.secondary
        )
    }
}


@Preview()
@Composable
fun PromptDetailScreenPreview() {
    val dummyPrompt = Prompt(
        id = "sample_id_123",
        title = "Cyberpunk City Neon Light",
        imageUrl = "",
        category = "visual_art",
        subCategories = listOf("Futuristic", "Sci-Fi", "Neon", "Cinematic"),
        content = "Imagine a futuristic city at night, drenched in neon rain. Flying cars zoom past towering skyscrapers with holographic advertisements. The atmosphere is moody and cyberpunk.",
        rating = "4.8",
        usageCount = 1250,
        aiModel = "Midjourney",
        modelVersion = "v6.0",
        createdAt = java.util.Date(),
    )

    PromptDetailScreen(
        data = dummyPrompt,
        isOwner = false,
        onBackClick = {},
        onSaveClick = {},
        onCopyClick = {},
        onReportClick = {},
        isSaved = false,
        onEditClick = {}
    )
}