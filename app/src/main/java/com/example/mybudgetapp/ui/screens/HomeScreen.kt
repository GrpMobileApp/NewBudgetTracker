package com.example.mybudgetapp.ui.screens



import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.mybudgetapp.ui.R
import com.example.mybudgetapp.ui.appbars.BottomBar
import com.example.mybudgetapp.ui.appbars.MainTopBar
import com.example.mybudgetapp.ui.components.HomeScreenContent
import com.example.mybudgetapp.ui.components.LoadingSpinner
import com.example.mybudgetapp.ui.components.RemainingScreenContent
import com.example.mybudgetapp.ui.components.SpentScreenContent
import com.example.mybudgetapp.ui.components.StartPlanning
import com.example.mybudgetapp.ui.viewModel.BudgetViewModel
import com.example.mybudgetapp.ui.viewModel.DateAndMonthViewModel
import com.example.mybudgetapp.ui.viewModel.ExpenseViewModel
import com.example.mybudgetapp.ui.viewModel.MainCategoryViewModel
import com.example.mybudgetapp.ui.viewModel.SharedViewModel
import com.example.mybudgetapp.ui.viewModel.SubCategoryViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

@Composable
fun HomeScreen(
    navController: NavController,
    dateAndMonthViewModel: DateAndMonthViewModel,
    subCategoryViewModel: SubCategoryViewModel,
    mainCategoryViewModel: MainCategoryViewModel,
    expenseViewModel: ExpenseViewModel
) {
    val budgetViewModel: BudgetViewModel = viewModel()
    val sharedViewModel: SharedViewModel = viewModel()
    val auth = FirebaseAuth.getInstance()

    // State to hold the budgetId
    val budgetId by sharedViewModel.budgetId.collectAsState()

    // Track plan button click state
    val isPlanningStarted by sharedViewModel.isPlanningStarted.collectAsState()
    // Context for Toast
    val context = LocalContext.current

    // List of selectable budget categories (Planned, Spent, and Remaining)
    val mainOptionList = listOf("Planned", "Spent", "Remaining")
    // Get selected option from ViewModel
    val selectedOption by sharedViewModel.selectedOption.collectAsState()

    // Add a loading state
    var isLoading by remember { mutableStateOf(true) }

    Scaffold (
        // Top bar receives a function to update the selected month and year
        topBar = { MainTopBar(navController, dateAndMonthViewModel, expenseViewModel) },
        bottomBar = { BottomBar(navController) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // selected month and year from ViewModel
            val selectedMonth by dateAndMonthViewModel.selectedMonth.collectAsState()
            val selectedYear by dateAndMonthViewModel.selectedYear.collectAsState()
            val userId = auth.currentUser?.uid.toString()

            // Fetch the budgetId when the month and year changes
            LaunchedEffect(selectedMonth.toString(), selectedYear.toString()) {
                budgetViewModel.getBudgetId(userId, selectedMonth, selectedYear) { id ->
                    sharedViewModel.setBudgetId(id)
                    isLoading = false
                    Log.d("HomeScreen", "Fetched budgetId: $id")
                }
            }


            // Content Row for main category options
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Gray)
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                mainOptionList.forEach { option ->
                    val isSelected = option == selectedOption
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .weight(1f)
                            .clickable { sharedViewModel.setSelectedOption(option) }
                            .background(
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.primary,
                                shape = RoundedCornerShape(16.dp)
                            )
                            .padding(vertical = 8.dp)
                    ) {
                        Text(
                            text = option,
                            color = if (isSelected) MaterialTheme.colorScheme.primary else Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            if (isLoading){
                LoadingSpinner()
            }else if (isPlanningStarted) {
                // Conditionally render StartPlanning composable
                StartPlanning(navController, dateAndMonthViewModel, mainCategoryViewModel)
            }else if (budgetId != null){
                // Handle UI based on budgetId
                when (selectedOption) {
                    "Planned" -> {
                        HomeScreenContent(mainCategoryViewModel, subCategoryViewModel)
                    }
                    "Spent" -> {
                        SpentScreenContent(mainCategoryViewModel, subCategoryViewModel)
                    }
                    "Remaining" -> {
                        RemainingScreenContent(mainCategoryViewModel, subCategoryViewModel)
                    }
                    else -> {
                        Log.d("HomeScreen", "Unknown option: $selectedOption")
                    }
                }
            } else {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 25.dp, start = 10.dp, end = 10.dp),
                    elevation = CardDefaults.elevatedCardElevation(defaultElevation = 10.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier.padding(10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Let's plan your month",
                            fontSize = 20.sp
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        Image(
                            painter = painterResource(id = R.drawable.budget),
                            contentDescription = "budget"
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "Select the relavant month in topbar",
                            fontSize = 20.sp
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Button(onClick = {
                            // Save budget on button click
                            budgetViewModel.saveBudget(userId, selectedMonth, selectedYear) { success ->
                                Toast.makeText(
                                    context,
                                    if (success) "Budget saved successfully!" else "Failed to save budget!",
                                    Toast.LENGTH_SHORT
                                ).show()

                                if (success) {
                                    sharedViewModel.setSelectedOption("Planned")
                                    sharedViewModel.setPlanningStarted(true)
                                }
                            }
                        }
                        ) {
                            Text(
                                text = "Start planning",
                                fontSize = 20.sp
                            )
                        }

                    }
                }
            }

        }
    }
}
