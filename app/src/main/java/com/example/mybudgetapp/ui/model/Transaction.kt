package com.example.mybudgetapp.ui.model

import com.google.firebase.firestore.PropertyName
import java.util.*

//enum to differentiate between income and expense
enum class TransactionType { INCOME, EXPENSE }

// Represents each transaction
data class Transaction(
    var id: String = "",  //Firestore document ID

    @get:PropertyName("amount") @set:PropertyName("amount")
    var amount: Double = 0.0, //can be String or Double

    @get:PropertyName("budgetId") @set:PropertyName("budgetId")
    var budgetId: String = "",

    @get:PropertyName("category_name") @set:PropertyName("category_name")
    var categoryName: String = "",

    @get:PropertyName("date") @set:PropertyName("date")
    var date: Date? = null, //firestore stores timestamps as Date

    @get:PropertyName("description") @set:PropertyName("description")
    var description: String = "",

    @get:PropertyName("sub_category_id") @set:PropertyName("sub_category_id")
    var subCategoryId: String = "",

    @get:PropertyName("sub_category_name") @set:PropertyName("sub_category_name")
    var subCategoryName: String = "",

    @get:PropertyName("user_id") @set:PropertyName("user_id")
    var userId: String = "",

    @get:PropertyName("transaction_type") @set:PropertyName("transaction_type")
    var transactionType: String = "",
) {
    // Function to determine the transaction type based on category name
    fun getTransactionType(): TransactionType {
        return when (transactionType.lowercase(Locale.getDefault())) {
            "income" -> TransactionType.INCOME
            else -> TransactionType.EXPENSE
        }
    }

    //function to safely retrieve amount as Double
    fun getAmountAsDouble(): Double {
        return amount
    }
}

