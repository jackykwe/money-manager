package com.kaeonx.moneymanager.fragments.categories

import androidx.lifecycle.ViewModel
import com.kaeonx.moneymanager.userrepository.UserRepository

class TypeDisplayViewModel : ViewModel() {

    private val userRepository = UserRepository.getInstance()
    val categories = userRepository.categories
}