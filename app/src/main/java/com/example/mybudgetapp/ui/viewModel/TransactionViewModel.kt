package com.example.mybudgetapp.ui.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.example.mybudgetapp.ui.model.Transaction

class TransactionViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()
    private val _transactions = MutableLiveData<List<Transaction>>() //liveData for transactions
    val transactions: LiveData<List<Transaction>> get() = _transactions

    //function to fetch transactions from firestore
    fun loadTransactions() {
        db.collection("transactions")
            .get()
            .addOnSuccessListener { result ->
                if (result.isEmpty) {
                    Log.d("Firestore", "No transactions found!")
                }

                val transactionList = mutableListOf<Transaction>()

                for (document in result) {
                    val transaction = document.toObject(Transaction::class.java)
                    Log.d("Firestore", "Transaction ID: ${document.id} - SubCategory: ${transaction.subCategoryName}")
                    transactionList.add(transaction)
                }

                _transactions.value = transactionList
                Log.d("Firestore", "Transactions Retrieved: ${transactionList.size}")
            }
            .addOnFailureListener { exception ->
                Log.e("Firestore", "Error getting transactions", exception)
            }
    }


    //filter transactions directly in the UI.
    fun filterTransactions(query: String): List<Transaction> {
        return _transactions.value?.filter { it.subCategoryName.contains(query, ignoreCase = true) } ?: emptyList()
    }
}