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
    //state variable to hold transaction details
    val transactionState = remember { mutableStateOf<Transaction?>(null) }

    //load the transaction details based on transactionId
    LaunchedEffect(transactionId) {
        viewModel.loadTransactionById(transactionId) { transaction ->
            transactionState.value = transaction
        }
    }

    //get the transaction details
    val transaction = transactionState.value

    Scaffold(
        topBar = { MainTopBar(navController, dateAndMonthViewModel, expenseViewModel) },
        bottomBar = { BottomBar(navController) }
    ) { innerPadding ->
        if (transaction != null) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(innerPadding)
                    .padding(16.dp)
            ) {
                //header for transaction details
                Text(
                    text = "Transaction Details",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(16.dp))

                //category and Description
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "Category: ${transaction.categoryName}",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Description: ${transaction.description}",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.Gray
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Subcategory: ${transaction.subCategoryName}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                //amount highlight with color based on transaction type
                Text(
                    text = "Amount: €${"%.2f".format(transaction.getAmountAsDouble())}",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (transaction.getTransactionType() == TransactionType.INCOME) Color.Green else Color.Red
                )
            }
        } else {
            //display a loading indicator while the transaction is being fetched
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
