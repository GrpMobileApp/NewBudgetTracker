package com.example.mybudgetapp.ui.screens


import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.mybudgetapp.ui.appbars.BottomBar
import com.example.mybudgetapp.ui.appbars.MainTopBar
import com.example.mybudgetapp.ui.viewModel.DateAndMonthViewModel
import com.example.mybudgetapp.ui.viewModel.ExpenseViewModel

@Composable
fun ChartScreen(navController: NavController, dateAndMonthViewModel: DateAndMonthViewModel,
                expenseViewModel: ExpenseViewModel,) {
    Scaffold(
        topBar = { MainTopBar(navController, dateAndMonthViewModel, expenseViewModel) },
        bottomBar = { BottomBar(navController) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            Text(
                text = "This is Chart Screen",
                style = MaterialTheme.typography.headlineMedium
            )
        }
    }
}

