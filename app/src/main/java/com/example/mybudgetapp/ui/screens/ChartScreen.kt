package com.example.mybudgetapp.ui.screens

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.mybudgetapp.ui.appbars.BottomBar
import com.example.mybudgetapp.ui.appbars.MainTopBar
import com.example.mybudgetapp.ui.viewModel.TransactionViewModel
import androidx.compose.runtime.livedata.observeAsState
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.google.firebase.auth.FirebaseAuth
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontWeight
import com.example.mybudgetapp.ui.viewModel.DateAndMonthViewModel
import com.example.mybudgetapp.ui.viewModel.ExpenseViewModel

@Composable
fun ChartScreen(
    navController: NavController,
    dateAndMonthViewModel: DateAndMonthViewModel,
    expenseViewModel: ExpenseViewModel,
    viewModel: TransactionViewModel = viewModel()
) {
    val context = LocalContext.current
    val userId = FirebaseAuth.getInstance().currentUser?.uid
    val transactions by viewModel.transactions.observeAsState(emptyList())

    //log the transactions
    Log.d("ChartScreen", "Transactions: $transactions")

    //ensure the transactions list is loaded before proceeding
    if (transactions.isEmpty()) {
        Log.d("ChartScreen", "No transactions to display.")
    }

    //group transactions by subcategory
    //val groupedTransactions = transactions.groupBy { it.subCategoryName }

    val selectedMonth by dateAndMonthViewModel.selectedMonth.collectAsState()
    val selectedYear by dateAndMonthViewModel.selectedYear.collectAsState()

// Filter by selected month & year
    val filteredTransactions = transactions.filter { transaction ->
        transaction.date?.let { date ->
            val cal = java.util.Calendar.getInstance().apply { time = date }
            val monthMatch = cal.getDisplayName(java.util.Calendar.MONTH, java.util.Calendar.LONG, java.util.Locale.getDefault()) == selectedMonth
            val yearMatch = cal.get(java.util.Calendar.YEAR).toString() == selectedYear
            monthMatch && yearMatch
        } ?: false
    }

// Group by date string (e.g., "05 Apr")
    val groupedTransactions = filteredTransactions.groupBy { transaction ->
        val formatter = java.text.SimpleDateFormat("dd MMM", java.util.Locale.getDefault())
        formatter.format(transaction.date!!)
    }

    //log grouped transactions
    Log.d("ChartScreen", "Grouped Transactions: $groupedTransactions")

    // Initialize BarChart data
    val barChartEntries = mutableListOf<BarEntry>()
    val labels = mutableListOf<String>()
    val barColors = mutableListOf<Int>()

    //loop through each grouped transaction and calculate total amount for each subcategory
    /*groupedTransactions.forEach { (subCategoryName, transactionList) ->
        val totalAmount = transactionList.sumOf { it.getAmountAsDouble() }

        //log the total amount
        Log.d("ChartScreen", "Subcategory: $subCategoryName, Total Amount: $totalAmount")

        //only add entries with non-zero amounts
        if (totalAmount > 0) {
            //check if the transaction is an expense or income
            val barColor = if (transactionList.first().categoryName == "expense") {
                Color.Red.toArgb() //red for expenses
            } else {
                Color.Green.toArgb() //green for income
            }

            //add BarEntry for each subcategory with corresponding color
            barChartEntries.add(BarEntry(barChartEntries.size.toFloat(), totalAmount.toFloat()))
            labels.add(subCategoryName)
            barColors.add(barColor)
        }
    }*/

    groupedTransactions.forEach { (dateLabel, transactionList) ->
        val totalAmount = transactionList.sumOf { it.getAmountAsDouble() }

        Log.d("ChartScreen", "Date: $dateLabel, Total Amount: $totalAmount")

        if (totalAmount > 0) {
            val barColor = if (transactionList.first().categoryName == "expense") {
                Color.Red.toArgb()
            } else {
                Color.Green.toArgb()
            }

            barChartEntries.add(BarEntry(barChartEntries.size.toFloat(), totalAmount.toFloat()))
            labels.add(dateLabel)
            barColors.add(barColor)
        }
    }


    //ensure load transactions when userId is available
    LaunchedEffect(userId) {
        if (userId != null) {
            viewModel.loadTransactions(userId)
        }
    }

    Scaffold(
        topBar = { MainTopBar(navController, dateAndMonthViewModel, expenseViewModel) },
        bottomBar = { BottomBar(navController) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center
        ) {
            if (barChartEntries.isNotEmpty()) {
                AndroidView(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp),
                    factory = { ctx ->
                        BarChart(ctx).apply {
                            description.isEnabled = false

                            val dataSet = BarDataSet(barChartEntries, "Transactions").apply {
                                colors = barColors
                                valueTextSize = 14f
                                valueTextColor = Color.Black.toArgb()
                            }
                            val data = BarData(dataSet)
                            this.data = data

                            //set the x-axis labels
                            xAxis.valueFormatter = IndexAxisValueFormatter(labels)
                            xAxis.granularity = 1f
                            xAxis.isGranularityEnabled = true

                            //customize the chart appearance
                            axisLeft.textColor = Color.Black.toArgb()
                            axisRight.isEnabled = false
                            xAxis.textColor = Color.Black.toArgb()
                            legend.isEnabled = true
                            legend.textColor = Color.Black.toArgb()

                            invalidate() // Refresh the BarChart
                        }
                    },
                    update = { chart ->
                        val dataSet = BarDataSet(barChartEntries, "Transactions").apply {
                            colors = barColors
                            valueTextSize = 14f
                            valueTextColor = Color.Black.toArgb()
                        }
                        val data = BarData(dataSet)
                        chart.data = data

                        chart.xAxis.valueFormatter = IndexAxisValueFormatter(labels)
                        chart.invalidate()
                    }
                )
            } else {
                Text(
                    text = "No data available",
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                    color = Color.Gray,
                    modifier = Modifier.padding(16.dp)
                )
            }

            //add labels below the chart
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                Text(
                    text = "Income & Expense",
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                    color = Color.Black,
                    modifier = Modifier.padding(8.dp)
                )
            }
            // Add a button to navigate to PieChartScreen
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { navController.navigate("pie_chart_screen") },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text(text = "View Pie Chart", color = Color.White)
            }
        }
        }
    }
