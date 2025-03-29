package com.example.mybudgetapp.ui.viewModel

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.mybudgetapp.ui.model.SubCategoryItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class SharedViewModel: ViewModel() {
    // To make accessible the budget id from anywhere
    private val _budgetId = MutableStateFlow<String?>(null)
    val budgetId: StateFlow<String?> = _budgetId

    fun setBudgetId(id:String?){
        Log.d("SharedViewModel", "Setting new BudgetId: $id")
        _budgetId.value = id
    }


}