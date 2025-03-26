package com.example.mybudgetapp.data

import com.google.firebase.firestore.FirebaseFirestore

class MainCategoryRepository {
    // Firestore instance
    private val db = FirebaseFirestore.getInstance()

    // Function to save MainCategory to a specific user's budget
    fun saveMainCategory(userId: String, budgetId: String, name: String, onResult: (Boolean) -> Unit) {
        val mainCategory = hashMapOf("name" to name)

        // Navigating to the right path
        db.collection("Users")
            .document(userId) // User document
            .collection("monthly_budgets") // Budget collection
            .document(budgetId) // Budget document
            .collection("main_categories") // MainCategory collection
            .add(mainCategory) // Add the main category
            .addOnSuccessListener { onResult(true) } // Success callback
            .addOnFailureListener { onResult(false) } // Failure callback
    }
}