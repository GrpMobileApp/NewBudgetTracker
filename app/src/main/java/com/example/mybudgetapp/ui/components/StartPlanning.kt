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
import androidx.navigation.NavController
import com.example.mybudgetapp.data.BudgetRepository
import com.example.mybudgetapp.ui.viewModel.BudgetViewModel
import com.example.mybudgetapp.ui.viewModel.DateAndMonthViewModel
import com.example.mybudgetapp.ui.viewModel.MainCategoryViewModel
import com.example.mybudgetapp.ui.viewModel.SharedViewModel
import com.example.mybudgetapp.ui.viewModel.SubCategoryViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay

@Composable
fun StartPlanning(
    navController: NavController,
    dateAndMonthViewModel: DateAndMonthViewModel,
    mainCategoryViewModel: MainCategoryViewModel
) {
    val sharedViewModel: SharedViewModel = viewModel()
    val auth = FirebaseAuth.getInstance()

    val userId = auth.currentUser?.uid.toString()
    val month by dateAndMonthViewModel.selectedMonth.collectAsState()
    val year by dateAndMonthViewModel.selectedYear.collectAsState()
    val budgetRepository = BudgetRepository()

    // State to hold the budgetId
    var budgetId by remember { mutableStateOf<String?>(null) }
    // Loading state
    var isLoading by remember { mutableStateOf(true) }
    // State to hold the result of the fetch and save operation
    var isSavingComplete by remember { mutableStateOf(false) }

    LaunchedEffect(isSavingComplete) {
        if (isSavingComplete) {
            delay(50)
            navController.navigate("home") {
                popUpTo("startPlanning") { inclusive = true }
            }
        }
    }

    // Fetch budgetId when month or year changes
    LaunchedEffect(month, year) {
        if (budgetId == null) { // Avoid redundant calls
            isLoading = true
            budgetRepository.getBudgetId(userId, month, year) { fetchedBudgetId ->
                fetchedBudgetId?.let {
                    budgetId = it
                    sharedViewModel.setBudgetId(it) // Store it in SharedViewModel

                    // Save main categories only after fetching budgetId
                    mainCategoryViewModel.saveMainCategories(userId, it) { success ->
                        isSavingComplete = success
                        isLoading = false
                    }
                } ?: run {
                    isLoading = false
                }
            }
        }
    }

    // UI logic
    if (isLoading) {
        LoadingSpinner()
    } else if (!isSavingComplete || budgetId == null) {
        Text("Failed to load budget. Please try again.")
    }
}