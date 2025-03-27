package com.example.mybudgetapp.data

import android.util.Log
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

    //Function to get main category id
    fun getMainCategoryId(userId: String, budgetId: String, categoryName:String, onResult: (String?) -> Unit){
        db.collection("Users")
            .document(userId)
            .collection("monthly_budgets")
            .document(budgetId)
            .collection("main_categories")
            .whereEqualTo("name", categoryName)  // Filter by name
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    val categoryDoc = documents.first()
                    onResult(categoryDoc.id)  // Return the budget ID
                } else {
                    onResult(null) // No matching budget found
                }
            }
            .addOnFailureListener {exception ->
                Log.e("FirestoreError", "Failed to fetch category-id: ${exception.message}")
                onResult(null)
            }
    }
}