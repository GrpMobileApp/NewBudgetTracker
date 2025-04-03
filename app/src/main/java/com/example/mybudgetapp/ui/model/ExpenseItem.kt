package com.example.mybudgetapp.ui.model

data class ExpenseItem(
    val userId: String,
    val budgetId: String,
    val category_name: String,
    val sub_category_id: Float,
    val sub_category_name: String,
    val description: String,
    val amount: Double,
    val date: Float
)
