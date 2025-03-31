package com.example.mybudgetapp.ui.components


import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

import androidx.compose.ui.Modifier

import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.TextField
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.window.PopupProperties
import com.example.mybudgetapp.ui.viewModel.MainCategoryViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownLayout( listOfItems: List<String>,label: String, onSelectedCategory:(String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }  // Controls menu visibility
    var selectedText by remember { mutableStateOf("") } // No default value (empty string)
    Box(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        // Wrapping TextField in ExposedDropdownMenuBox to make it interactable
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded } // Toggle menu visibility
        ) {
            TextField(
                value = selectedText,
                onValueChange = {},
                readOnly = true,  // Prevent manual typing
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth(),
                label = { Text(label) },
                trailingIcon = {
                    Icon(Icons.Default.ArrowDropDown, contentDescription = "Dropdown")
                }
            )

            // Dropdown Menu
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier
                    .width(285.dp),
                properties = PopupProperties(focusable = true) // Ensures dropdown stays open in dialog

            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp), // Apply rounded corners
                    tonalElevation = 8.dp // Optional: Adds elevation for a shadow effect
                ) {
                    Column {
                        // Iterate over the items and make them clickable
                        listOfItems.forEachIndexed { index, item ->
                            DropdownMenuItem(
                                text = { Text(item) },
                                onClick = {
                                    selectedText = item  // Set selected item
                                    onSelectedCategory(item)
                                    expanded = false
                                },
                            )
                            if (index < listOfItems.lastIndex) {
                                HorizontalDivider(
                                    thickness = 0.5.dp,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.padding(start = 5.dp, end = 5.dp)
                                ) // Adds separator
                            }
                        }
                    }
                }
            }
        }
    }
}