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
import com.kaeonx.moneymanager.userrepository.domain.Transaction
import com.kaeonx.moneymanager.userrepository.domain.toDayTransactions
import com.kaeonx.moneymanager.xerepository.XERepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.*

private const val TAG = "tfvm"

class TransactionsViewModel : ViewModel() {
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

    private val _overallBudget = userRepository.getBudget("Overall")

    private var previousLiveData: LiveData<List<Transaction>>? = null
    private val _transactionsRVPacket = MediatorLiveData<TransactionsRVPacket?>().apply {
        addSource(_overallBudget) { recalculateTransactionsRVPacket(previousLiveData?.value) }  // Not sure if necessary
        addSource(_displayCalendar) { updatePreviousLiveData() }
        addSource(userRepository.preferences) { recalculateTransactionsRVPacket(previousLiveData?.value) }
        addSource(xeRepository.xeRows) { recalculateTransactionsRVPacket(previousLiveData?.value) }
    }
    val transactionsRVPacket: LiveData<TransactionsRVPacket?>
        get() = _transactionsRVPacket

    private fun updatePreviousLiveData() {
        if (previousLiveData != null) {
            _transactionsRVPacket.removeSource(previousLiveData!!)
        }
        previousLiveData = userRepository.getTransactionsBetween(
            _displayCalendar.value.timeInMillis,  // no need clone, since no edits will be made to it
            CalendarHandler.getEndOfMonthMillis(_displayCalendar.value.clone() as Calendar)
        )
        _transactionsRVPacket.addSource(previousLiveData!!) { recalculateTransactionsRVPacket(it) }
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

    private fun recalculateTransactionsRVPacket(list: List<Transaction>?) {
        if (list == null) return
        viewModelScope.launch(Dispatchers.Default) {
            val dayTransactionsList = list.toDayTransactions()
            val homeCurrency = UserPDS.getString("ccc_home_currency")

            val income = dayTransactionsList.sumByBigDecimal { BigDecimal(it.dayIncome ?: "0") }
            val expenses = dayTransactionsList.sumByBigDecimal { BigDecimal(it.dayExpenses ?: "0") }
            val showHomeCurrencyMode2 = !(UserPDS.getBoolean("ccc_hide_matching_currency") &&
                    dayTransactionsList.all { it.incomeAllHome } &&
                    dayTransactionsList.all { it.expensesAllHome })

            val budgetObj = _overallBudget.value
            val summaryData = if (budgetObj == null) {
                TransactionsSummaryData(
                    showBudgetCurrency = null,
                    showHomeCurrencyMode1 = null,
                    showHomeCurrencyMode2 = showHomeCurrencyMode2,
                    budgetCurrency = null,
                    budget = null,
                    homeCurrency = homeCurrency,
                    monthExpenses = CurrencyHandler.displayAmount(expenses),
                    pieData = null,
                    monthIncome = CurrencyHandler.displayAmount(income),
                    monthBalance = CurrencyHandler.displayAmount(income.minus(expenses))
                )
            } else {
                val budgetAmount = BigDecimal(budgetObj.originalAmount)
                var spentAmountIBC = BigDecimal.ZERO  // In Budget's Currency
                list.filter { it.type == "Expenses" }.forEach {
                    spentAmountIBC = spentAmountIBC.plus(
                        if (it.originalCurrency == budgetObj.originalCurrency) {
                            BigDecimal(it.originalAmount)
                        } else {
                            CurrencyHandler.convertAmountViaProxy(
                                BigDecimal(it.originalAmount),
                                foreignCurrencySrc = it.originalCurrency,
                                homeCurrencyPxy = homeCurrency,
                                foreignCurrencyDst = budgetObj.originalCurrency
                            )
                        }
                    )
                }

                val dayDivDays = CalendarHandler.getDayDivDays(_displayCalendar.value, 4)

                val entries: List<PieEntry>
                val colourList: List<Int>
                if (spentAmountIBC <= budgetAmount) {
                    val exDivBud = spentAmountIBC.divide(budgetAmount, 2, RoundingMode.HALF_UP)
                    if (exDivBud <= dayDivDays) {
                        entries = listOf(
                            PieEntry(exDivBud.toFloat(), "ex"),
                            PieEntry(dayDivDays.minus(exDivBud).toFloat(), "target ex"),
                            PieEntry(BigDecimal.ONE.minus(dayDivDays).toFloat(), "remainder")
                        )
                        colourList = listOf(
                            ColourHandler.getColourObjectOf("Green,500"),
                            ColourHandler.getColourObjectOf("Grey,200"),
                            ColourHandler.getColourObjectOf("White")
                        )
                    } else {
                        entries = listOf(
                            PieEntry(dayDivDays.toFloat(), "target ex"),
                            PieEntry(exDivBud.minus(dayDivDays).toFloat(), "ex"),
                            PieEntry(BigDecimal.ONE.minus(exDivBud).toFloat(), "remainder")
                        )
                        colourList = listOf(
                            ColourHandler.getColourObjectOf("Green,500"),
                            ColourHandler.getColourObjectOf("Amber,500"),
                            ColourHandler.getColourObjectOf("White")
                        )
                    }
                } else {
                    val budDivEx = budgetAmount.divide(spentAmountIBC, 2, RoundingMode.HALF_UP)
                    val dayDivDaysTimesBudDivEx = dayDivDays.times(budDivEx)
                    entries = listOf(
                        PieEntry(dayDivDaysTimesBudDivEx.toFloat(), "target ex"),
                        PieEntry(budDivEx.minus(dayDivDaysTimesBudDivEx).toFloat(), "bud"),
                        PieEntry(BigDecimal.ONE.minus(budDivEx).toFloat(), "over ex")
                    )
                    colourList = listOf(
                        ColourHandler.getColourObjectOf("Green,500"),
                        ColourHandler.getColourObjectOf("Amber,500"),
                        ColourHandler.getColourObjectOf("Red,500")
                    )
                }

                val dataSet = PieDataSet(entries, null).apply {
                    colors = colourList
                    setDrawValues(false)
//                    sliceSpace = 1f  // in dp (as float)
                }
                TransactionsSummaryData(
                    showBudgetCurrency = budgetObj.originalCurrency != homeCurrency,
                    showHomeCurrencyMode1 = !(
                            UserPDS.getBoolean("ccc_hide_matching_currency") && dayTransactionsList.all { it.expensesAllHome }
                            ),
                    showHomeCurrencyMode2 = showHomeCurrencyMode2,
                    budgetCurrency = budgetObj.originalCurrency,
                    budget = budgetObj.originalAmount,
                    homeCurrency = homeCurrency,
                    monthExpenses = CurrencyHandler.displayAmount(expenses),
                    pieData = PieData(dataSet),
                    monthIncome = CurrencyHandler.displayAmount(income),
                    monthBalance = CurrencyHandler.displayAmount(income.minus(expenses))
                )
            }

            val headerString = CalendarHandler.getFormattedString(
                displayCalendar.value!!.clone() as Calendar,
                "MMM yyyy"
            )
            withContext(Dispatchers.Main) {
                _transactionsRVPacket.value = TransactionsRVPacket(
                    newList = dayTransactionsList,
                    headerString = headerString,
                    summaryData = summaryData
                )
            }
        }
    }
}