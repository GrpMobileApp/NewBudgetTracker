package com.example.mybudgetapp.ui.viewModel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow

class MainCategoryViewModel: ViewModel() {
    private val _mainCategoryList = MutableStateFlow(
        listOf(
            "Income","Food","Household","Insurance", "Transport", "Other"
        )
    )
    val mainCategoryList = _mainCategoryList
}