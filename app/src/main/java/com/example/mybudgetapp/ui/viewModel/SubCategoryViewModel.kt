package com.example.mybudgetapp.ui.viewModel

import androidx.lifecycle.ViewModel
import com.example.mybudgetapp.data.SubCategoryRepository
import com.example.mybudgetapp.ui.model.CategoryItem
import com.example.mybudgetapp.ui.model.SubCategoryItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class SubCategoryViewModel: ViewModel() {
    // Repository instance to handle data operations
    private val repository = SubCategoryRepository()

    private val _categoryList = MutableStateFlow(
        listOf(
            CategoryItem(category = "Salary", amount = 4000f),
            CategoryItem(category = "Savings", amount = 400f)
        )
    )

    val categoryList: StateFlow<List<CategoryItem>> = _categoryList

    // Function to add a new category item
    fun addCategoryItem(category: String, amount: Float){
        if(category.isNotBlank() && amount > 0){
            val newItem = CategoryItem(category, amount)
            _categoryList.value += newItem
        }
    }

    fun getSubCategory(userId: String, budgetId: String, mainCatId: String, onResult: (List<SubCategoryItem>) -> Unit){
        repository.getSubCategory(userId, budgetId, mainCatId, onResult)
    }
}