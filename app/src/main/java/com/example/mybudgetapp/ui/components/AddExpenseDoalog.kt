package com.example.mybudgetapp.ui.components

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mybudgetapp.ui.viewModel.MainCategoryViewModel
import com.example.mybudgetapp.ui.model.SubCategoryItem
import com.example.mybudgetapp.ui.viewModel.ExpenseViewModel
import com.example.mybudgetapp.ui.viewModel.SharedViewModel


@Composable
fun AddExpenseDialog(
    onDismiss: () -> Unit,
    expenseViewModel: ExpenseViewModel,
    mainCategoryViewModel: MainCategoryViewModel
){
    var category by remember { mutableStateOf("") }
    var subCategory by remember { mutableStateOf("") }
    var subCategoryId by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    val sharedViewModel: SharedViewModel = viewModel()

    // State to hold the budgetId
    val budgetId by sharedViewModel.budgetId.collectAsState()
    val userId = "v3Udk1WkxbV4YqoyNgLL"

    val mainCategoryList by mainCategoryViewModel.mainCategoryList.collectAsState()
    val mainCategoryWithSubcategories by mainCategoryViewModel.mainCategoryWithSubcategories.collectAsState()

    // Filtered subcategories based on selected main category
    var subCategoriesOfSelectedMain by remember { mutableStateOf<List<SubCategoryItem>>(emptyList()) }

    // Fetch main categories & subcategories when dialog opens
    LaunchedEffect(Unit) {
        mainCategoryViewModel.fetchMainCategoryWithSubcategories(userId,budgetId!!)
    }

    Dialog (onDismissRequest = { onDismiss() }) {
        Surface (
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White),
            shape = MaterialTheme.shapes.medium
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                Text(text = "Add Expense", style = MaterialTheme.typography.headlineSmall)
                Spacer(modifier = Modifier.height(10.dp))

                // Use DropdownLayout for Category
                DropdownLayout(
                    listOfItems = mainCategoryList,
                    label = "Select Category"
                ) { selectedCategory ->
                    category = selectedCategory
                    Log.d("AddExpenseDialog", "selectedCategory '$selectedCategory' saved.")

                    // Update subcategories based on the selected category
                    val selectedMainCategory = mainCategoryWithSubcategories.find { it.mainCategoryName == category }
                    subCategoriesOfSelectedMain = selectedMainCategory?.subCategories ?: emptyList()
                    Log.d("AddExpenseDialog", "Subcategories for '$category': ${subCategoriesOfSelectedMain.map { it.subCategoryName }}")
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Dropdown for Subcategory
                DropdownLayout(
                    listOfItems = subCategoriesOfSelectedMain.map { it.subCategoryName },
                    label = "Select Subcategory"
                ) { selectedSubCategory ->
                    subCategory = selectedSubCategory

                    // Find the selected subcategory item to get its ID
                    val selectedSubCategoryItem = subCategoriesOfSelectedMain.find { it.subCategoryName == selectedSubCategory }
                    subCategoryId = selectedSubCategoryItem?.subCategoryId ?: ""
                }

                Spacer(modifier = Modifier.height(10.dp))

                TextField(
                    value = note,
                    onValueChange = { note = it },
                    label = { Text("Enter a Note") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(10.dp))

                TextField(
                    value = amount,
                    onValueChange = { amount = it },
                    label = { Text("Enter Amount") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(20.dp))

                Row {
                    Button(onClick = { onDismiss() }) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.weight(1f))

                    Button(
                        onClick = {
                            val amountValue = amount.toFloatOrNull() ?: 0f
                            if (budgetId != null && !subCategoryId.isNullOrBlank() && category.isNotBlank() && subCategory.isNotBlank() && amountValue > 0) {
                                expenseViewModel.storeExpense(
                                    userId = userId,
                                    budgetId = budgetId !!,
                                    categoryName = category,
                                    subCategoryId = subCategoryId,
                                    subCategoryName = subCategory,
                                    description = note,
                                    amount = amountValue.toDouble(),
                                    date = java.sql.Timestamp(System.currentTimeMillis()),
                                    onResult = { success -> if (success) onDismiss() }
                                )
                            }
                        }
                    ) {
                        Text("Submit")
                    }
                }
            }
        }
    }
}