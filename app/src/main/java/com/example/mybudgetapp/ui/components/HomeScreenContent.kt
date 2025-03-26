package com.example.mybudgetapp.ui.compone

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mybudgetapp.ui.components.AddCategoryDialog
import com.example.mybudgetapp.ui.viewModel.CategoryViewModel
import com.example.mybudgetapp.ui.viewModel.MainCategoryViewModel

@Composable
fun HomeScreenContent(mainCategoryViewModel: MainCategoryViewModel,categoryViewModel: CategoryViewModel) {
    // List of selectable budget categories (Planned, Spent, and Remaining)
    val mainOptionList = listOf("Planned", "Spent", "Remaining")
    // Declare mutable state for selected option(planned, spent or Remaining)
    var selectedOption by remember { mutableStateOf("Planned") }
    // Function to update the selected option(planned, spent or Remaining)
    fun onOptionSelected(opt: String){
        selectedOption = opt
    }

    // Collect the main category list from the mainCategoryViewModel
    val mainCategoryList by mainCategoryViewModel.mainCategoryList.collectAsState()

    // Collect the category list from the CategoryViewModel
    val categoryList by categoryViewModel.categoryList.collectAsState()

    // State to control the visibility of the Add Category dialog(Controls popup visibility)
    var showDialog by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Gray)
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        mainOptionList.forEach { option ->
            val isSelected = option == selectedOption
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .weight(1f)
                    .clickable {
                        selectedOption = option
                    }
                    .background(
                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(16.dp)
                    )
                    .padding(vertical = 8.dp)


            ) {
                Text(
                    text = option,
                    color = if (isSelected) MaterialTheme.colorScheme.primary else Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }

    //content of home screen
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        mainCategoryList.forEach { category ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 10.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier.padding(10.dp)
                ) {
                    Text(
                        text = category,
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    categoryList.forEachIndexed { index, cat ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp), // Space between items
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = cat.category, fontSize = 15.sp)
                            Text(text = cat.amount.toString(), fontSize = 15.sp)
                        }
                    }
                    // "Add Item" button
                    Spacer(modifier = Modifier.height(10.dp))

                    Button(onClick = { showDialog = true }) {
                        Text("Add Category")
                    }

                }
            }
        }
    }
    // Show dialog when user clicks "Add Category"
    if (showDialog) {
        AddCategoryDialog (
            onDismiss = { showDialog = false },
            onSubmit = { category, amount ->
                categoryViewModel.addCategoryItem(category, amount )
                showDialog = false
            }
        )
    }
}