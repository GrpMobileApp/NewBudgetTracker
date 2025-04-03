package com.example.mybudgetapp.data

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import java.sql.Timestamp

class TransactionRepository {
    private val db = FirebaseFirestore.getInstance()
    // Function to store transaction details
    fun saveTransactions(
        userId: String,
        budgetId:String,
        categoryName:String,
        subCategoryId:String,
        subCategoryName:String,
        description:String,
        amount:Double,
        date:Timestamp,
        onResult: (Boolean) -> Unit
    ) {
        val transaction = hashMapOf(
            "userId" to userId,
            "budgetId" to budgetId,
            "category_name" to categoryName,
            "sub_category_id" to subCategoryId,
            "sub_category_name" to subCategoryName,
            "description" to description,
            "amount" to amount,
            "date" to date
        )

        // Navigating to the right path
        db.collection("transactions")
            .add(transaction) // Add the main category
            .addOnSuccessListener {
                onResult(true)  // Callback on success
            }
            .addOnFailureListener {
                    e -> Log.e("FirestoreError", "Error adding Transaction: ${e.message}")
                onResult(false)  // Callback on error
            }
    }
}