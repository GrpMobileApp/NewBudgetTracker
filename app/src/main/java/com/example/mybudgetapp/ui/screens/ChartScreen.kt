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
import com.github.mikephil.charting.formatter.ValueFormatter


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
    val categoryNames = mutableListOf<String>()  // List to store category names
    val labels = mutableListOf<String>()  // List to store category names for x-axis

    // Log the transactions
    Log.d("ChartScreen", "Transactions: $transactions")

    // Ensure the transactions list is loaded before proceeding
    if (transactions.isEmpty()) {
        Log.d("ChartScreen", "No transactions to display.")
    }

    // Get selected month and year from the view model
    val selectedMonth by dateAndMonthViewModel.selectedMonth.collectAsState()
    val selectedYear by dateAndMonthViewModel.selectedYear.collectAsState()

    // Filter transactions by the selected month and year
    val filteredTransactions = transactions.filter { transaction ->
        transaction.date?.let { date ->
            val cal = java.util.Calendar.getInstance().apply { time = date }
            val monthMatch = cal.getDisplayName(java.util.Calendar.MONTH, java.util.Calendar.LONG, java.util.Locale.getDefault()) == selectedMonth
            val yearMatch = cal.get(java.util.Calendar.YEAR).toString() == selectedYear
            monthMatch && yearMatch
        } ?: false
    }

    // Initialize BarChart data
    val barChartEntries = mutableListOf<BarEntry>()
    val barColors = mutableListOf<Int>()

    // Iterate through each transaction instead of grouping by date
    filteredTransactions.forEachIndexed { index, transaction ->
        val amount = transaction.getAmountAsDouble()

        Log.d("ChartScreen", "Transaction: ${transaction.date}, Amount: $amount")

        if (amount != 0.0) {
            // Set the bar color based on category: green for income, red for expense
            val barColor = if (transaction.categoryName?.equals("income", ignoreCase = true) == true) {
                Color(0xFF81C784) // Green for income
            } else {
                Color(0xFFEF9A9A) // Red for expense
            }

            // Add each transaction as a separate entry
            barChartEntries.add(BarEntry(index.toFloat(), amount.toFloat()))
            labels.add(transaction.categoryName ?: "Unknown") // Add category name to labels
            categoryNames.add(transaction.categoryName ?: "Unknown")
            barColors.add(barColor.toArgb())  // Convert Color to ARGB integer
        }
    }

    // Ensure to load transactions when userId is available
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
            // Display the selected date at the top of the screen
            Text(
                text = "$selectedMonth $selectedYear", // Display selected month and year
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(bottom = 16.dp) // Add some space below the date
            )
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

                            // Set the x-axis labels
                            xAxis.valueFormatter = IndexAxisValueFormatter(labels)
                            xAxis.granularity = 1f
                            xAxis.isGranularityEnabled = true

                            // Customize the chart appearance
                            axisLeft.textColor = Color.Black.toArgb()
                            axisRight.isEnabled = false
                            xAxis.textColor = Color.Black.toArgb()
                            legend.isEnabled = true
                            legend.textColor = Color.Black.toArgb()

                            // Set up the Y-axis value formatter to show the amount
                            axisLeft.valueFormatter = object : ValueFormatter() {
                                override fun getFormattedValue(value: Float): String {
                                    // Format the Y-axis value to show the amount
                                    return "€${value.toInt()}"
                                }
                            }

                            invalidate() // Refresh the BarChart
                        }
                    },

                    update = { chart ->
                        val dataSet = BarDataSet(barChartEntries, "Transactions").apply {
                            colors = barColors
                            valueTextSize = 14f
                            valueTextColor = Color.Black.toArgb()
                            setDrawValues(true)
                        }
                        val data = BarData(dataSet)
                        chart.data = data

                        chart.xAxis.valueFormatter = IndexAxisValueFormatter(labels)

                        // Update the Y-axis value formatter to show the amount
                        chart.axisLeft.valueFormatter = object : ValueFormatter() {
                            override fun getFormattedValue(value: Float): String {
                                // Format the Y-axis value to show the amount
                                return "$${value.toInt()}"
                            }
                        }
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

            // Add labels below the chart
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
