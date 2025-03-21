package com.example.mybudgetapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
fun TransactionScreen(navController: NavController,dateAndMonthViewModel: DateAndMonthViewModel, expenseViewModel: ExpenseViewModel, viewModel: TransactionViewModel = viewModel()) {
    var searchQuery by remember { mutableStateOf("") }

    //filter transactions when the searchQuery changes
    val filteredTransactions by remember(searchQuery) {
        mutableStateOf(viewModel.getFilteredTransactions(searchQuery))
    }
//for common structure of  transaction screen and it include top bar and buttom bar
    Scaffold(
        topBar = { MainTopBar(navController, dateAndMonthViewModel, expenseViewModel ) },
        bottomBar = { BottomBar(navController) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            // selected month and year from ViewModel
            val selectedMonth by dateAndMonthViewModel.selectedMonth.collectAsState()
            val selectedYear by dateAndMonthViewModel.selectedYear.collectAsState()

            //search bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Search Transactions") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            //display transactions
            LazyColumn {
                items(filteredTransactions) { transaction ->
                    TransactionItem(transaction)
                }
            }
        }
    }
}

//to display each individual transaction in the list
@Composable
fun TransactionItem(transaction: Transaction) {
    val backgroundColor =
        if (transaction.type == TransactionType.INCOME) Color(0xFFD0F0C0) else Color(0xFFFFD6D6)
    val textColor = if (transaction.type == TransactionType.INCOME) Color(0xFF006400) else Color.Red
//display each transaction in a material-styled container with padding
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween // Align items to opposite ends
        )
        {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = transaction.name, style = MaterialTheme.typography.bodyLarge)
                Text(
                    text = "€${transaction.amount}",
                    fontWeight = FontWeight.Bold,
                    color = textColor
                )
            }
            //Edit icon for each list
            IconButton(onClick = { }) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit Transaction",
                    tint = Color.Gray
                )
            }
            //Delete icon for each list
            IconButton(onClick = { }) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete Transaction",
                    tint = Color.Gray
                )
            }
        }
    }
}