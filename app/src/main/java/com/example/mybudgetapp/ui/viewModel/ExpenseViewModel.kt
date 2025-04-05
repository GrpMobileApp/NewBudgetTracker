package com.example.mybudgetapp.ui.viewModel

import androidx.lifecycle.ViewModel
import com.example.mybudgetapp.data.TransactionRepository
import com.example.mybudgetapp.ui.model.ExpenseItem
import kotlinx.coroutines.flow.MutableStateFlow
import java.sql.Timestamp

class ExpenseViewModel:ViewModel() {
    private val repository = TransactionRepository()
    /*private val _expenses = MutableStateFlow(
        listOf(
            ExpenseItem(category = "Income", subCategory = "salary" , note = "first week", amount = 4000f),
            ExpenseItem(category = "Income", subCategory = "Interest" , note = "op", amount = 4000f)
        )
    )
    val expense = _expenses*/

    //function to add expense
    fun storeExpense(
        userId: String,
        budgetId:String,
        categoryName:String,
        subCategoryId:String,
        subCategoryName:String,
        description:String,
        amount:Double,
        date: Timestamp,
        onResult: (Boolean) -> Unit
    ){
        /*if (category.isNotBlank() && subCategory.isNotBlank() && amount > 0){
            val newItem = ExpenseItem(category,subCategory,note, amount)
            _expenses.value += newItem
        }
        */
        repository.saveTransactions(userId,budgetId,categoryName,subCategoryId,subCategoryName,description,amount,date,onResult)
    }

    fun removeRelevantTransactions(subCategoryId: String, onResult :(Boolean) -> Unit){
        repository.deleteTransactions(subCategoryId, onResult)
    }
}