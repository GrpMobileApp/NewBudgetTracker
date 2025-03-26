package com.example.mybudgetapp.ui.viewModel

import com.example.mybudgetapp.data.BudgetRepository

class BudgetViewModel {
    private val repository = BudgetRepository()

    fun getBudgetId(userId: String, month: String, year: String, onResult: (String?) -> Unit) {
        repository.getBudgetId(userId, month, year, onResult)
    }
}