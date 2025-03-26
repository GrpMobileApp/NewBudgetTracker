package com.example.mybudgetapp.data
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore

class BudgetRepository {

    private val db = FirebaseFirestore.getInstance()

    // Function to get budget document ID based on month and year
    fun getBudgetId(userId: String, month: String, year: String, onResult: (String?) -> Unit) {
        db.collection("users").document(userId).collection("budget")
            .whereEqualTo("month", month)  // Filter by month
            .whereEqualTo("year", year)    // Filter by year
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    val budgetDoc = documents.first()
                    onResult(budgetDoc.id)  // Return the budget ID
                } else {
                    onResult(null) // No matching budget found
                }
            }
            .addOnFailureListener {exception ->
                Log.e("FirestoreError", "Failed to fetch budget: ${exception.message}")
                onResult(null)
            }
    }
}