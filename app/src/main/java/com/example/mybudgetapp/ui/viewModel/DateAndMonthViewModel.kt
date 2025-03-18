package com.example.mybudgetapp.ui.viewModel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class DateAndMonthViewModel: ViewModel() {
    private val _currentDate = LocalDate.now() // Get current date
    private val _formatterMonth = DateTimeFormatter.ofPattern("MMMM") // Format for displaying month
    private val _formatterYear = DateTimeFormatter.ofPattern("yyyy") // Format for displaying year


    private val _selectedMonth = MutableStateFlow(_currentDate.format(_formatterMonth))
    val selectedMonth = _selectedMonth.asStateFlow()

    private  val _selectedYear = MutableStateFlow(_currentDate.format(_formatterYear))
    val selectedYear = _selectedYear.asStateFlow()

    fun updateMonthAndYear(month:String, year:String){
        _selectedMonth.value = month
        _selectedYear.value = year
    }
}