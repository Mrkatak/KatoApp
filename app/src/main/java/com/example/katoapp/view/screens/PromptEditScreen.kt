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

    //launcher utk memilih gambar
    val singlePhotoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            //kirim URI ke ViewModel utk disimpan sementara
            viewModel.onImageSelected(uri)
        }
    )

    LaunchedEffect(uiState.isSuccess, uiState.errorMessage) {
        if (uiState.isSuccess) {
            Toast.makeText(context, "Prompt Berhasil Disimpan!", Toast.LENGTH_SHORT).show()
            navController.popBackStack()
            viewModel.resetSuccessState()
        }

        if (uiState.errorMessage != null) {
            Toast.makeText(context, uiState.errorMessage, Toast.LENGTH_LONG).show()
        }
    }


    PromptEditScreen(
        onBackClick = { navController.popBackStack() },
        onSaveClick = { title, content, mainCat, model, version, subCats, sharing ->
            viewModel.savePrompt(
                title = title,
                content = content,
                mainCategory = mainCat,
                subCategories = subCats,
                aiModel = model,
                modelVersion = version,
                imageUri = uiState.selectedImageUri,
                isSharing = sharing
            )
        },
        onImageClick = {
            singlePhotoPickerLauncher.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
            )
        },
        categories = uiState.categories,
        isLoading = uiState.isLoading,
        generalCategories = uiState.generalCategories,
        selectedGeneralCategories = uiState.selectedGeneralCategories,
        selectedImageUri = uiState.selectedImageUri,
        onGeneralCategoryToggle = { category ->
            viewModel.toggleGeneralCategory(category)
        }
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PromptEditScreen(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit,
    onSaveClick: (String, String, String, String, String, List<String>, Boolean) -> Unit,
    onImageClick: () -> Unit,
    categories: List<String>,
    generalCategories: List<String>,
    selectedGeneralCategories: List<String>,
    onGeneralCategoryToggle: (String) -> Unit,
    isLoading: Boolean = false,
    selectedImageUri: Uri? = null
) {
    // State Lokal Form
    var title by remember { mutableStateOf("") }
    var promptContent by remember { mutableStateOf("") }
    var aiModel by remember { mutableStateOf("") }
    var modelVersion by remember { mutableStateOf("") }
//    var generalCategory by remember { mutableStateOf("") }

    //state main category (default index 0)
    var selectedCategory by remember(categories) {
        mutableStateOf(if (categories.isNotEmpty()) categories[0] else "")
    }
    var isSharing by remember { mutableStateOf(false) }

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
                        label = "Makanan...",
                        options = generalCategories,
                        selectedOptions = selectedGeneralCategories,
                        onSelectionChanged = onGeneralCategoryToggle,
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
                    if (selectedImageUri != null) {
                        AsyncImage(
                            model = selectedImageUri,
                            contentDescription = "Preview Gambar",
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
                //Button Simpan
//                Button(
//                    onClick = {
//                        if (title.isNotEmpty() && promptContent.isNotEmpty()) {
//                            onSaveClick(
//                                title,
//                                promptContent,
//                                selectedCategory,
//                                aiModel,
//                                modelVersion,
//                                selectedGeneralCategories,
//                                isSharing
//                            )
//                        }
//                    },
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .height(40.dp),
//                    shape = RoundedCornerShape(100.dp),
//                    enabled = !isLoading,
//                    elevation = ButtonDefaults.elevatedButtonElevation(2.dp)
//                ) {
//                    if (isLoading) {
//                        CircularProgressIndicator(
//                            color = Color.White,
//                            modifier = Modifier.size(24.dp)
//                        )
//                    } else {
//                        Text(
//                            text = "Simpan",
//                            style = MaterialTheme.typography.labelLarge,
//                            color = MaterialTheme.colorScheme.onPrimary
//                        )
//                    }
//                }

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
                            //function hapus
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
@Composable private fun View() {
    PromptEditScreen(
        onBackClick = {},
        onSaveClick = { _, _, _, _, _, _, _ -> },
        onImageClick = {},
        onGeneralCategoryToggle = {},
        categories = listOf("Text to Text", "Text to Image", "Text to Video"),
        generalCategories = listOf("Makanan", "Teknologi", "Pendidikan", "Hiburan"),
        selectedGeneralCategories = listOf("Teknologi"),
        isLoading = false,
        selectedImageUri = null
    )
}