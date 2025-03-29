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
import com.example.mybudgetapp.ui.viewModel.DateAndMonthViewModel
import com.example.mybudgetapp.ui.viewModel.ExpenseViewModel
import com.example.mybudgetapp.ui.viewModel.MainCategoryViewModel
import com.example.mybudgetapp.ui.viewModel.SubCategoryViewModel


@Composable
//fun Navigation(modifier: Modifier = Modifier, authViewModel: AuthViewModel, activity: Activity) {
fun Navigation(modifier: Modifier = Modifier, activity: Activity) {
    val navController = rememberNavController()
    // ViewModel instances
    val dateAndMonthViewModel: DateAndMonthViewModel = viewModel()
    val subCategoryViewModel: SubCategoryViewModel = viewModel()
    val mainCategoryViewModel: MainCategoryViewModel = viewModel()
    val expenseViewModel: ExpenseViewModel = viewModel()

    // Navigation setup
    NavHost(navController = navController, startDestination = "home") {
        //this is for Signin screen
        composable(route = "home") {
            HomeScreen(
                navController,
                dateAndMonthViewModel,
                subCategoryViewModel,
                mainCategoryViewModel,
                expenseViewModel
            )
        }
        //this is for Signup screen
        composable(route = "home") {
            HomeScreen(navController,
                dateAndMonthViewModel,
                subCategoryViewModel,
                mainCategoryViewModel,
                expenseViewModel )
        }
        composable(route = "home") {
            HomeScreen(
                navController,
                dateAndMonthViewModel,
                subCategoryViewModel,
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
