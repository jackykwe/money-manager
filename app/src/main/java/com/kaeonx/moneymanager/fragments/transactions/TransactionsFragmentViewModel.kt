package com.kaeonx.moneymanager.fragments.transactions

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.kaeonx.moneymanager.activities.AuthViewModel.Companion.userId
import com.kaeonx.moneymanager.customclasses.MutableLiveData2
import com.kaeonx.moneymanager.customclasses.sumByBigDecimal
import com.kaeonx.moneymanager.handlers.CalendarHandler
import com.kaeonx.moneymanager.handlers.ColourHandler
import com.kaeonx.moneymanager.handlers.CurrencyHandler
import com.kaeonx.moneymanager.userrepository.UserPDS
import com.kaeonx.moneymanager.userrepository.UserRepository
import com.kaeonx.moneymanager.userrepository.domain.DayTransactions
import com.kaeonx.moneymanager.userrepository.domain.Transaction
import com.kaeonx.moneymanager.userrepository.domain.toDayTransactions
import com.kaeonx.moneymanager.xerepository.XERepository
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.*

private const val TAG = "tfvm"

class TransactionsFragmentViewModel : ViewModel() {
    init {
        Log.d(TAG, "TFVM started, with userId $userId")
    }

    private val userRepository = UserRepository.getInstance()
    private val xeRepository = XERepository.getInstance()

    private val _displayCalendar = MutableLiveData2(
        CalendarHandler.getStartOfMonthCalendar(Calendar.getInstance())
    )
    val displayCalendar: LiveData<Calendar>
        get() = _displayCalendar

    private var previousLiveData: LiveData<List<Transaction>>? = null
    private val _sensitiveDayTransactions = MediatorLiveData<List<DayTransactions>?>().apply {
        // TODO: Add budget source as well. Actually no need recalculate. Just update.
        // addSource(_some_budget_source) { value = value }  // Try this
        addSource(_displayCalendar) { updatePreviousLiveData() }
        addSource(userRepository.preferences) {
            viewModelScope.launch {
                value = recalculateDayTransactions()
            }
        }
        addSource(xeRepository.xeRows) {
            viewModelScope.launch {
                value = recalculateDayTransactions()
            }
        }
    }
    val sensitiveDayTransactions: LiveData<List<DayTransactions>?>
        get() = _sensitiveDayTransactions

    private suspend fun recalculateDayTransactions(): List<DayTransactions>? =
        previousLiveData?.value?.toDayTransactions()

    private fun updatePreviousLiveData() {
        if (previousLiveData != null) {
            _sensitiveDayTransactions.removeSource(previousLiveData!!)
        }
        previousLiveData = userRepository.getTransactionsBetween(
            _displayCalendar.value.timeInMillis,  // no need clone, since no edits will be made to it
            CalendarHandler.getEndOfMonthMillis(_displayCalendar.value.clone() as Calendar)
        )
        _sensitiveDayTransactions.addSource(previousLiveData!!) {
            viewModelScope.launch {
                _sensitiveDayTransactions.value = recalculateDayTransactions()
            }
        }
    }

    ////////////////////////////////////////////////////////////////////////////////
    /**
     * Calendar Manipulation
     */
    ////////////////////////////////////////////////////////////////////////////////

    internal fun monthMinusOne() {
        _displayCalendar.value = _displayCalendar.value.apply {
            this.add(Calendar.MONTH, -1)
        }
    }

    internal fun monthPlusOne() {
        _displayCalendar.value = _displayCalendar.value.apply {
            this.add(Calendar.MONTH, 1)
        }
    }

    internal fun updateMonthYear(newMonth: Int, newYear: Int) {
        _displayCalendar.value = _displayCalendar.value.apply {
            this.set(Calendar.MONTH, newMonth)
            this.set(Calendar.YEAR, newYear)
        }
    }

    ////////////////////////////////////////////////////////////////////////////////
    /**
     * SummaryData Processing
     */
    ////////////////////////////////////////////////////////////////////////////////

    private fun displayMonthCompare(): Int {
        val currentMonthFirstMillis = CalendarHandler.getStartOfMonthMillis(Calendar.getInstance())
        val displayMonthFirstMillis = _displayCalendar.value.timeInMillis
        return displayMonthFirstMillis.compareTo(currentMonthFirstMillis)
    }

    internal suspend fun getSummaryData(list: List<DayTransactions>): TransactionsSummaryData {
        val homeCurrency = UserPDS.getString("ccc_home_currency")
        val budget = BigDecimal("1000")  // TODO: Grab budget
        val income = list.sumByBigDecimal { BigDecimal(it.dayIncome ?: "0") }
        val expenses = list.sumByBigDecimal { BigDecimal(it.dayExpenses ?: "0") }
        val days = _displayCalendar.value.getActualMaximum(Calendar.DAY_OF_MONTH)

        val compare = displayMonthCompare()
        val day = when {
            compare == 0 -> Calendar.getInstance().get(Calendar.DAY_OF_MONTH)  // current month
            compare < 0 -> days
            compare > 0 -> 0
            else -> throw IllegalStateException("Did I fail math")
        }
        val dayDivDays =
            BigDecimal(day).divide(BigDecimal(days), 2, RoundingMode.HALF_UP)

        val entries: List<PieEntry>
        val colourList: List<Int>
        if (expenses < budget) {
            val exDivBud = expenses.divide(budget, 2, RoundingMode.HALF_UP)
            if (exDivBud <= dayDivDays) {
                entries = listOf(
                    PieEntry(exDivBud.toFloat(), "ex"),
                    PieEntry(dayDivDays.minus(exDivBud).toFloat(), "target ex"),
                    PieEntry(BigDecimal.ONE.minus(dayDivDays).toFloat(), "remainder")
                )
                colourList = listOf(
                    ColourHandler.getColourObject("Green,500"),
                    ColourHandler.getColourObject("Grey,200"),
                    ColourHandler.getColourObject("White")
                )
            } else {
                entries = listOf(
                    PieEntry(dayDivDays.toFloat(), "target ex"),
                    PieEntry(exDivBud.minus(dayDivDays).toFloat(), "ex"),
                    PieEntry(BigDecimal.ONE.minus(exDivBud).toFloat(), "remainder")
                )
                colourList = listOf(
                    ColourHandler.getColourObject("Green,500"),
                    ColourHandler.getColourObject("Amber,500"),
                    ColourHandler.getColourObject("White")
                )
            }
        } else {
            val budDivEx = budget.divide(expenses, 2, RoundingMode.HALF_UP)
            entries = listOf(
                PieEntry(dayDivDays.times(budDivEx).toFloat(), "target ex"),
                PieEntry(budDivEx.toFloat(), "bud"),
                PieEntry(BigDecimal.ONE.minus(budDivEx).toFloat(), "over ex")
            )
            colourList = listOf(
                ColourHandler.getColourObject("Green,500"),
                ColourHandler.getColourObject("Amber,500"),
                ColourHandler.getColourObject("Red,500")
            )
        }

        val dataSet = PieDataSet(entries, null).apply {
            colors = colourList
            setDrawValues(false)
//            sliceSpace = 1f  // in dp (as float)
        }
        val bool1 = !(UserPDS.getBoolean("ccc_hide_matching_currency") &&
                list.all { it.expensesAllHome })
        val bool2 = !(UserPDS.getBoolean("ccc_hide_matching_currency") &&
                list.all { it.incomeAllHome } &&
                list.all { it.expensesAllHome })
        return TransactionsSummaryData(
            homeCurrency = homeCurrency,
            showCurrencyMode1 = bool1,
            showCurrencyMode2 = bool2,
            budget = CurrencyHandler.displayAmount(budget),
            monthIncome = CurrencyHandler.displayAmount(income),
            monthExpenses = CurrencyHandler.displayAmount(expenses),
            monthBalance = CurrencyHandler.displayAmount(income.minus(expenses)),
            pieData = PieData(dataSet)
        )
    }
}