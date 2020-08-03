package com.kaeonx.moneymanager.fragments.transactions

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.kaeonx.moneymanager.R
import com.kaeonx.moneymanager.activities.App
import com.kaeonx.moneymanager.activities.AuthViewModel.Companion.userId
import com.kaeonx.moneymanager.customclasses.MutableLiveData2
import com.kaeonx.moneymanager.handlers.CalendarHandler
import com.kaeonx.moneymanager.handlers.CurrencyHandler
import com.kaeonx.moneymanager.userrepository.UserPDS
import com.kaeonx.moneymanager.userrepository.UserRepository
import com.kaeonx.moneymanager.userrepository.domain.DayTransactions
import com.kaeonx.moneymanager.userrepository.domain.Transaction
import com.kaeonx.moneymanager.userrepository.domain.toDayTransactions
import com.kaeonx.moneymanager.xerepository.XERepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode
import java.util.*

private const val TAG = "tfvm"

class TransactionsFragmentViewModel : ViewModel() {
    init {
        Log.d(TAG, "TFVM started, with userId $userId")
    }

    private val _displayCalendar = MutableLiveData2(
        CalendarHandler.getStartOfMonthCalendar(Calendar.getInstance())
    )
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

    fun updateMonthYear(newMonth: Int, newYear: Int) {
        _displayCalendar.value = _displayCalendar.value.apply {
            this.set(Calendar.MONTH, newMonth)
            this.set(Calendar.YEAR, newYear)
        }
    }

    private val userRepository = UserRepository.getInstance()
    private val xeRepository = XERepository.getInstance()

    private var previousLiveData: LiveData<List<Transaction>>? = null
    val sensitiveDayTransactions = MediatorLiveData<List<DayTransactions>>().apply {
        // TODO: Add budget source as well. Actually no need recalculate. Just update.
        // addSource(_some_budget_source) { value = value }  // Try this
        addSource(_displayCalendar) { updatePreviousLiveData() }
        addSource(userRepository.preferences) { value = recalculateDayTransactions() }
        addSource(xeRepository.xeRows) { value = recalculateDayTransactions() }
    }

    private fun recalculateDayTransactions(): List<DayTransactions> =
        previousLiveData?.value?.toDayTransactions() ?: listOf()

    private fun updatePreviousLiveData() {
        if (previousLiveData != null) {
            sensitiveDayTransactions.removeSource(previousLiveData!!)
        }
        previousLiveData = userRepository.getTransactionsBetween(
            displayCalendar.value!!.timeInMillis,  // no need clone, since no edits will be made to it
            CalendarHandler.getEndOfMonthMillis(displayCalendar.value!!.clone() as Calendar)
        )
        sensitiveDayTransactions.addSource(previousLiveData!!) {
            sensitiveDayTransactions.value = recalculateDayTransactions()
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

    suspend fun getSummaryData(list: List<DayTransactions>): SummaryData {
        return withContext(Dispatchers.Default) {
            val homeCurrency = UserPDS.getString("ccc_home_currency")
            val budget = BigDecimal("1000")  // TODO: Grab budget
            val income = list.sumByBigDecimal { it.dayIncome ?: "0" }
            val expenses = list.sumByBigDecimal { it.dayExpenses ?: "0" }
            val days = _displayCalendar.value.getActualMaximum(Calendar.DAY_OF_MONTH)

            val compare = displayMonthCompare()
            val day = when {
                compare == 0 -> Calendar.getInstance().get(Calendar.DAY_OF_MONTH)  // current month
                compare < 0 -> days
                compare > 0 -> 0
                else -> throw IllegalStateException("Did I fail math")
            }
            val dayDivDays =
                BigDecimal(day).divide(BigDecimal(days), MathContext(2, RoundingMode.HALF_UP))

            val entries: List<PieEntry>
            val colorList: List<Int>
            if (expenses < budget) {
                val exDivBud = expenses.divide(budget, MathContext(2, RoundingMode.HALF_UP))
                if (exDivBud <= dayDivDays) {
                    entries = listOf(
                        PieEntry(exDivBud.toFloat(), "ex"),
                        PieEntry(dayDivDays.minus(exDivBud).toFloat(), "target ex"),
                        PieEntry(BigDecimal.ONE.minus(dayDivDays).toFloat(), "remainder")
                    )
                    colorList = listOf(
                        App.context.resources.getColor(R.color.green_500, null),
                        App.context.resources.getColor(R.color.grey_200, null),
                        App.context.resources.getColor(R.color.white, null)
                    )
                } else {
                    entries = listOf(
                        PieEntry(dayDivDays.toFloat(), "target ex"),
                        PieEntry(exDivBud.minus(dayDivDays).toFloat(), "ex"),
                        PieEntry(BigDecimal.ONE.minus(exDivBud).toFloat(), "remainder")
                    )
                    colorList = listOf(
                        App.context.resources.getColor(R.color.green_500, null),
                        App.context.resources.getColor(R.color.green_200, null),
                        App.context.resources.getColor(R.color.white, null)
                    )
                }
            } else {
                val budDivEx = budget.divide(expenses, MathContext(2, RoundingMode.HALF_UP))
                entries = listOf(
                    PieEntry(dayDivDays.times(budDivEx).toFloat(), "target ex"),
                    PieEntry(budDivEx.toFloat(), "bud"),
                    PieEntry(BigDecimal.ONE.minus(budDivEx).toFloat(), "over ex")
                )
                colorList = listOf(
                    App.context.resources.getColor(R.color.green_500, null),
                    App.context.resources.getColor(R.color.green_200, null),
                    App.context.resources.getColor(R.color.red_500, null)
                )
            }

            val dataSet = PieDataSet(entries, null).apply {
                colors = colorList
                setDrawValues(false)
//            sliceSpace = 1f  // in dp (as float)
            }
            val bool1 = !(UserPDS.getBoolean("ccc_hide_matching_currency") &&
                    list.all { it.expensesAllHome })
            val bool2 = !(UserPDS.getBoolean("ccc_hide_matching_currency") &&
                    list.all { it.incomeAllHome } &&
                    list.all { it.expensesAllHome })
            SummaryData(
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

    // Courtesy of https://bezkoder.com/kotlin-sum-sumby-sumbydouble-bigdecimal-list-map-example/
    private suspend fun <T> Iterable<T>.sumByBigDecimal(selector: (T) -> String): BigDecimal {
        return withContext(Dispatchers.Default) {
            var sum: BigDecimal = BigDecimal.ZERO
            for (element in this@sumByBigDecimal) {
                sum = sum.plus(BigDecimal(selector(element)))
            }
            sum
        }
    }

}