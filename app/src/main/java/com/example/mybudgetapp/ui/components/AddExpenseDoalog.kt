package com.example.mybudgetapp.ui.components
import android.app.Dialog
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.mybudgetapp.ui.viewModel.MainCategoryViewModel

@Composable
fun AddExpenseDialog(onDismiss: () -> Unit, onSubmit: (String, String, String, Float) -> Unit, mainCategoryViewModel: MainCategoryViewModel){
    var category by remember { mutableStateOf("") }
    var subCategory by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }

    val mainCategoryList by mainCategoryViewModel.mainCategoryList.collectAsState()
    Dialog (onDismissRequest = { onDismiss() }) {
        Surface (
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White),
            shape = MaterialTheme.shapes.medium
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                Text(text = "Add Expense", style = MaterialTheme.typography.headlineSmall)
                Spacer(modifier = Modifier.height(10.dp))

                // Use DropdownLayout for Category
                DropdownLayout(
                    listOfItems = mainCategoryList,
                    label = "Select Category"
                ) { selectedCategory ->
                    category = selectedCategory
                }

                Spacer(modifier = Modifier.height(10.dp))

                TextField(
                    value = subCategory,
                    onValueChange = { subCategory = it },
                    label = { Text("Enter Subcategory") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(10.dp))

                TextField(
                    value = note,
                    onValueChange = { note = it },
                    label = { Text("Enter a Note") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(10.dp))

                TextField(
                    value = amount,
                    onValueChange = { amount = it },
                    label = { Text("Enter Amount") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(20.dp))

                Row {
                    Button(onClick = { onDismiss() }) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.weight(1f))

                    Button(
                        onClick = {
                            val amountValue = amount.toFloatOrNull() ?: 0f
                            if (category.isNotBlank() && subCategory.isNotBlank() && amountValue > 0) {
                                onSubmit(category, subCategory, note, amountValue)
                                onDismiss()
                            }
                        }
                    ) {
                        Text("Submit")
                    }
                }
            }
        }
    }
}