package com.example.mybudgetapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
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
import com.google.firebase.auth.FirebaseAuth

@Composable
fun TransactionScreen(
    navController: NavController,
    dateAndMonthViewModel: DateAndMonthViewModel,
    expenseViewModel: ExpenseViewModel,
    viewModel: TransactionViewModel = viewModel()
) {
    var searchQuery by remember { mutableStateOf("") }

    //get logged-in user ID
    val userId = FirebaseAuth.getInstance().currentUser?.uid

    //observe transactions from ViewModel
    val transactions by viewModel.transactions.observeAsState(emptyList())

    //filter transactions based on search query
    val filteredTransactions by remember(searchQuery, transactions) {
        derivedStateOf {
            transactions.filter { it.subCategoryName.contains(searchQuery, ignoreCase = true) }
        }
    }

    //load transactions for the logged-in user
    LaunchedEffect(userId) {
        userId?.let { viewModel.loadTransactions(it) }
    }

    Scaffold(
        topBar = { MainTopBar(navController, dateAndMonthViewModel, expenseViewModel) },
        bottomBar = { BottomBar(navController) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            //search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Search Transactions") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            //display filtered transactions
            LazyColumn {
                items(filteredTransactions) { transaction ->
                    TransactionItem(transaction, viewModel)
                }
            }
        }
    }
}

@Composable
fun TransactionItem(transaction: Transaction, viewModel: TransactionViewModel) {
    //state variable to control the visibility of the delete confirmation dialog
    var showDialog by remember { mutableStateOf(false) }
    //for edit
    var showEditDialog by remember { mutableStateOf(false) }

    //determine the background color based on the transaction type
    val backgroundColor =
        if (transaction.getTransactionType() == TransactionType.INCOME) Color(0xFFD0F0C0) else Color(0xFFFFD6D6)
    //determine the text color based on the transaction type
    val textColor = if (transaction.getTransactionType() == TransactionType.INCOME) Color(0xFF006400) else Color.Red

    //display an alert dialog when the user attempts to delete a transaction
    if (showDialog) {
        AlertDialog(
            //close dialog when dismissed
            onDismissRequest = { showDialog = false },
            title = { Text("Delete Transaction") },
            text = { Text("Are you sure you want to delete this transaction?") },
            confirmButton = {
                TextButton(onClick = {
                    //call ViewModel to delete transaction
                    viewModel.deleteTransaction(transaction)
                    //close the dialog after deletion
                    showDialog = false
                }) {
                    Text("Delete", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    //close dialog without deleting
                    Text("Cancel")
                }
            }
        )
    }

    //for edit dialog
    if (showEditDialog) {
        EditTransactionDialog(
            transaction = transaction,
            onDismiss = { showEditDialog = false },
            onSave = { updatedTransaction ->
                viewModel.updateTransaction(updatedTransaction)
                showEditDialog = false
            }
        )
    }

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
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = transaction.subCategoryName, style = MaterialTheme.typography.bodyLarge)
                Text(
                    //convert amount safely to double
                    text = "€${transaction.getAmountAsDouble()}",
                    fontWeight = FontWeight.Bold,
                    color = textColor
                )
            }
            //row for edit and delete buttons
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                IconButton(onClick = { showEditDialog = true }) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit Transaction",
                        tint = Color.Gray
                    )
                }

                IconButton(onClick = { showDialog = true }) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete Transaction",
                        tint = Color.Gray
                    )
                }
            }
        }
    }
}

//for edit transaction
@Composable
fun EditTransactionDialog(
    transaction: Transaction,
    onDismiss: () -> Unit,
    onSave: (Transaction) -> Unit
) {
    //state variables to hold the input values
    var categoryName by remember { mutableStateOf(transaction.categoryName) }
    var description by remember { mutableStateOf(transaction.description) }
    var amount by remember { mutableStateOf(transaction.getAmountAsDouble().toString()) }
    var subCategoryName by remember { mutableStateOf(transaction.subCategoryName) }

    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text("Edit Transaction") },
        text = {
            Column {
                //input for category name
                OutlinedTextField(
                    value = categoryName,
                    onValueChange = { categoryName = it },
                    label = { Text("Category Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                //input for description
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                //input for amount
                OutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it },
                    label = { Text("Amount") },
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
                //input for subcategory name
                OutlinedTextField(
                    value = subCategoryName,
                    onValueChange = { subCategoryName = it },
                    label = { Text("Subcategory Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val updatedTransaction = transaction.copy(
                    categoryName = categoryName,
                    description = description,
                    amount = amount.toDoubleOrNull() ?: transaction.amount
                )
                onSave(updatedTransaction)
            }) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = { onDismiss() }) {
                Text("Cancel")
            }
        }
    )
}
