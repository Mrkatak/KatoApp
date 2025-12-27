package com.example.katoapp.view.screens

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage // Pastikan dependency Coil sudah ada, jika belum pakai Image biasa dulu
import com.example.katoapp.R
import com.example.katoapp.data.model.Prompt
import com.example.katoapp.view.component.CategoryMapper
import com.example.katoapp.view.component.GeneralCategory
import com.example.katoapp.view.component.MainCategoryButton
import com.example.katoapp.viewModel.AuthViewModel
import com.example.katoapp.viewModel.PromptOrgViewModel

@Composable
fun PromptEditRoute(
    navController: NavController,
    viewModel: PromptOrgViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri -> viewModel.onImageSelected(uri) }
    )

    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            Toast.makeText(context, "Berhasil diperbarui!", Toast.LENGTH_SHORT).show()
            navController.popBackStack()
        }
    }

    if (uiState.isLoading && uiState.promptData == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else if (uiState.promptData != null) {
        PromptEditScreen(
            initialData = uiState.promptData!!,
            categories = uiState.categories,
            generalCategories = uiState.generalCategories,
            newImageUri = uiState.selectedImageUri,
            isLoading = uiState.isLoading,
            onBackClick = { navController.popBackStack() },
            onImageClick = { imagePicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) },
            onSaveClick = { title, content, mainCat, model, ver, subCats, sharing ->
                viewModel.updatePrompt(title, content, mainCat, subCats, model, ver, sharing)
            }
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PromptEditScreen(
    modifier: Modifier = Modifier ,
    initialData: Prompt ,
    newImageUri: Uri?,
    onBackClick: () -> Unit ,
    onSaveClick: (String, String, String, String, String, List<String>, Boolean) -> Unit ,
    onImageClick: () -> Unit ,
    categories: List<String> ,
    generalCategories: List<String> ,
    isLoading: Boolean = false
) {
    // State Lokal Form
    var title by remember { mutableStateOf(initialData.title) }
    var promptContent by remember { mutableStateOf(initialData.content) }
    var aiModel by remember { mutableStateOf(initialData.aiModel) }
    var modelVersion by remember { mutableStateOf(initialData.modelVersion) }

    //category
    var selectedCategory by remember { mutableStateOf(initialData.category)}
    var selectedGeneralCategories by remember { mutableStateOf(initialData.subCategories) }

    var isSharing by remember { mutableStateOf(initialData.status == "sharing") }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Edit Prompt",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBackClick
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_back),
                            contentDescription = "ic back",
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(bottom = 32.dp, top = 8.dp)
                .padding(horizontal = 26.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            //Input Judul
            InputSection(title = "Judul") {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    placeholder = {
                        Text(
                            text = "Masukkan Judul",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Sentences
                    )
                )
            }

            //Input Isi Prompt
            InputSection(title = "Isi Prompt") {
                OutlinedTextField(
                    value = promptContent,
                    onValueChange = { promptContent = it },
                    placeholder = {
                        Text(
                            text = "Masukkan isi prompt...",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(112.dp),
                    shape = RoundedCornerShape(12.dp),
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Sentences
                    ),
                    maxLines = 10
                )
            }

            //Main Category
            InputSection(title = "Kategori Utama") {
                if (isLoading) {
                    CircularProgressIndicator()
                } else {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        categories.forEach { dbValue ->
                            MainCategoryButton(
                                text = CategoryMapper.getDisplayName(dbValue),
                                icon = CategoryMapper.getIcon(dbValue),
                                isSelected = (selectedCategory == dbValue),
                                onClick = { selectedCategory = dbValue}
                            )
                        }
                    }
                }
            }

            //Model & versi AI
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(26.dp)
            ) {
                Column(
                    modifier
                        .weight(1f)
                ) {
                    InputSection("Model AI") {
                        OutlinedTextField(
                            value = aiModel,
                            onValueChange = { aiModel = it },
                            placeholder = {
                                Text(
                                    text = "Gemini...",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.secondary,
                                    overflow = TextOverflow.Ellipsis
                                ) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true,
                            maxLines = 1,
                            keyboardOptions = KeyboardOptions(
                                capitalization = KeyboardCapitalization.Sentences
                            )
                        )
                    }
                }

                Column(
                    modifier
                        .weight(1f)
                ) {
                    InputSection("Versi Model AI") {
                        OutlinedTextField(
                            value = modelVersion,
                            onValueChange = { modelVersion = it },
                            placeholder = {
                                Text(
                                    text = "2.5....",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.secondary
                                )
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true,
                            maxLines = 1,
                            keyboardOptions = KeyboardOptions(
                                capitalization = KeyboardCapitalization.Sentences
                            )
                        )
                    }
                }
            }

            //General Category
            InputSection(title = "Kategori Umum") {
                if (generalCategories.isEmpty() && isLoading) {
                    OutlinedTextField(
                        value = "Memuat data...",
                        onValueChange = {},
                        enabled = false,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                } else {
                    GeneralCategory(
                        label = "Pilih Kategori",
                        options = generalCategories,
                        selectedOptions = selectedGeneralCategories,
                        onSelectionChanged = { cat ->
                            val list = selectedGeneralCategories.toMutableList()
                            if (list.contains(cat)) list.remove(cat) else list.add(cat)
                            selectedGeneralCategories = list
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            //Gambar Preview
            InputSection(title = "Gambar Preview") {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                        .clickable { onImageClick() },
                    contentAlignment = Alignment.Center
                ) {
                    if (newImageUri != null) {
                        AsyncImage(
                            model = newImageUri,
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else if(initialData.imageUrl.isNotEmpty()){
                        AsyncImage(
                            model = initialData.imageUrl,
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                painter = painterResource(R.drawable.ic_image),
                                contentDescription = "Upload",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Upload Gambar",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }

            //Status Prompt
            InputSection(title = "Status Prompt") {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Switch(
                        checked = isSharing,
                        onCheckedChange = { isSharing = it }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isSharing) "Sharing" else "Private",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Row(
                    modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = {
                            //function hapus
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error
                        ),
                        elevation = ButtonDefaults.elevatedButtonElevation(2.dp),
                        shape = RoundedCornerShape(100.dp)

                    ) {
                        Text(
                            text = "Hapus Prompt" ,
                            style = MaterialTheme.typography.labelLarge ,
                            color = MaterialTheme.colorScheme.onTertiary
                        )
                    }

                    Spacer(modifier.width(8.dp))
                    Button(
                        onClick = {
                            onSaveClick(
                                title,
                                promptContent,
                                selectedCategory,
                                aiModel,
                                modelVersion,
                                selectedGeneralCategories,
                                isSharing
                            )
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        ),
                        elevation = ButtonDefaults.elevatedButtonElevation(2.dp),
                        shape = RoundedCornerShape(100.dp)

                    ) {
                        Text(
                            text = "Simpan" ,
                            style = MaterialTheme.typography.labelLarge ,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                //Button Batal
                Button(
                    onClick = onBackClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp),
                    shape = RoundedCornerShape(100.dp),
                    elevation = ButtonDefaults.elevatedButtonElevation(2.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Text(
                        text = "Batal",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }

        }
    }
}


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PromptEditPreview() {
    val dummyPrompt = Prompt(

        title = "Judul Prompt",
        content = "Isi prompt",
        aiModel = "ChatGPT",
        modelVersion = "4o",
        category = "Text to Image",
        subCategories = listOf("Robotic", "Teknologi"),
        status = "sharing"
    )

    PromptEditScreen(
        initialData = dummyPrompt,
        newImageUri = null,
        categories = listOf("Text to Text", "Text to Image"),
        generalCategories = listOf("Teknologi", "Seni"),
        onBackClick = {},
        onSaveClick = { _, _, _, _, _, _, _ -> },
        onImageClick = {}
    )
}

