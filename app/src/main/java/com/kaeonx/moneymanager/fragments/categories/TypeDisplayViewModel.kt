package com.kaeonx.moneymanager.fragments.categories

import androidx.lifecycle.ViewModel
import com.kaeonx.moneymanager.userrepository.UserRepository

class TypeDisplayViewModel(type: String) : ViewModel() {

    private val userRepository = UserRepository.getInstance()
    val categories = when (type) {
        "Income" -> userRepository.incomeCategories
        "Expenses" -> userRepository.expensesCategories
        else -> throw IllegalStateException("Unknown type $type")
    }
}