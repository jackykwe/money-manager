package com.kaeonx.moneymanager.fragments.accounts

import androidx.lifecycle.ViewModel
import com.kaeonx.moneymanager.userrepository.UserRepository

class AccountsDisplayViewModel : ViewModel() {

    private val userRepository = UserRepository.getInstance()
    val accounts = userRepository.accounts

}