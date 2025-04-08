package com.example.mybudgetapp.data

import android.util.Log
import com.example.mybudgetapp.ui.model.TransactionType
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
        transactionType: String,
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
            "date" to date,
            "transaction_type" to transactionType
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

    //Delete transactions
    fun deleteTransactions(subCategoryId: String, onResult: (Boolean) -> Unit){

        // Navigating to the right path
        db.collection("transactions")
            .whereEqualTo("sub_category_id", subCategoryId)
            .get()
            .addOnSuccessListener { result ->
                // If the query is successful, retrieve the matching documents
                val matchingDocs = result.documents

                // Create a new batch operation
                val batch = db.batch()

                // Loop through each matching document and add a delete operation to the batch
                matchingDocs.forEach { doc ->
                    batch.delete(doc.reference)
                }

                // Sending all delete operations to Firestore at once
                batch.commit()
                    .addOnSuccessListener {
                        onResult(true)
                    }
                    .addOnFailureListener {
                        onResult(false)
                    }
            }
            .addOnFailureListener { e ->
                Log.e("FirestoreError", "Error fetching documents: ${e.message}")
                onResult(false)
            }
    }
}