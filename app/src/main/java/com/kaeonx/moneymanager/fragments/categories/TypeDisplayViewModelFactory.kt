package com.kaeonx.moneymanager.fragments.categories

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

@Suppress("UNCHECKED_CAST")
class TypeDisplayViewModelFactory(private val application: Application, private val userId: String, private val type: String) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TypeDisplayViewModel::class.java)) {
            return TypeDisplayViewModel(application, userId, type) as T
        }
        throw IllegalArgumentException("Unable to instantiate TypeDisplayViewModel: Unknown ViewModel class provided: ${modelClass.canonicalName}")
    }
}