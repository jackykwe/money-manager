package com.kaeonx.moneymanager.fragments.transactions

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import com.kaeonx.moneymanager.activities.AuthViewModel.Companion.userId
import com.kaeonx.moneymanager.customclasses.MutableLiveData2
import com.kaeonx.moneymanager.handlers.CalendarHandler
import com.kaeonx.moneymanager.userrepository.UserRepository
import com.kaeonx.moneymanager.userrepository.domain.DayTransactions
import com.kaeonx.moneymanager.userrepository.domain.Transaction
import com.kaeonx.moneymanager.userrepository.domain.toDayTransactions
import com.kaeonx.moneymanager.xerepository.XERepository
import java.util.*

private const val TAG = "tfvm"

class TransactionsFragmentViewModel : ViewModel() {
    init {
        Log.d(TAG, "TFVM started, with userId $userId")
    }

    private val _displayCalendar = MutableLiveData2(Calendar.getInstance())
    val displayCalendar: LiveData<Calendar>
        get() = _displayCalendar

    fun monthMinusOne() {
        _displayCalendar.value = _displayCalendar.value.apply {
            this.add(Calendar.MONTH, -1)
        }
    }

    fun monthPlusOne() {
        _displayCalendar.value = _displayCalendar.value.apply {
            this.add(Calendar.MONTH, 1)
        }
    }

    fun updateCalendar(newMonth: Int, newYear: Int) {
        _displayCalendar.value = _displayCalendar.value.apply {
            this.set(Calendar.MONTH, newMonth)
            this.set(Calendar.YEAR, newYear)
        }
    }

    private val userRepository = UserRepository.getInstance()
    private val xeRepository = XERepository.getInstance()

    private lateinit var previousLiveData: LiveData<List<Transaction>>

    val sensitiveDayTransactions = MediatorLiveData<List<DayTransactions>>().apply {
        addSource(_displayCalendar) { updatePreviousLiveData() }
        addSource(userRepository.preferences) {
            xeRepository.checkAndUpdateIfNecessary()
            value = recalculateDayTransactions()
        }
        addSource(xeRepository.xeRows) { value = recalculateDayTransactions() }
    }

    private fun recalculateDayTransactions(): List<DayTransactions> =
        previousLiveData.value?.toDayTransactions() ?: listOf()

    private fun updatePreviousLiveData() {
        if (this::previousLiveData.isInitialized) {
            sensitiveDayTransactions.removeSource(previousLiveData)
        }
        previousLiveData = userRepository.getTransactionsBetween(
            CalendarHandler.getStartOfMonthMillis(displayCalendar.value!!),
            CalendarHandler.getEndOfMonthMillis(displayCalendar.value!!)
        )
        sensitiveDayTransactions.addSource(previousLiveData) {
            sensitiveDayTransactions.value = recalculateDayTransactions()
        }
    }

}