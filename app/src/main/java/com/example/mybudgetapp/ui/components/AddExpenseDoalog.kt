package com.example.mybudgetapp.ui.components

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.media.app.NotificationCompat
import com.example.mybudgetapp.ui.MyFirebaseMessagingService
import com.example.mybudgetapp.ui.R
import com.example.mybudgetapp.ui.viewModel.MainCategoryViewModel
import com.example.mybudgetapp.ui.model.SubCategoryItem
import com.example.mybudgetapp.ui.viewModel.ExpenseViewModel
import com.example.mybudgetapp.ui.viewModel.SharedViewModel
import com.example.mybudgetapp.ui.viewModel.SubCategoryViewModel
import com.google.firebase.auth.FirebaseAuth


@Composable
fun AddExpenseDialog(
    onDismiss: () -> Unit,
    expenseViewModel: ExpenseViewModel,
    mainCategoryViewModel: MainCategoryViewModel
){
    val context = LocalContext.current
    var category by remember { mutableStateOf("") }
    var subCategory by remember { mutableStateOf("") }
    var subCategoryId by remember { mutableStateOf("") }
    var totalSpend by remember { mutableStateOf(0.0) }
    var remainingAmount by remember { mutableStateOf(0.0) }
    var plannedAmount by remember { mutableStateOf(0.0) }
    var note by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    val sharedViewModel: SharedViewModel = viewModel()
    val subCategoryViewModel: SubCategoryViewModel = viewModel()
    val auth = FirebaseAuth.getInstance()
    var transactionType by remember { mutableStateOf("") }


    // State to hold the budgetId
    val budgetId by sharedViewModel.budgetId.collectAsState()
    val userId = auth.currentUser?.uid.toString()

    val mainCategoryList by mainCategoryViewModel.mainCategoryList.collectAsState()
    val mainCategoryWithSubcategories by mainCategoryViewModel.mainCategoryWithSubcategories.collectAsState()

    // Filtered subcategories based on selected main category
    var subCategoriesOfSelectedMain by remember { mutableStateOf<List<SubCategoryItem>>(emptyList()) }

    // Fetch main categories & subcategories when dialog opens
    LaunchedEffect(Unit) {
        mainCategoryViewModel.fetchMainCategoryWithSubcategories(userId,budgetId!!)
    }
    val amountValue = amount.toFloatOrNull() ?: 0f
    val isFormValid = category.isNotBlank() &&
            subCategory.isNotBlank() &&
            note.isNotBlank() &&
            !amountValue.isNaN() &&
            amountValue > 0

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
                Text(
                    text = "Add Transaction",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 10.dp)
                )
                Spacer(modifier = Modifier.height(10.dp))

                // Use DropdownLayout for Category
                DropdownLayout(
                    listOfItems = mainCategoryList,
                    label = "Select Category"
                ) { selectedCategory ->
                    category = selectedCategory
                    Log.d("AddExpenseDialog", "selectedCategory '$selectedCategory' saved.")
                    //update transaction type
                    transactionType = if(category === "Income") "income" else "expense"

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
                    totalSpend = selectedSubCategoryItem?.totalSpend ?: 0.00
                    remainingAmount = selectedSubCategoryItem?.remainingAmount ?: 0.00
                    plannedAmount = selectedSubCategoryItem?.plannedAmount ?: 0.00
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

                            if (budgetId != null && !subCategoryId.isNullOrBlank() && category.isNotBlank() && subCategory.isNotBlank() && amountValue > 0) {
                                val finalAmount = amountValue.toDouble()
                                val newSpend = totalSpend + finalAmount
                                val newRemaining = remainingAmount - finalAmount

                                expenseViewModel.storeExpense(
                                    userId = userId,
                                    budgetId = budgetId !!,
                                    categoryName = category,
                                    subCategoryId = subCategoryId,
                                    subCategoryName = subCategory,
                                    description = note,
                                    amount = amountValue.toDouble(),
                                    date = java.sql.Timestamp(System.currentTimeMillis()),
                                    transactionType = transactionType,
                                    onResult = { success -> if (success) onDismiss() }
                                )
                                subCategoryViewModel.updateTotalSpendAndRemaining(
                                    subCategoryId = subCategoryId,
                                    totalSpend = newSpend,
                                    remaining = newRemaining,
                                ) { success ->
                                    if (success) {
                                        Log.d(
                                            "AddExpenseDialog", "Successfully updated totalSpend and remainingAmount"
                                        )
                                        if (newRemaining <= 0) {
                                            val notificationService = MyFirebaseMessagingService()
                                            notificationService.sendOverspendNotification(context, subCategory)
                                        }
                                    } else {
                                        Log.e(
                                            "AddExpenseDialog", "Failed to update totalSpend and remainingAmount"
                                        )
                                    }
                                }
                            }
                        },
                        enabled = isFormValid
                    ) {
                        Text("Submit")
                    }
                }
            }
        }
    }
}
