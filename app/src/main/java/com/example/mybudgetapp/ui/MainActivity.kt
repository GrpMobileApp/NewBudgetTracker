package com.example.mybudgetapp.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.mybudgetapp.ui.screens.HomeScreen
import com.example.mybudgetapp.ui.screens.InfoScreen
import com.example.mybudgetapp.ui.screens.TransactionScreen
import com.example.mybudgetapp.ui.theme.MyBudgetAppTheme
import com.example.mybudgetapp.ui.viewModel.DateAndMonthViewModel
import com.example.mybudgetapp.ui.viewModel.ExpenseViewModel
import com.example.mybudgetapp.ui.viewModel.MainCategoryViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        /*This is the correct one but still not implemented
        setContent {
            MyBudgetAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Navigation(
                        modifier = Modifier.padding(innerPadding),
                        authViewModel = authViewModel,
                        activity = this
                    )
                }
            }
        }*/
        setContent {
            MyBudgetAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Navigation(modifier = Modifier.padding(innerPadding),
                        activity = this)
                }
            }
        }
    }
}
