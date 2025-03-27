package com.example.mybudgetapp.ui.viewModel

import androidx.lifecycle.ViewModel
import com.example.mybudgetapp.data.SubCategoryRepository
import com.example.mybudgetapp.ui.model.SubCategoryItem

class SubCategoryViewModel: ViewModel() {
    // Repository instance to handle data operations
    private val repository = SubCategoryRepository()

    fun getSubCategory(userId: String, budgetId: String, mainCatId: String, onResult: (List<SubCategoryItem>) -> Unit){
        repository.getSubCategory(userId, budgetId, mainCatId, onResult)
    }
}