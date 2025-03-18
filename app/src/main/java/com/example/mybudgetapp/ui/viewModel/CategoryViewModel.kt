package com.example.mybudgetapp.ui.viewModel
import androidx.lifecycle.ViewModel
import com.example.mybudgetapp.ui.model.CategoryItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class CategoryViewModel:ViewModel() {
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
}