package com.example.mybudgetapp.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mybudgetapp.ui.model.SubCategoryItem
import com.example.mybudgetapp.ui.viewModel.MainCategoryViewModel
import com.example.mybudgetapp.ui.viewModel.SharedViewModel
import com.example.mybudgetapp.ui.viewModel.SubCategoryViewModel
import com.google.firebase.auth.FirebaseAuth

@Composable
fun SpentScreenContent(mainCategoryViewModel: MainCategoryViewModel, subCategoryViewModel: SubCategoryViewModel) {
    val sharedViewModel: SharedViewModel = viewModel()
    val auth = FirebaseAuth.getInstance()

    val userId = auth.currentUser?.uid.toString()

    // State to hold the budgetId
    val budgetId by sharedViewModel.budgetId.collectAsState()

    //call a function when screen appear
    LaunchedEffect(budgetId) {
        if (budgetId != null) {
            mainCategoryViewModel.fetchMainCategoryWithSubcategories(userId, budgetId!!)
        }
    }
    //Combined list of main categories with their subcategories
    val mainCategoryWithSubcategories by mainCategoryViewModel.mainCategoryWithSubcategories.collectAsState()



    // Content of home screen
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Loop through each main category and display their subcategories
        mainCategoryWithSubcategories.forEach { category ->

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 10.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier.padding(10.dp)
                ) {
                    Text(
                        text = category.mainCategoryName.uppercase(),
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    // Display subcategories for this main category
                    category.subCategories.forEach {  subCat ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp), // Space between items
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = subCat.subCategoryName, fontSize = 15.sp)
                            Text(text = subCat.totalSpend.toString(), fontSize = 15.sp)
                        }

                        // Add the separating line
                        HorizontalDivider(thickness = 1.dp, color = Color.LightGray)
                    }
                }
            }

        }
    }
}

