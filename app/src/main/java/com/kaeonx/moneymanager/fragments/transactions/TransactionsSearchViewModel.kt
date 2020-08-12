package com.kaeonx.moneymanager.fragments.transactions

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kaeonx.moneymanager.userrepository.UserRepository
import com.kaeonx.moneymanager.userrepository.domain.DayTransactions
import com.kaeonx.moneymanager.userrepository.domain.Transaction
import com.kaeonx.moneymanager.userrepository.domain.toDayTransactions
import com.kaeonx.moneymanager.xerepository.XERepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TransactionsSearchViewModel(initQuery: String) : ViewModel() {

    internal var currentQuery: String = initQuery
        private set

    private val userRepository = UserRepository.getInstance()
    private val xeRepository = XERepository.getInstance()

    private var previousLiveData: LiveData<List<Transaction>>? = null

    private val _transactionsSearchRVPacket =
        MediatorLiveData<TransactionsSearchRVPacket?>().apply {
            addSource(userRepository.preferences) { recalculateDayTransactions() }
            addSource(xeRepository.xeRows) { recalculateDayTransactions() }
        }
    val transactionsSearchRVPacket: LiveData<TransactionsSearchRVPacket?>
        get() = _transactionsSearchRVPacket

    init {
        reSearch(initQuery)
    }

    private fun recalculateDayTransactions() {
        val previousLiveData = previousLiveData
        if (previousLiveData == null || previousLiveData.value == null) return
        viewModelScope.launch(Dispatchers.Default) {
            val currentQuery = currentQuery
            val dayTransactions: List<DayTransactions>
            val count: Int
            previousLiveData.value!!.let {
                count = it.size
                dayTransactions = it.toDayTransactions()
            }
            withContext(Dispatchers.Main) {
                _transactionsSearchRVPacket.value = TransactionsSearchRVPacket(
                    resultText = "$count Transactions found containing",
                    searchQuery = currentQuery,
                    dayTransactions = dayTransactions
                )
            }
        }
    }

    internal fun reSearch(newQuery: String) {
        if (previousLiveData != null) {
            _transactionsSearchRVPacket.removeSource(previousLiveData!!)
        }
        currentQuery = newQuery
        if (newQuery.isBlank()) {
            _transactionsSearchRVPacket.value = TransactionsSearchRVPacket(
                resultText = "Please enter a search string above",
                searchQuery = "",
                dayTransactions = listOf()
            )
        } else {
            previousLiveData = userRepository.searchTransactions(newQuery)
            _transactionsSearchRVPacket.addSource(previousLiveData!!) { recalculateDayTransactions() }
        }
    }
}