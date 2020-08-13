package com.kaeonx.moneymanager.fragments.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import java.util.*

@Suppress("UNCHECKED_CAST")
class DetailCategoryViewModelFactory(
    private val yearModeEnabled: Boolean,
    private val isYearMode: Boolean,
    private val initArchiveCalendarStart: Calendar,
    private val type: String,
    private val category: String,
    private val initCalendar: Calendar
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DetailCategoryViewModel::class.java)) {
            return DetailCategoryViewModel(
                yearModeEnabled,
                isYearMode,
                initArchiveCalendarStart,
                type,
                category,
                initCalendar
            ) as T
        }
        throw IllegalArgumentException("Unable to instantiate DetailCategoryViewModel: Unknown ViewModel class provided: ${modelClass.canonicalName}")
    }
}