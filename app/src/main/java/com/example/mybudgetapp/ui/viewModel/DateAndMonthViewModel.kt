package com.example.mybudgetapp.ui.viewModel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class DateAndMonthViewModel: ViewModel() {
    private val _selectedMonth = MutableStateFlow("")
    val selectedMonth = _selectedMonth.asStateFlow()

    private  val _selectedYear = MutableStateFlow("")
    val selectedYear = _selectedYear.asStateFlow()

    fun updateMonthAndYear(month:String, year:String){
        _selectedMonth.value = month
        _selectedYear.value = year
    }
}