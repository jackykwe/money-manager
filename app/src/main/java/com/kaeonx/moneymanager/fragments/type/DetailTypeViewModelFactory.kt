package com.kaeonx.moneymanager.fragments.type

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import java.util.*

@Suppress("UNCHECKED_CAST")
class DetailTypeViewModelFactory(
    private val type: String,
    private val initCalendar: Calendar,
    private val showCurrency: Boolean
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DetailTypeViewModel::class.java)) {
            return DetailTypeViewModel(type, initCalendar, showCurrency) as T
        }
        throw IllegalArgumentException("Unable to instantiate DetailTypeViewModel: Unknown ViewModel class provided: ${modelClass.canonicalName}")
    }
}