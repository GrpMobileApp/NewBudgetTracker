package com.example.mybudgetapp.ui.screens



import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.navigation.NavController
import com.example.mybudgetapp.ui.appbars.BottomBar
import com.example.mybudgetapp.ui.appbars.MainTopBar
import com.example.mybudgetapp.ui.viewModel.DateAndMonthViewModel

@Composable
fun HomeScreen(navController: NavController, dateAndMonthViewModel: DateAndMonthViewModel) {

    // List of selectable budget categories (Planned, Spent, and Remaining)
    val mainOptionList = listOf("Planned", "Spent", "Remaining")
    // Declare mutable state for selected option(planned, spent or Remaining)
    var selectedOption by remember { mutableStateOf("Planned") }

    // Function to update the selected option(planned, spent or Remaining)
    fun onOptionSelected(opt: String){
        selectedOption = opt
    }

    Scaffold (
        // Top bar receives a function to update the selected month and year
        topBar = { MainTopBar(navController, dateAndMonthViewModel::updateMonthAndYear) },
        bottomBar = { BottomBar(navController) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // selected month and year from ViewModel
            val selectedMonth by dateAndMonthViewModel.selectedMonth.collectAsState()
            val selectedYear by dateAndMonthViewModel.selectedYear.collectAsState()

            // Row for selecting "Planned", "Spent", or "Remaining"
            Row (
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Gray)
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ){
                mainOptionList.forEach{option ->
                    val isSelected = option == selectedOption
                    Box (
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .weight(1f)
                            .clickable{
                                selectedOption = option
                            }
                            .background(
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.primary,
                                shape = RoundedCornerShape(16.dp)
                            )
                            .padding(vertical = 8.dp)


                    ){
                        Text(
                            text = option,
                            color = if (isSelected) MaterialTheme.colorScheme.primary else Color.White ,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            Text(
                text = "This is home screen"
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
