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

    // Function to add a new category item
    fun addCategoryItem(
        budgetId: String,
        mainCategoryName: String,
        plannedAmount: Double,
        subCategoryName: String,
        totalSpend: Double,
        userId: String,
        onSuccess: () -> Unit
    ) {
        if (subCategoryName.isNotBlank()) {
            repository.saveSubCategory(budgetId, mainCategoryName, plannedAmount, plannedAmount - totalSpend, subCategoryName, totalSpend, userId){ success ->
                if (success) {
                    onSuccess() //refresh only when Firestore confirms success
                }
            }
        }
    }





    fun getSubCategory(userId: String, budgetId: String, mainCatName: String) {
        repository.getSubCategory(userId, budgetId, mainCatName){ subCategories ->
            _subCategoryList.value = subCategories
        }
    }

    fun setSubCategoryList(subCategoryItem: List<SubCategoryItem>){
        _subCategoryList.value = subCategoryItem
    }

    fun updateTotalSpendAndRemaining(subCategoryId: String, totalSpend:Double, remaining:Double, onResult: (Boolean) -> Unit){
        repository.updateTotalSpendAndRemaining(subCategoryId,totalSpend, remaining, onResult)
    }

    fun updateSubCategoryDetails(subCategoryId: String, subCategoryName: String, plannedAmount: Double, remaining:Double, onResult: (Boolean) -> Unit){
        repository.updateSubCategoryData(subCategoryId, subCategoryName, plannedAmount, remaining, onResult)
    }

    fun deleteSelectedSubCategory(subCategoryId: String, onResult: (Boolean) -> Unit){
        repository.deleteSubCategory(subCategoryId, onResult)
    }
}