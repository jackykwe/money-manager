package com.kaeonx.moneymanager.fragments.categories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.kaeonx.moneymanager.userrepository.domain.Category

@Suppress("UNCHECKED_CAST")
class CategoryEditViewModelFactory(private val oldCategory: Category) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CategoryEditViewModel::class.java)) {
            return CategoryEditViewModel(oldCategory) as T
        }
        throw IllegalArgumentException("Unable to instantiate CategoryEditViewModel: Unknown ViewModel class provided: ${modelClass.canonicalName}")
    }
}