package com.kaeonx.moneymanager.fragments.transactions

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kaeonx.moneymanager.activities.AuthViewModel.Companion.userId
import com.kaeonx.moneymanager.customclasses.MutableLiveData2
import com.kaeonx.moneymanager.userrepository.UserRepository
import com.kaeonx.moneymanager.userrepository.domain.DayTransactions
import com.kaeonx.moneymanager.userrepository.domain.toDayTransactions
import kotlinx.coroutines.launch

private const val TAG = "tfvm"

class TransactionsFragmentViewModel : ViewModel() {
    init {
        Log.d(TAG, "TFVM started, with userId $userId")
    }

    private val userRepository = UserRepository.getInstance()
    val dayTransactions: LiveData<List<DayTransactions>> = Transformations.map(userRepository.transactions) {
        it.toDayTransactions("SGD")
    }
    val homeCurrency = MutableLiveData2("SGD")

    fun clearAllData() {
        viewModelScope.launch {
            userRepository.clearAllData()
        }
    }
}