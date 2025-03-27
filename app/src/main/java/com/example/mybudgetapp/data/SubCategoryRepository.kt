package com.example.mybudgetapp.data

import android.util.Log
import com.example.mybudgetapp.ui.model.SubCategoryItem
import com.google.firebase.firestore.FirebaseFirestore

class SubCategoryRepository {
    private val db = FirebaseFirestore.getInstance()
    // Function to get sub category details
    fun getSubCategory(userId: String, budgetId: String, mainCatId: String, onResult: (List<SubCategoryItem>) -> Unit) {
        db.collection("Users")
            .document(userId)
            .collection("monthly_budgets")
            .document(budgetId)
            .collection("main_categories")
            .document(mainCatId)
            .collection("sub_categories")
            .get()
            .addOnSuccessListener { documents ->
                val subCategories = mutableListOf<SubCategoryItem>()

                for (document in documents) { // Iterate through each document
                    val name = document.getString("name")
                    val plannedAmount = document.getDouble("planned_amount") ?: 0.0
                    val totalSpend = document.getDouble("total_spend") ?: 0.0

                    if (name != null) {
                        subCategories.add(SubCategoryItem(name, plannedAmount, totalSpend))
                    }
                }
                onResult(subCategories) // Return the list
            }
            .addOnFailureListener {exception ->
                Log.e("FirestoreError", "Failed to fetch subcategories: ${exception.message}")
                onResult(emptyList())
            }
    }
}