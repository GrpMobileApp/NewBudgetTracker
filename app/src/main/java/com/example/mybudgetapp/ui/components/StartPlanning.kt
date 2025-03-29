package com.example.mybudgetapp.ui.components

import android.util.Log
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mybudgetapp.data.BudgetRepository
import com.example.mybudgetapp.ui.viewModel.BudgetViewModel
import com.example.mybudgetapp.ui.viewModel.DateAndMonthViewModel
import com.example.mybudgetapp.ui.viewModel.MainCategoryViewModel
import com.example.mybudgetapp.ui.viewModel.SharedViewModel
import com.example.mybudgetapp.ui.viewModel.SubCategoryViewModel
import kotlinx.coroutines.delay

@Composable
fun StartPlanning(
    subCategoryViewModel:SubCategoryViewModel,
    dateAndMonthViewModel: DateAndMonthViewModel,
    mainCategoryViewModel: MainCategoryViewModel
) {
    val sharedViewModel: SharedViewModel = viewModel()
    val userId = "v3Udk1WkxbV4YqoyNgLL"
    val month by dateAndMonthViewModel.selectedMonth.collectAsState()
    val year by dateAndMonthViewModel.selectedYear.collectAsState()
    val budgetRepository = BudgetRepository()

    // State to hold the budgetId
    var budgetId by remember { mutableStateOf<String?>(null) }
    // Loading state
    var isLoading by remember { mutableStateOf(true) }
    // State to hold the result of the fetch and save operation
    var isSavingComplete by remember { mutableStateOf(false) }

    // Fetch budgetId when month or year changes
    LaunchedEffect(month, year) {
        isLoading = true

        budgetRepository.getBudgetId(userId, month, year) { fetchedBudgetId ->
            if (fetchedBudgetId != null) {
                budgetId = fetchedBudgetId
                sharedViewModel.setBudgetId(fetchedBudgetId) // Store it in SharedViewModel

                // Save main categories only after fetching budgetId
                mainCategoryViewModel.saveMainCategories(userId, budgetId!!) { success ->
                    isSavingComplete = success
                    isLoading = false
                }
            } else {
                isLoading = false
            }
        }
    }

    // Show a message or HomeScreenContent based on the result
    if (isLoading) {
        LoadingSpinner()
    }else if (budgetId != null) {
        HomeScreenContent(mainCategoryViewModel, subCategoryViewModel)
    } else {
        // Show a loading state or an error message if saving fails
        Text("Failed to load budget. Please try again.")
    }
}