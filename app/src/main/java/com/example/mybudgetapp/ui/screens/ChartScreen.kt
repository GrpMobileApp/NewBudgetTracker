package com.example.mybudgetapp.ui.screens

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Scaffold
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
import com.example.mybudgetapp.ui.model.TransactionType
import com.example.mybudgetapp.ui.viewModel.DateAndMonthViewModel
import com.example.mybudgetapp.ui.viewModel.ExpenseViewModel
import androidx.compose.runtime.livedata.observeAsState
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.components.Legend
import com.google.firebase.auth.FirebaseAuth
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb

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

    // Log the transactions for debugging
    Log.d("ChartScreen", "Transactions: $transactions")

    //ensure the transactions list is loaded before proceeding
    if (transactions.isEmpty()) {
        Log.d("ChartScreen", "No transactions to display.")
    }

    //group transactions by subcategory
    val groupedTransactions = transactions.groupBy { it.subCategoryName }

    // Log grouped transactions for debugging
    Log.d("ChartScreen", "Grouped Transactions: $groupedTransactions")

    //initialize BarChart data
    val barChartEntries = mutableListOf<BarEntry>()
    val labels = mutableListOf<String>()

    //loop through each grouped transaction and calculate total amount for each subcategory
    groupedTransactions.forEach { (subCategoryName, transactionList) ->
        val totalAmount = transactionList.sumOf { it.getAmountAsDouble() }

        // Log the total amount for debugging
        Log.d("ChartScreen", "Subcategory: $subCategoryName, Total Amount: $totalAmount")

        //only add entries with non-zero amounts
        if (totalAmount > 0) {
            //check if the transaction is an expense or income
            val barColor = if (transactionList.first().categoryName == "expense") {
                Color.Red.toArgb() // Red for expenses
            } else {
                Color.Green.toArgb() // Green for income
            }

            // Add BarEntry for each subcategory with corresponding color
            barChartEntries.add(BarEntry(barChartEntries.size.toFloat(), totalAmount.toFloat()))
            labels.add(subCategoryName)
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
            AndroidView(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp),
                factory = { ctx ->
                    BarChart(ctx).apply {
                        description.isEnabled = false

                        //sdd check for whether we have valid entries to display
                        if (barChartEntries.isNotEmpty()) {
                            val dataSet = BarDataSet(barChartEntries, "Transactions").apply {
                                //set the color dynamically for each bar
                                color = Color.Blue.toArgb()
                                valueTextSize = 14f
                                valueTextColor = Color.Black.toArgb()
                            }
                            val data = BarData(dataSet)
                            this.data = data

                            //set the x-axis labels
                            xAxis.valueFormatter = object : com.github.mikephil.charting.formatter.IndexAxisValueFormatter(labels) {}
                            invalidate() //refresh the BarChart
                        } else {
                            Log.d("ChartScreen", "No valid entries to display in chart.")
                        }

                        //customize the chart appearance
                        axisLeft.textColor = Color.Black.toArgb()
                        axisRight.isEnabled = false
                        xAxis.textColor = Color.Black.toArgb()
                        legend.isEnabled = false
                    }
                },
                update = { chart ->
                    //only update the chart data if necessary
                    if (barChartEntries.isNotEmpty()) {
                        val dataSet = BarDataSet(barChartEntries, "Transactions").apply {
                            //set the color dynamically for each bar
                            color = Color.Blue.toArgb()
                            valueTextSize = 14f
                            valueTextColor = Color.Black.toArgb()
                        }
                        val data = BarData(dataSet)
                        chart.data = data

                        chart.xAxis.valueFormatter = object : com.github.mikephil.charting.formatter.IndexAxisValueFormatter(labels) {}
                        chart.invalidate() //refresh the chart
                    }
                }
            )
        }
    }
}
