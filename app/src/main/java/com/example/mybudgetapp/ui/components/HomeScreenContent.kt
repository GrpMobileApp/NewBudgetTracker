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
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
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
import com.example.mybudgetapp.data.MainCategoryWithSubcategories
import com.example.mybudgetapp.ui.model.CategoryItem
import com.example.mybudgetapp.ui.model.SubCategoryItem
import com.example.mybudgetapp.ui.viewModel.MainCategoryViewModel
import com.example.mybudgetapp.ui.viewModel.SharedViewModel
import com.example.mybudgetapp.ui.viewModel.SubCategoryViewModel
import com.google.firebase.auth.FirebaseAuth

@Composable
fun HomeScreenContent(mainCategoryViewModel: MainCategoryViewModel, subCategoryViewModel: SubCategoryViewModel) {
    val sharedViewModel: SharedViewModel = viewModel()
    val auth = FirebaseAuth.getInstance()

    val userId = auth.currentUser?.uid.toString()

    // Collect the main category list from the mainCategoryViewModel
    val mainCategoryList by mainCategoryViewModel.mainCategoryList.collectAsState()

    // State to control the visibility of the Add Category dialog(Controls popup visibility)
    var selectedCategory by remember { mutableStateOf<String?>(null) }


    // State to hold the budgetId
    val budgetId by sharedViewModel.budgetId.collectAsState()

    // State to hold the filtered subcategories for each main category
    val filteredSubCategories = remember {
        mutableStateOf<Map<String, List<SubCategoryItem>>>(emptyMap())
    }
    //call a function when screen appear
    LaunchedEffect(budgetId) {
        if (budgetId != null) {
            mainCategoryViewModel.fetchMainCategoryWithSubcategories(userId, budgetId!!)
        }
    }
    //Combined list of main categories with their subcategories
    val mainCategoryWithSubcategories by mainCategoryViewModel.mainCategoryWithSubcategories.collectAsState()

    //sort by main category
    val sortedMainCategoryWithSubcategories = remember(mainCategoryWithSubcategories) {
        mainCategoryWithSubcategories.sortedWith(compareBy(
            { it.mainCategoryName != "Income" },
            { it.mainCategoryName == "Other" }
        ))
    }

    //To track selected subcategory
    var selectedMainCategory by remember { mutableStateOf<MainCategoryWithSubcategories?>(null) }

    //To track selected subcategory
    var selectedSubCategory by remember { mutableStateOf<SubCategoryItem?>(null) }

    //state control the visibility of su category update dialog
    var showSubCategoryUpdateDialog by remember { mutableStateOf(false) }

    // Content of home screen
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Loop through each main category and display their subcategories
        sortedMainCategoryWithSubcategories.forEach { category ->

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
                        text = category.mainCategoryName.uppercase(),
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    // Display subcategories for this main category
                    category.subCategories.forEach {  subCat ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    selectedMainCategory = category
                                    selectedSubCategory = subCat
                                    showSubCategoryUpdateDialog = true

                                }
                                .padding(vertical = 4.dp), // Space between items
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = subCat.subCategoryName, fontSize = 15.sp)
                            Text(text = subCat.plannedAmount.toString(), fontSize = 15.sp)
                        }

                        // Add the separating line
                        HorizontalDivider(thickness = 1.dp, color = Color.LightGray)
                    }

                    // "Add Item" button
                    Spacer(modifier = Modifier.height(10.dp))

                    Button(onClick = { selectedCategory = category.mainCategoryName }) {
                        Text("Add Category")
                    }
                }
            }

        }
    }
    // Move Dialog OUTSIDE the loop
    if (selectedCategory != null) {
        AddCategoryDialog(
            onDismiss = { selectedCategory = null }, // Reset on close
            onSubmit = { name, plannedAmount ->
                subCategoryViewModel.addCategoryItem(
                    budgetId!!,
                    selectedCategory!!, // Use selectedCategory
                    plannedAmount.toDouble(),
                    name,
                    totalSpend = 0.0,
                    userId
                ){
                    selectedCategory = null  // Reset after submission
                    // Refresh main category list after adding a new subcategory
                    mainCategoryViewModel.fetchMainCategoryWithSubcategories(userId, budgetId!!)
                }
            },
            mainCategoryName = selectedCategory!! // Pass correct category name
        )
    }
    // Show dialog when user clicks on sub category
    if (showSubCategoryUpdateDialog && selectedMainCategory != null && selectedSubCategory != null) {
        UpdateDeleteSubCategoryDialog (
            onDismiss = {showSubCategoryUpdateDialog = false},
            mainCategoryViewModel = mainCategoryViewModel,
            subCategory = selectedSubCategory!!
        )
    }
}

