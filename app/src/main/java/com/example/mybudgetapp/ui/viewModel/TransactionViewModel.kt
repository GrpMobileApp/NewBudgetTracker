package com.example.mybudgetapp.ui.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.example.mybudgetapp.ui.model.Transaction

class TransactionViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()
    private val _transactions = MutableLiveData<List<Transaction>>()
    val transactions: LiveData<List<Transaction>> get() = _transactions

    //load transactions for the logged-in user
    fun loadTransactions(userId: String) {
        db.collection("transactions")
            .whereEqualTo("user_id", userId)  //get only the user's transactions
            .get()
            .addOnSuccessListener { result ->
                if (result.isEmpty) {
                    Log.d("Firestore", "No transactions found for user $userId")
                }

                val transactionList = mutableListOf<Transaction>()

                for (document in result) {
                    val transaction = document.toObject(Transaction::class.java)
                    transaction.id = document.id //Assign Firestore document ID
                    transactionList.add(transaction)
                }

                _transactions.value = transactionList
                Log.d("Firestore", "Transactions Retrieved: ${transactionList.size}")
            }
            .addOnFailureListener { exception ->
                Log.e("Firestore", "Error getting transactions", exception)
            }
    }

    //to delete transaction
    fun deleteTransaction(transaction: Transaction) {
        if (transaction.id.isNotEmpty()) {
            db.collection("transactions").document(transaction.id)
                .delete()
                .addOnSuccessListener {
                    Log.d("Firestore", "Transaction successfully deleted!")
                    // Update LiveData by removing the deleted transaction
                    _transactions.value = _transactions.value?.filterNot { it.id == transaction.id }
                }
                .addOnFailureListener { e ->
                    Log.e("Firestore", "Error deleting transaction", e)
                }
        } else {
            Log.e("Firestore", "Transaction ID is empty, cannot delete.")
        }
    }
}
