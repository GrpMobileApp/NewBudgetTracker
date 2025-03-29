package com.example.mybudgetapp.data
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore

class BudgetRepository {

    private val db = FirebaseFirestore.getInstance()

    // Function to get budget document ID based on month and year
    fun getBudgetId(userId: String, month: String, year: String, onResult: (String?) -> Unit) {
        db.collection("monthly_budgets")
            .whereEqualTo("month", month)  // Filter by month
            .whereEqualTo("year", year)    // Filter by year
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    val budgetDoc = documents.first()
                    onResult(budgetDoc.id)  // Return the budget ID
                    Log.e("Budget", "fetch budget: ${budgetDoc.id}")
                } else {
                    onResult(null) // No matching budget found
                    Log.e("budgetnull", "fetch budget null ${userId},${month},${year}")
                }
            }
            .addOnFailureListener {exception ->
                Log.e("FirestoreError", "Failed to fetch budget: ${exception.message}")
                onResult(null)
            }
    }

    fun storeBudget(userId: String, month: String, year: String, onResult: (Boolean) -> Unit){
            val budget = hashMapOf("month" to month, "year" to year, "userId" to userId)

            // Navigating to the right path: users -> userId -> budget -> budgetId -> maincategory
            db.collection("monthly_budgets")
                .add(budget) // Add the main category
                .addOnSuccessListener { onResult(true) } // Success callback
                .addOnFailureListener { onResult(false) } // Failure callback
        }

}