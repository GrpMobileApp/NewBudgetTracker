package com.example.mybudgetapp.ui.viewModel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class SharedViewModel: ViewModel() {
    // To make accessible the budget id from anywhere
    private val _budgetId = MutableStateFlow<String?>(null)
    val budgetId: StateFlow<String?> = _budgetId

    fun setBudgetId(id:String?){
        _budgetId.value = id
    }
}