package com.kaeonx.moneymanager.fragments.budget

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import java.util.*

@Suppress("UNCHECKED_CAST")
class BudgetsViewModelFactory(private val initCalendar: Calendar) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BudgetsViewModel::class.java)) {
            return BudgetsViewModel(initCalendar) as T
        }
        throw IllegalArgumentException("Unable to instantiate BudgetsViewModel: Unknown ViewModel class provided: ${modelClass.canonicalName}")
    }
}