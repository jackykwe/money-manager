package com.kaeonx.moneymanager.fragments.accounts

import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kaeonx.moneymanager.userrepository.UserPDS
import com.kaeonx.moneymanager.userrepository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AccountsViewModel : ViewModel() {

    private val userRepository by lazy { UserRepository.getInstance() }
    private val accounts = userRepository.accounts
    internal val accountSize = Transformations.map(accounts) { it ->
        it.size
    }

    ////////////////////////////////////////////////////////////////////////////////
    /**
     * Batch delete categories
     */
    ////////////////////////////////////////////////////////////////////////////////

    internal fun deleteAccounts(idList: List<Int>) {
        val chosenNames = accounts.value!!
            .filter { it.accountId in idList }
            .map { it.name }
        viewModelScope.launch(Dispatchers.Default) {
            userRepository.deleteAccountsTransactionSuspend(
                accountIds = idList,
                updateTstDefaultAccount = UserPDS.getString("tst_default_account") in chosenNames,
                newTstDefaultAccount = accounts.value!!.find { it.name !in chosenNames }!!.name
            )
        }
    }

}