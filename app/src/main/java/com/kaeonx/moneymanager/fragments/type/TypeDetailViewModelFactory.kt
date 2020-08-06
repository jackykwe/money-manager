package com.kaeonx.moneymanager.fragments.type

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import java.util.*

@Suppress("UNCHECKED_CAST")
class TypeDetailViewModelFactory(
    private val type: String,
    private val initCalendar: Calendar,
    private val showCurrency: Boolean
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TypeDetailViewModel::class.java)) {
            return TypeDetailViewModel(type, initCalendar, showCurrency) as T
        }
        throw IllegalArgumentException("Unable to instantiate TypeDetailViewModel: Unknown ViewModel class provided: ${modelClass.canonicalName}")
    }
}