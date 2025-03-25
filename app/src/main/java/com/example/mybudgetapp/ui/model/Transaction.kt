package com.example.mybudgetapp.ui.model

import com.google.firebase.firestore.PropertyName
import java.util.*


//enum will differentiate income and expense
enum class TransactionType { INCOME, EXPENSE }

//it represent each transaction
data class Transaction(
    @get:PropertyName("amount") @set:PropertyName("amount")
    var amount: Double = 0.0,

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

) {
    //function to determine the transaction type based on category name
    fun getTransactionType(): TransactionType {
        return if (categoryName.contains("Income", ignoreCase = true)) {
            TransactionType.INCOME
        } else {
            TransactionType.EXPENSE
        }
    }
}
