package com.example.mybudgetapp.ui.model


data class SubCategoryItem(
    val budgetId: String,
    val mainCategoryName: String,
    val plannedAmount: Double,
    val remainingAmount:Double,
    val subCategoryName: String,
    val totalSpend: Double,
    val userId: String,
    val subCategoryId:String
)
