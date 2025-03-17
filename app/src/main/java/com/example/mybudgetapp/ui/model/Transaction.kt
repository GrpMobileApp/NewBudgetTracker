package com.example.mybudgetapp.ui.model

//enum will differentiate income and expense
enum class TransactionType { INCOME, EXPENSE }

//it represent each transaction
data class Transaction(
    val name: String,
    val amount: Double,
    val type: TransactionType
)