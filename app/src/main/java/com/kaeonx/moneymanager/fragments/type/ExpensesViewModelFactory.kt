package com.kaeonx.moneymanager.fragments.type

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import java.util.*

@Suppress("UNCHECKED_CAST")
class ExpensesViewModelFactory(
    private val displayCalendar: Calendar,
    private val showCurrency: Boolean
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ExpensesViewModel::class.java)) {
            return ExpensesViewModel(displayCalendar, showCurrency) as T
        }
        throw IllegalArgumentException("Unable to instantiate ExpensesViewModel: Unknown ViewModel class provided: ${modelClass.canonicalName}")
    }
}