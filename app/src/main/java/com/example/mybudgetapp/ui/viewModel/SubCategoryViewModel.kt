package com.example.mybudgetapp.ui.viewModel

import androidx.lifecycle.ViewModel
import com.example.mybudgetapp.data.SubCategoryRepository
import com.example.mybudgetapp.ui.model.SubCategoryItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SubCategoryViewModel: ViewModel() {
    // Repository instance to handle data operations
    private val repository = SubCategoryRepository()

    //initializing _subCategoryList as an empty list of SubCategoryItem
    private val _subCategoryList = MutableStateFlow<List<SubCategoryItem>>(emptyList())

    val categoryList: StateFlow<List<SubCategoryItem>> = _subCategoryList.asStateFlow()

/*    // Function to add a new category item
    fun addCategoryItem(name: String, plannedAmount: Double, totalSpend: Double){
        if (name.isNotBlank()) {
            val newItem = SubCategoryItem(name, plannedAmount, totalSpend)
            _subCategoryList.value += newItem
        }
    }

 */




    fun getSubCategory(userId: String, budgetId: String, mainCatName: String) {
        repository.getSubCategory(userId, budgetId, mainCatName){ subCategories ->
            _subCategoryList.value = subCategories
        }
    }

    fun setSubCategoryList(subCategoryItem: List<SubCategoryItem>){
        _subCategoryList.value = subCategoryItem
    }
}