package com.example.mybudgetapp.data

import android.util.Log
import com.example.mybudgetapp.ui.model.SubCategoryItem
import com.google.firebase.firestore.FirebaseFirestore

class SubCategoryRepository {
    private val db = FirebaseFirestore.getInstance()
    // Function to get sub category details
    fun getSubCategory(userId: String, budgetId: String, mainCatName: String, onResult: (List<SubCategoryItem>) -> Unit) {
        db.collection("sub_categories")
            .whereEqualTo("userId", userId)
            .whereEqualTo("budgetId", budgetId)
            .whereEqualTo("mainCategoryName", mainCatName)
            .get()
            .addOnSuccessListener { documents ->
                val subCategories = mutableListOf<SubCategoryItem>()

                for (document in documents) { // Iterate through each document
                    val budgetID = document.getString("budgetId")?: ""
                    val mainCategoryName = document.getString("mainCategoryName")?: ""
                    val plannedAmount = document.getDouble("plannedAmount")?: 0.0
                    val remainingAmount = document.getDouble("remainingAmount")?: 0.0
                    val subCategoryName = document.getString("subCategoryName")?: ""
                    val totalSpend = document.getDouble("totalSpend") ?: 0.0
                    val userID = document.getString("userId") ?: ""

                    if (subCategoryName.isNotEmpty()) {
                        subCategories.add(SubCategoryItem(budgetID, mainCategoryName,plannedAmount,remainingAmount, subCategoryName, totalSpend, userID))
                    }
                }
                // Log the fetched subcategories
                Log.d("SubCategoryRepository", "Fetched subcategories: $subCategories \n$budgetId\n$mainCatName")
                onResult(subCategories) // Return the list
            }
            .addOnFailureListener {exception ->
                Log.e("FirestoreError", "Failed to fetch subcategories: ${exception.message}")
                onResult(emptyList())
            }
    }
}