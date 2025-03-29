package com.example.mybudgetapp.ui.components

import android.util.Log
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mybudgetapp.ui.model.CategoryItem
import com.example.mybudgetapp.ui.model.SubCategoryItem
import com.example.mybudgetapp.ui.viewModel.MainCategoryViewModel
import com.example.mybudgetapp.ui.viewModel.SharedViewModel
import com.example.mybudgetapp.ui.viewModel.SubCategoryViewModel

@Composable
fun HomeScreenContent(mainCategoryViewModel: MainCategoryViewModel, subCategoryViewModel: SubCategoryViewModel) {
    val sharedViewModel: SharedViewModel = viewModel()
    val userId = "v3Udk1WkxbV4YqoyNgLL"

    // List of selectable budget categories (Planned, Spent, and Remaining)
    val mainOptionList = listOf("Planned", "Spent", "Remaining")
    // Declare mutable state for selected option(planned, spent or Remaining)
    var selectedOption by remember { mutableStateOf("Planned") }

    // Function to update the selected option(planned, spent or Remaining)
    fun onOptionSelected(opt: String) {
        selectedOption = opt
    }

    // Collect the main category list from the mainCategoryViewModel
    val mainCategoryList by mainCategoryViewModel.mainCategoryList.collectAsState()

    // State to control the visibility of the Add Category dialog(Controls popup visibility)
    var showDialog by remember { mutableStateOf(false) }

    // State to hold the budgetId
    val budgetId by sharedViewModel.budgetId.collectAsState()

    // State to hold the filtered subcategories for each main category
    val filteredSubCategories = remember {
        mutableStateOf<Map<String, List<SubCategoryItem>>>(emptyMap())
    }
    //call a function when screen appear
    LaunchedEffect (userId, budgetId){
        budgetId?.let {
            mainCategoryViewModel.fetchMainCategoryWithSubcategories(userId,it)
        }
    }
    //Combined list of main categories with their subcategories
    val mainCategoryWithSubcategories by mainCategoryViewModel.mainCategoryWithSubcategories.collectAsState()

    // Content Row for main category options
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
                    .clickable { selectedOption = option }
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

    // Content of home screen
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Loop through each main category and display their subcategories
        mainCategoryWithSubcategories.forEach { category ->

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
                    Text(text = category.mainCategoryName.uppercase())
                    Spacer(modifier = Modifier.height(16.dp))

                    // Display subcategories for this main category
                    category.subCategories.forEach {  subCat ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp), // Space between items
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = subCat.subCategoryName, fontSize = 15.sp)
                            Text(text = subCat.plannedAmount.toString(), fontSize = 15.sp)
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
/*
    // Show dialog when user clicks "Add Category"
    if (showDialog) {
        AddCategoryDialog(
            onDismiss = { showDialog = false },
            onSubmit = { name, plannedAmount ->
                subCategoryViewModel.addCategoryItem(name, plannedAmount)
                showDialog = false
            }
        )
    }

 */
}