package com.example.mybudgetapp.ui.viewModel

import androidx.lifecycle.ViewModel
import com.example.mybudgetapp.ui.model.Transaction
import com.example.mybudgetapp.ui.model.TransactionType

class TransactionViewModel : ViewModel() {
    //hardcoded transactions list
    val transactions = listOf(
        Transaction("Groceries", -50.0, TransactionType.EXPENSE),
        Transaction("Electricity Bill", -30.0, TransactionType.EXPENSE),
        Transaction("Shopping", -100.0, TransactionType.EXPENSE),
        Transaction("Salary", 1500.0, TransactionType.INCOME),
        Transaction("Freelancing", 500.0, TransactionType.INCOME),
        Transaction("Dining Out", -25.0, TransactionType.EXPENSE)
    )

    //fun to filter transactions based on search query
    fun getFilteredTransactions(query: String): List<Transaction> {
        return transactions.filter { it.name.contains(query, ignoreCase = true) }
    }
}
