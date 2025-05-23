package com.example.mybudgetapp.ui.viewModel

import androidx.lifecycle.ViewModel
import com.example.mybudgetapp.data.BudgetRepository

class BudgetViewModel:ViewModel() {
    private val repository = BudgetRepository()

    fun getBudgetId(userId: String, month: String, year: String, onResult: (String?) -> Unit) {
        repository.getBudgetId(userId, month, year, onResult)
    }

    fun saveBudget(userId: String, month:String, year: String, onResult: (Boolean) -> Unit) {
        repository.storeBudget(userId, month, year, onResult)
    }
}