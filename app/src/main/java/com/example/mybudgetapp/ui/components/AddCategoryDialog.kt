package com.example.mybudgetapp.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.mybudgetapp.ui.R

@Composable
fun AddCategoryDialog(onDismiss: () -> Unit, onSubmit: (String, Float) -> Unit, mainCategoryName:String) {
    var category by remember  { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }

    // Define the dialog UI
    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text(text = mainCategoryName, fontWeight = FontWeight.Bold) },

        // Content of the dialog
        text = {
            Column  {
                TextField(
                    value = category,
                    onValueChange = { category = it },
                    label = { Text(stringResource(R.string.enter_category)) }
                )
                Spacer(modifier = Modifier.height(10.dp))

                TextField(
                    value = amount,
                    onValueChange = { amount = it },
                    label = { Text(stringResource(R.string.enter_amount)) }
                )
            }
        },

        // Submit button
        confirmButton = {
            Button (
                onClick = {
                    val amountValue = amount.toFloatOrNull() ?: 0f
                    if (category.isNotBlank() && amountValue > 0) {
                        onSubmit(category, amountValue)
                    }
                }
            ) {
                Text("Submit")
            }
        },
        //cancel button
        dismissButton = {
            Button(onClick = { onDismiss() }) {
                Text("Cancel")
            }
        }
    )
}
