package com.example.mybudgetapp.ui.viewModel

import androidx.lifecycle.ViewModel
import com.example.mybudgetapp.ui.model.ExpenseItem
import kotlinx.coroutines.flow.MutableStateFlow

class ExpenseViewModel:ViewModel() {
    private val _expenses = MutableStateFlow(
        listOf(
            ExpenseItem(category = "Income", subCategory = "salary" , note = "first week", amount = 4000f),
            ExpenseItem(category = "Income", subCategory = "Interest" , note = "op", amount = 4000f)
        )
    )
    val expense = _expenses

    //function to add expense
    fun addExpenseItem(category:String, subCategory:String, note: String, amount:Float){
        if (category.isNotBlank() && subCategory.isNotBlank() && amount > 0){
            val newItem = ExpenseItem(category,subCategory,note, amount)
            _expenses.value += newItem
        }
    }
}