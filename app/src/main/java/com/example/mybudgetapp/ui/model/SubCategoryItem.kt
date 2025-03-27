package com.example.mybudgetapp.ui.model

import com.google.firebase.firestore.PropertyName

data class SubCategoryItem(
    val name: String,
    @get:PropertyName("planned_amount") @set:PropertyName("planned_amount") var plannedAmount: Double = 0.0,
    @get:PropertyName("total_spend") @set:PropertyName("total_spend") var totalSpend: Double = 0.0
)
