package com.example.mybudgetapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mybudgetapp.ui.viewModel.MainCategoryViewModel
import com.example.mybudgetapp.ui.model.SubCategoryItem
import com.example.mybudgetapp.ui.viewModel.SharedViewModel
import com.example.mybudgetapp.ui.viewModel.SubCategoryViewModel
import com.google.firebase.auth.FirebaseAuth


@Composable
fun UpdateDeleteSubCategoryDialog(
    onDismiss: () -> Unit,
    mainCategoryViewModel: MainCategoryViewModel,
    subCategory: SubCategoryItem
) {
    val sharedViewModel: SharedViewModel = viewModel()
    val subCategoryViewModel: SubCategoryViewModel = viewModel()
    val auth = FirebaseAuth.getInstance()

    val subCat by remember { mutableStateOf(subCategory) }
    var subCategoryName by remember { mutableStateOf(subCat.subCategoryName) }
    var subCategoryAmount by remember { mutableStateOf(subCat.plannedAmount.toString()) }

    // State to hold the budgetId
    val budgetId by sharedViewModel.budgetId.collectAsState()
    val userId = auth.currentUser?.uid.toString()



    Dialog(onDismissRequest = { onDismiss() }) {
        Surface(
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
                Text(text = subCat.subCategoryName, style = MaterialTheme.typography.headlineSmall)
                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = subCategoryName,
                    onValueChange = { subCategoryName = it },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = subCategoryAmount,
                    onValueChange = { subCategoryAmount = it},
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
                            if (budgetId != null && subCategory.subCategoryId.isNotBlank() && subCategoryName.isNotBlank() && subCategoryAmount.toDouble() > 0) {
                                subCategoryViewModel.updateSubCategoryDetails(
                                    subCategoryId = subCategory.subCategoryId,
                                    subCategoryName = subCategoryName,
                                    plannedAmount = subCategoryAmount.toDouble(),
                                    remaining = subCategoryAmount.toDouble() - subCategory.totalSpend,
                                    onResult = { success ->
                                        if (success) {
                                            mainCategoryViewModel.fetchMainCategoryWithSubcategories(userId,budgetId!!)
                                            onDismiss()
                                        }
                                    }
                                )
                            }
                        }
                    ) {
                        Text("Update")
                    }
                }
                //Button to delete sub category
                Button(
                    onClick = {
                        if (budgetId != null && subCategory.subCategoryId.isNotBlank()) {
                            subCategoryViewModel.deleteSelectedSubCategory(
                                subCategoryId = subCategory.subCategoryId,
                                onResult = { success ->
                                    if (success) {
                                        mainCategoryViewModel.fetchMainCategoryWithSubcategories(userId,budgetId!!)
                                        onDismiss()
                                    }
                                }
                            )
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7A2624)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Delete ${subCat.subCategoryName}")
                }
            }
        }
    }
}