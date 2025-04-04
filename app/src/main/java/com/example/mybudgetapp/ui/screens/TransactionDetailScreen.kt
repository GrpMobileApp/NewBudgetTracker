package com.example.mybudgetapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.mybudgetapp.ui.appbars.BottomBar
import com.example.mybudgetapp.ui.appbars.MainTopBar
import com.example.mybudgetapp.ui.model.Transaction
import com.example.mybudgetapp.ui.model.TransactionType
import com.example.mybudgetapp.ui.viewModel.DateAndMonthViewModel
import com.example.mybudgetapp.ui.viewModel.ExpenseViewModel
import com.example.mybudgetapp.ui.viewModel.TransactionViewModel

@Composable
fun TransactionDetailScreen(
    navController: NavController,
    dateAndMonthViewModel: DateAndMonthViewModel,
    expenseViewModel: ExpenseViewModel,
    transactionId: String,
    viewModel: TransactionViewModel = viewModel()
) {
    // State to hold the transaction details
    var transaction by remember { mutableStateOf<Transaction?>(null) }

    // Fetch the transaction details using the transactionId
    LaunchedEffect(transactionId) {
        viewModel.loadTransactionById(transactionId) { fetchedTransaction ->
            transaction = fetchedTransaction
        }
    }

    // Display the transaction details
    if (transaction != null) {
        Scaffold(
            topBar = { MainTopBar(navController, dateAndMonthViewModel, expenseViewModel) },
            bottomBar = { BottomBar(navController) }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(paddingValues)
                    .padding(16.dp)
            ) {
                Text(
                    text = "Category: ${transaction?.categoryName}",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Subcategory: ${transaction?.subCategoryName}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Amount: €${"%.2f".format(transaction?.amount)}",
                    fontWeight = FontWeight.Bold,
                    color = if (transaction?.getTransactionType() == TransactionType.INCOME) Color.Green else Color.Red
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Description: ${transaction?.description}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Date: ${transaction?.date?.toString() ?: "Unknown"}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    } else {
        // Show loading spinner while fetching transaction
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    }
}
