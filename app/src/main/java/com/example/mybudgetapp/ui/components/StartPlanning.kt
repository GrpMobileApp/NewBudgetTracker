package com.example.mybudgetapp.ui.components

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.example.mybudgetapp.ui.viewModel.BudgetViewModel
import com.example.mybudgetapp.ui.viewModel.DateAndMonthViewModel
import com.example.mybudgetapp.ui.viewModel.MainCategoryViewModel

@Composable
fun StartPlanning(
    budgetViewModel: BudgetViewModel,
    dateAndMonthViewModel: DateAndMonthViewModel,
    mainCategoryViewModel: MainCategoryViewModel
) {
    val userId = "v3Udk1WkxbV4YqoyNgLL"
    val month by dateAndMonthViewModel.selectedMonth.collectAsState()
    val year by dateAndMonthViewModel.selectedYear.collectAsState()

    // State to hold the budgetId
    var budgetId by remember { mutableStateOf<String?>(null) }

    // Collect the main category list from the mainCategoryViewModel
    val mainCategoryList by mainCategoryViewModel.mainCategoryList.collectAsState()


    // Fetch the budgetId
    LaunchedEffect(month.toString(), year.toString()) {
        budgetViewModel.getBudgetId(userId, month, year) { id ->
            budgetId = id

            // Check budgetId is not null and enter every maincategory, from list to firebase
            id?.let { validBudgetId ->
                mainCategoryList.forEach { category ->
                    mainCategoryViewModel.saveMainCategory(userId, validBudgetId, category) { success ->
                        Log.d("StartPlanning", "Category '$category' saved: $success")
                    }
                }
            }
        }
    }
}