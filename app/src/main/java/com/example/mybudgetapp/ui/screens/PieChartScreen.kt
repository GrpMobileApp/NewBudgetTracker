package com.example.mybudgetapp.ui.screens

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.mybudgetapp.ui.appbars.BottomBar
import com.example.mybudgetapp.ui.appbars.MainTopBar
import com.example.mybudgetapp.ui.viewModel.DateAndMonthViewModel
import com.example.mybudgetapp.ui.viewModel.ExpenseViewModel
import com.example.mybudgetapp.ui.viewModel.TransactionViewModel
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.google.firebase.auth.FirebaseAuth


@Composable
fun PieChartScreen(
    navController: NavController,
    dateAndMonthViewModel: DateAndMonthViewModel,
    expenseViewModel: ExpenseViewModel,
    viewModel: TransactionViewModel = viewModel()
) {
    val context = LocalContext.current
    val userId = FirebaseAuth.getInstance().currentUser?.uid
    val transactions by viewModel.transactions.observeAsState(emptyList())

    // Log the transactions
    Log.d("PieChartScreen", "Transactions: $transactions")

    // Ensure the transactions list is loaded before proceeding
    if (transactions.isEmpty()) {
        Log.d("PieChartScreen", "No transactions to display.")
    }

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

    // Group filtered transactions by subcategory
    val groupedTransactions = filteredTransactions.groupBy { it.subCategoryName }

    // Log grouped transactions
    Log.d("PieChartScreen", "Grouped Transactions: $groupedTransactions")

    // Initialize PieChart data
    val pieEntries = mutableListOf<PieEntry>()
    val pieColors = mutableListOf<Int>()

    // Loop through each grouped transaction and calculate total amount for each subcategory
    groupedTransactions.forEach { (subCategoryName, transactionList) ->
        val totalAmount = transactionList.sumOf { it.getAmountAsDouble() }

        // Log the total amount for each subcategory
        Log.d("PieChartScreen", "Subcategory: $subCategoryName, Total Amount: $totalAmount")

        // Only add entries with non-zero amounts
        if (totalAmount > 0) {
            // Check if any transaction in the group is "income"
            val pieColor = if (transactionList.any { it.categoryName?.equals("income", ignoreCase = true) == true }) {
                Color(0xFF81C784).toArgb() // Green if any transaction is of category 'income'
            } else {
                // Apply colors for expenses
                when {
                    subCategoryName.contains("food", ignoreCase = true) -> Color(0xFFFF80AB).toArgb() // Pink for food expenses
                    else -> Color(0xFFEF9A9A).toArgb() // Red for other expenses
                }
            }
            // Log the pie color chosen for this subcategory
            Log.d("PieChartScreen", "PieColor for $subCategoryName: ${if (pieColor == Color.Green.toArgb()) "Green" else if (pieColor == Color.Magenta.toArgb()) "Pink" else "Orange"}")

            // Add PieEntry for each subcategory with corresponding color
            pieEntries.add(PieEntry(totalAmount.toFloat(), subCategoryName))
            pieColors.add(pieColor)
        }
    }

    // Ensure load transactions when userId is available
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
            if (pieEntries.isNotEmpty()) {
                AndroidView(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp),
                    factory = { ctx ->
                        PieChart(ctx).apply {
                            description.isEnabled = false

                            val dataSet = PieDataSet(pieEntries, "Transactions").apply {
                                colors = pieColors
                                valueTextSize = 14f
                                valueTextColor = Color.Black.toArgb()
                            }
                            val data = PieData(dataSet)
                            this.data = data

                            // Customize the chart appearance
                            setUsePercentValues(true)
                            isDrawHoleEnabled = true
                            holeRadius = 40f
                            setEntryLabelColor(Color.Black.toArgb())
                            legend.isEnabled = true
                            legend.textColor = Color.Black.toArgb()

                            invalidate() // Refresh the PieChart
                        }
                    },
                    update = { chart ->
                        val dataSet = PieDataSet(pieEntries, "Transactions").apply {
                            colors = pieColors
                            valueTextSize = 14f
                            valueTextColor = Color.Black.toArgb()
                        }
                        val data = PieData(dataSet)
                        chart.data = data
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

            // Spacer to give some space between the chart and the label
            Spacer(modifier = Modifier.height(16.dp))

            // Add labels below the chart
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp), // Padding for label
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Income & Expense",
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                    color = Color.Black,
                    modifier = Modifier.padding(8.dp)
                )
            }
        }
    }
}
