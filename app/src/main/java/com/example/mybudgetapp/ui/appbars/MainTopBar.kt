package com.example.mybudgetapp.ui.appbars

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.mybudgetapp.ui.components.AddExpenseDialog
import com.example.mybudgetapp.ui.viewModel.DateAndMonthViewModel
import com.example.mybudgetapp.ui.viewModel.ExpenseViewModel
import com.example.mybudgetapp.ui.viewModel.MainCategoryViewModel
import com.example.mybudgetapp.ui.viewModel.SharedViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun MainTopBar(navController: NavController, dateAndMonthViewModel: DateAndMonthViewModel, expenseViewModel: ExpenseViewModel ){
    val mainCategoryViewModel:MainCategoryViewModel = viewModel()
    val sharedViewModel: SharedViewModel = viewModel()
    var expanded by remember {  mutableStateOf(false) } // Controls dropdown visibility
    val selectedMonth by dateAndMonthViewModel.selectedMonth.collectAsState()
    val selectedYear by dateAndMonthViewModel.selectedYear.collectAsState()
    var selectedMonthYear = "$selectedMonth $selectedYear"

    val currentDate = LocalDate.now()
    val monthYearList = generateMonthYearList(currentDate, 24) // Generate last 24 months for dropdown

    //state control the visibility of expense adding dialog
    var showExpenseAddDialog by remember { mutableStateOf(false) }

    // State to hold the budgetId
    val budgetId by sharedViewModel.budgetId.collectAsState()

    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            titleContentColor = MaterialTheme.colorScheme.onSecondary
        ),
        title = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth(),
            ){
                Text(
                    text = "My Budget App",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.SansSerif,
                    modifier = Modifier
                        .fillMaxWidth()
                )
                //Month and Year text with the dropdown icon in a Row
                Row (
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()

                ){
                    Text(
                        text = selectedMonthYear,
                        fontSize = 20.sp,
                        fontFamily = FontFamily.SansSerif,
                        color = MaterialTheme.colorScheme.onSecondary
                    )
                    // Dropdown icon next to the month/year
                    IconButton(onClick = {expanded = !expanded}) {
                        Icon(
                            imageVector = Icons.Filled.ExpandMore,
                            contentDescription = "Open calender",
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    // Dropdown menu for month/year
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = {expanded = false},
                        modifier = Modifier
                            .background(Color.White)
                    ) {
                        Row (
                            modifier = Modifier
                                .horizontalScroll(rememberScrollState())
                                .padding(2.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ){
                            monthYearList.forEach { monthYear ->
                                val dividedDate = monthYear.split(" ")
                                val month = dividedDate[0]
                                val year = dividedDate[1]
                                val isSelected = monthYear == selectedMonthYear

                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier
                                        .clickable{
                                            selectedMonthYear = monthYear
                                            dateAndMonthViewModel.updateMonthAndYear(month, year)
                                            expanded = false
                                        }
                                        .background(
                                            color = if (isSelected) Color.Gray else Color.White,
                                            shape = RoundedCornerShape(16.dp)
                                        )
                                        .border(2.dp, Color.Blue, shape = RoundedCornerShape(16.dp))
                                        .padding(horizontal = 16.dp, vertical = 8.dp)

                                ){
                                    Column (
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ){
                                        Text(
                                            text = month.substring(0,3),
                                            color = if (isSelected) Color.White else Color.Black,
                                            fontSize = 20.sp

                                        )
                                        Text(
                                            text = year,
                                            color = if (isSelected) Color.White else Color.Black,
                                            fontSize = 18.sp
                                        )
                                    }
                                }
                            }
                        }

                    }
                }
            }
        },
        actions = {
            if (budgetId != null){
                //plus icon button to add expenses
                IconButton(
                    onClick = {showExpenseAddDialog = true}
                ) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "Add expense",
                        tint = MaterialTheme.colorScheme.onSecondary

                    )
                }
            }
        }
    )
    // Show dialog when user clicks on plus icon
    if(showExpenseAddDialog){
        AddExpenseDialog (
            onDismiss = {showExpenseAddDialog = false},
            expenseViewModel = expenseViewModel,
            mainCategoryViewModel = mainCategoryViewModel
        )
    }
}

//function to create month and year list
fun generateMonthYearList(currentDate: LocalDate, noOfMonthsBack: Int): List<String> {
    val formatter = DateTimeFormatter.ofPattern("MMMM yyyy")
    val monthYearList = mutableListOf<String>()
    var date = currentDate

    for (i in 0 until noOfMonthsBack){
        monthYearList.add(date.format(formatter))
        date = date.minusMonths(1)
    }
    return monthYearList
}