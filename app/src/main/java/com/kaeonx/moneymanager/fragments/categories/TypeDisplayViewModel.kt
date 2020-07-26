package com.kaeonx.moneymanager.fragments.categories

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.kaeonx.moneymanager.userrepository.UserRepository

class TypeDisplayViewModel(application: Application, userId: String, type: String) : AndroidViewModel(application) {

    private val userRepository = UserRepository.getInstance(application, userId)
    val categories = when (type) {
        "Income" -> userRepository.incomeCategories
        "Expenses" -> userRepository.expensesCategories
        else -> throw IllegalStateException("Unknown type $type")
    }
}