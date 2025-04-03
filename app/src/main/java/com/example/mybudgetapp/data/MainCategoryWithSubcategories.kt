package com.example.mybudgetapp.data

import com.example.mybudgetapp.ui.model.SubCategoryItem

data class MainCategoryWithSubcategories(
    val mainCategoryName: String,
    val subCategories: List<SubCategoryItem>
)
