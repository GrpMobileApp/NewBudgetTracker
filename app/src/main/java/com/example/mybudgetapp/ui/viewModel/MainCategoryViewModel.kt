package com.example.mybudgetapp.ui.viewModel

import androidx.lifecycle.ViewModel
import com.example.mybudgetapp.data.MainCategoryRepository
import kotlinx.coroutines.flow.MutableStateFlow

class MainCategoryViewModel: ViewModel() {
    // Repository instance to handle data operations
    private val repository = MainCategoryRepository()
    private val _mainCategoryList = MutableStateFlow(
        listOf(
            "Income","Food","Household","Insurance", "Transport", "Other"
        )
    )
    val mainCategoryList = _mainCategoryList

    //Saves a new main category for the given user and budget.
    fun saveMainCategory(userId: String, budgetId:String, name: String, onResult: (Boolean) -> Unit) {
        repository.saveMainCategory(userId, budgetId, name, onResult)
    }
}