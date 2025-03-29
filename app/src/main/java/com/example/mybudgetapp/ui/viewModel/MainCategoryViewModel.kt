package com.example.mybudgetapp.ui.viewModel

import androidx.lifecycle.ViewModel
import com.example.mybudgetapp.data.MainCategoryRepository
import com.example.mybudgetapp.data.MainCategoryWithSubcategories
import com.example.mybudgetapp.data.SubCategoryRepository
import com.example.mybudgetapp.ui.model.SubCategoryItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MainCategoryViewModel: ViewModel() {
    // Repository instance to handle data operations
    private val repository = MainCategoryRepository()
    private val subCategoryRepository = SubCategoryRepository()
    private val _mainCategoryWithSubcategories = MutableStateFlow<List<MainCategoryWithSubcategories>>(
        emptyList()
    )
    val mainCategoryWithSubcategories:StateFlow<List<MainCategoryWithSubcategories>> = _mainCategoryWithSubcategories.asStateFlow()

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

    //Fetch main category id for the given user, budget and category name.
    fun fetchMainCategoryId(userId: String, budgetId:String, name: String, onResult: (String?) -> Unit){
        repository.getMainCategoryId(userId, budgetId, name, onResult)
    }

    // Function to fetch subcategories and combine them with the main category list
    fun fetchMainCategoryWithSubcategories(userId: String, budgetId: String){
        //Fetch the main category list
        val categories = _mainCategoryList.value

        //Create a list to storethe combined data
        val mainCategoryWithSubcategoriesList = mutableListOf<MainCategoryWithSubcategories>()

        //Loop through main category and fetch it's sub categories
        categories.forEach { category ->
            subCategoryRepository.getSubCategory(userId,budgetId,category){ subCategoryItems ->
                //Add the combined result (main category + subcategories) to the list
                mainCategoryWithSubcategoriesList.add(
                    MainCategoryWithSubcategories(category, subCategoryItems)
                )
                //update the state with combined list
                _mainCategoryWithSubcategories.value = mainCategoryWithSubcategoriesList
            }
        }

    }
}