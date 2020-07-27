package com.kaeonx.moneymanager.fragments.categories

import androidx.lifecycle.ViewModel
import com.kaeonx.moneymanager.userrepository.UserRepository

class TypeDisplayViewModel(userId: String, type: String) : ViewModel() {

    private val userRepository = UserRepository.getInstance(userId)
    val categories = when (type) {
        "Income" -> userRepository.incomeCategories
        "Expenses" -> userRepository.expensesCategories
        else -> throw IllegalStateException("Unknown type $type")
    }
}