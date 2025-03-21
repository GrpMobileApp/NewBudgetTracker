package com.example.mybudgetapp.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.mybudgetapp.ui.screens.HomeScreen
import com.example.mybudgetapp.ui.screens.InfoScreen
import com.example.mybudgetapp.ui.screens.TransactionScreen
import com.example.mybudgetapp.ui.theme.MyBudgetAppTheme
import com.example.mybudgetapp.ui.viewModel.CategoryViewModel
import com.example.mybudgetapp.ui.viewModel.DateAndMonthViewModel
import com.example.mybudgetapp.ui.viewModel.ExpenseViewModel
import com.example.mybudgetapp.ui.viewModel.MainCategoryViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyBudgetAppTheme {
                MyBudgetApp()
            }
        }
    }
}

@Composable
fun MyBudgetApp() {
    val navController = rememberNavController()
    //create viewmodel instance
    val dateAndMonthViewModel:DateAndMonthViewModel = viewModel()
    val categoryViewModel:CategoryViewModel = viewModel()
    val mainCategoryViewModel:MainCategoryViewModel = viewModel()
    val expenseViewModel:ExpenseViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = "home"
    ){
        composable(route = "home") { HomeScreen(navController, dateAndMonthViewModel, categoryViewModel, mainCategoryViewModel, expenseViewModel ) }
        composable(route = "outflow") { TransactionScreen(navController, dateAndMonthViewModel, expenseViewModel) }
        composable(route = "info") { InfoScreen(navController) }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MyBudgetAppTheme {
        MyBudgetApp()
    }
}