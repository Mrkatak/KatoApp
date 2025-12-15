package com.example.katoapp.view.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GeneralCategory(
    label: String,
    options: List<String>,
    selectedOptions: List<String>,
    onSelectionChanged: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    //display text utk oulined
    val displayText = if (selectedOptions.isEmpty()) {
        "Pilih Kategori..."
    } else {
        selectedOptions.joinToString(", ")
    }

    val textColor = if (selectedOptions.isEmpty()) Color.Gray else MaterialTheme.colorScheme.onSurface

    //dropdown expanded
    val zIndexValue = if (expanded) 1f else 0f

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = {
            expanded = it
        },
        modifier = modifier.zIndex(zIndexValue)
    ) {
        Box(
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
                .heightIn(min = 56.dp)
                .clip(RoundedCornerShape(12.dp))
                .border(
                    width = 1.dp,
                    color = if (expanded) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(horizontal = 16.dp, vertical = 12.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = displayText,
                    color = textColor,
                    style = MaterialTheme.typography.bodyLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )

//                Icon(
//                    imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.ArrowDropDown,
//                    contentDescription = null,
//                    tint = MaterialTheme.colorScheme.onSurfaceVariant
//                )
            }
        }

        //isi menu
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(MaterialTheme.colorScheme.surface)
        ) {
            if (options.isEmpty()) {
                DropdownMenuItem(
                    text = { Text("Tidak ada data", color = Color.Gray) },
                    onClick = { }
                )
            } else {
                options.forEach { option ->
                    val isSelected = selectedOptions.contains(option)

                    DropdownMenuItem(
                        text = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = option,
                                    modifier = Modifier.weight(1f),
                                    style = MaterialTheme.typography.bodyLarge
                                )
                                Checkbox(
                                    checked = isSelected,
                                    onCheckedChange = {
                                        onSelectionChanged(option)
                                    }
                                )
                            }
                        },
                        onClick = {
                            onSelectionChanged(option)
                        },
                        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                    )
                }
            }
        }
    }
}