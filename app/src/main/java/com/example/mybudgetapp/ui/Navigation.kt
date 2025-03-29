package com.example.mybudgetapp.ui

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.mybudgetapp.ui.screens.HomeScreen
import com.example.mybudgetapp.ui.screens.InfoScreen
import com.example.mybudgetapp.ui.screens.TransactionScreen
import com.example.mybudgetapp.ui.viewModel.CategoryViewModel
import com.example.mybudgetapp.ui.viewModel.DateAndMonthViewModel
import com.example.mybudgetapp.ui.viewModel.ExpenseViewModel
import com.example.mybudgetapp.ui.viewModel.MainCategoryViewModel

@Composable
fun Navigation(modifier: Modifier = Modifier, activity: Activity) {
    val navController = rememberNavController()

    // ViewModel instances
    val dateAndMonthViewModel: DateAndMonthViewModel = viewModel()
    val categoryViewModel: CategoryViewModel = viewModel()
    val mainCategoryViewModel: MainCategoryViewModel = viewModel()
    val expenseViewModel: ExpenseViewModel = viewModel()

    // Navigation setup
    NavHost(navController = navController, startDestination = "home") {
        composable(route = "home") {
            HomeScreen(
                navController,
                dateAndMonthViewModel,
                categoryViewModel,
                mainCategoryViewModel,
                expenseViewModel
            )
        }
        composable(route = "outflow") {
            TransactionScreen(
                navController,
                dateAndMonthViewModel,
                expenseViewModel
            )
        }
        composable(route = "info") {
            InfoScreen(navController)
        }
    }
}
