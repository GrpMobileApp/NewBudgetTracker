package com.example.mybudgetapp.ui.screens

<<<<<<< HEAD
<<<<<<< HEAD
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
=======
import androidx.compose.foundation.layout.*
>>>>>>> d88140668bd40921e2872149a09d72235e267c2e
=======
import androidx.compose.foundation.layout.*
>>>>>>> 58fc489787b28c376bdb162e900d680cbf1235e7
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
<<<<<<< HEAD
<<<<<<< HEAD
import androidx.compose.material3.MaterialTheme
=======
import androidx.compose.material3.Button
>>>>>>> d88140668bd40921e2872149a09d72235e267c2e
=======
import androidx.compose.material3.Button
>>>>>>> 58fc489787b28c376bdb162e900d680cbf1235e7
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
<<<<<<< HEAD
<<<<<<< HEAD
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
=======
=======
>>>>>>> 58fc489787b28c376bdb162e900d680cbf1235e7
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.ButtonDefaults
<<<<<<< HEAD
>>>>>>> d88140668bd40921e2872149a09d72235e267c2e
=======
>>>>>>> 58fc489787b28c376bdb162e900d680cbf1235e7
import androidx.navigation.NavController
import com.example.mybudgetapp.ui.appbars.BottomBar
import com.example.mybudgetapp.ui.appbars.MainTopBar

@Composable
fun HomeScreen(navController: NavController) {

    // Declare mutable state for selected month and year
    var selectedMonth by remember { mutableStateOf("") }
    var selectedYear by remember { mutableStateOf("") }

    val mainOptionList = listOf("Planned", "Spent", "Remaining")
    // Declare mutable state for selected option(planned, spent or Remaining)
    var selectedOption by remember { mutableStateOf("Planned") }

    // Function to update the selected month and year
    fun onMonthYearSelected(month: String, year: String) {
        selectedMonth = month
        selectedYear = year
    }

<<<<<<< HEAD
<<<<<<< HEAD

    // Function to update the selected option(planned, spent or Remaining)
    fun onOptionSelected(opt: String){
        selectedOption = opt
    }

    Scaffold (
        topBar = { MainTopBar(navController, ::onMonthYearSelected, ::onOptionSelected) },
=======
    Scaffold(
=======
    Scaffold(

>>>>>>> 58fc489787b28c376bdb162e900d680cbf1235e7
        topBar = { MainTopBar(navController, ::onMonthYearSelected) },
>>>>>>> d88140668bd40921e2872149a09d72235e267c2e
        bottomBar = { BottomBar(navController) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
<<<<<<< HEAD
<<<<<<< HEAD
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
=======
            Text(text = "This is home screen")
            Spacer(modifier = Modifier.height(16.dp))

>>>>>>> d88140668bd40921e2872149a09d72235e267c2e
=======
            Text(text = "This is home screen")
            Spacer(modifier = Modifier.height(16.dp))

>>>>>>> 58fc489787b28c376bdb162e900d680cbf1235e7

        }
    }
}
