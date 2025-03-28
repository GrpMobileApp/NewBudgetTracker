package com.example.mybudgetapp.ui.model

import com.google.firebase.firestore.PropertyName
import java.util.*

//enum to differentiate between income and expense
enum class TransactionType { INCOME, EXPENSE }

// Represents each transaction
data class Transaction(
    @get:PropertyName("amount") @set:PropertyName("amount")
    var amount: Any = 0.0, //can be String or Double

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
    var userId: String = ""
) {
    // Function to determine the transaction type based on category name
    fun getTransactionType(): TransactionType {
        return if (categoryName.equals("income", ignoreCase = true)) {
            TransactionType.INCOME
        } else {
            TransactionType.EXPENSE
        }
    }

    //function to safely retrieve amount as Double
    fun getAmountAsDouble(): Double {
        return when (amount) {
            is String -> (amount as String).toDoubleOrNull() ?: 0.0
            is Double -> amount as Double
            else -> 0.0
        }
    }
}
