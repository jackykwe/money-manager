package com.kaeonx.moneymanager.fragments.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.kaeonx.moneymanager.chartcomponents.generateLineChartPacket
import com.kaeonx.moneymanager.customclasses.MutableLiveData2
import com.kaeonx.moneymanager.handlers.CalendarHandler
import com.kaeonx.moneymanager.handlers.ColourHandler
import com.kaeonx.moneymanager.handlers.CurrencyHandler
import com.kaeonx.moneymanager.userrepository.UserPDS
import com.kaeonx.moneymanager.userrepository.UserRepository
import com.kaeonx.moneymanager.userrepository.domain.Transaction
import com.kaeonx.moneymanager.xerepository.XERepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.*

class DetailCategoryViewModel(
    yearModeEnabled: Boolean,
    initIsYearMode: Boolean,
    initArchiveCalendarStart: Calendar,
    private val type: String,
    private val category: String,
    initCalendar: Calendar
) : ViewModel() {

    private val userRepository = UserRepository.getInstance()
    private val xeRepository = XERepository.getInstance()

    ////////////////////////////////////////////////////////////////////////////////
    /**
     * Calendar Manipulation
     */
    ////////////////////////////////////////////////////////////////////////////////
    private val _displayCalendarStart = MutableLiveData2(initCalendar)
    val displayCalendarStart: LiveData<Calendar>
        get() = _displayCalendarStart
    private val _displayCalendarEnd = MutableLiveData2(
        if (yearModeEnabled && initIsYearMode) {
            (CalendarHandler.getEndOfYearCalendar(initCalendar.clone() as Calendar))
        } else {
            (CalendarHandler.getEndOfMonthCalendar(initCalendar.clone() as Calendar))
        }
    )

    internal fun selectMonth(newMonth: Int, newYear: Int) {
        isYearMode = false
        _displayCalendarStart.value = _displayCalendarStart.value.apply {
            set(Calendar.MONTH, newMonth)
            set(Calendar.YEAR, newYear)
        }
        _displayCalendarEnd.value = CalendarHandler.getEndOfMonthCalendar(
            _displayCalendarStart.value.clone() as Calendar
        )
    }

    private var isYearMode = initIsYearMode
    private var archiveCalendarStart = initArchiveCalendarStart
    internal fun toggleView() {  // If this function runs, means yearModeEnabled is true.
        if (!isYearMode) {
            archiveCalendarStart = _displayCalendarStart.value.clone() as Calendar
            _displayCalendarStart.value = CalendarHandler.getStartOfYearCalendar(
                _displayCalendarStart.value
            )
            _displayCalendarEnd.value = CalendarHandler.getEndOfYearCalendar(
                _displayCalendarStart.value.clone() as Calendar
            )
            isYearMode = true
        } else {
            _displayCalendarStart.value = archiveCalendarStart
            _displayCalendarEnd.value = CalendarHandler.getEndOfMonthCalendar(
                _displayCalendarStart.value.clone() as Calendar
            )
            isYearMode = false
        }
    }

    ////////////////////////////////////////////////////////////////////////////////
    /**
     * Data Manipulation
     */
    ////////////////////////////////////////////////////////////////////////////////
    private var _transactions: LiveData<List<Transaction>>? = null
    private fun updateTransactions() {
        if (_transactions != null) {
            _categoryTypeRVPacket.removeSource(_transactions!!)
        }
        _transactions = userRepository.getCategoryTransactionsBetween(
            type,
            category,
            _displayCalendarStart.value.timeInMillis,  // no need clone, since no edits will be made to it
            _displayCalendarEnd.value.timeInMillis  // no need clone, since no edits will be made to it
        )
        _categoryTypeRVPacket.addSource(_transactions!!) { recalculateDetailCategoryRVPacket(it) }
    }

    private val _categoryTypeRVPacket = MediatorLiveData<DetailCategoryRVPacket?>().apply {
        addSource(_displayCalendarEnd) { updateTransactions() }
        addSource(xeRepository.xeRows) { recalculateDetailCategoryRVPacket(_transactions?.value) }
    }
    val categoryTypeRVPacket: LiveData<DetailCategoryRVPacket?>
        get() = _categoryTypeRVPacket

    private fun getNumberOfDays() = CalendarHandler.calculateNumberOfDays(
        _displayCalendarStart.value,
        _displayCalendarEnd.value
    )

    private fun getNumberOfMonths() = CalendarHandler.calculateNumberOfMonths(
        _displayCalendarStart.value,
        _displayCalendarEnd.value
    )

    private fun generateRangeString(numberOfMonths: Int): String = when (numberOfMonths) {
        1 -> CalendarHandler.getFormattedString(
            _displayCalendarStart.value.clone() as Calendar,
            "MMM yyyy"
        ).toUpperCase(Locale.ROOT)
        12 -> CalendarHandler.getFormattedString(
            _displayCalendarStart.value.clone() as Calendar,
            "yyyy"
        ).toUpperCase(Locale.ROOT)
        else -> throw IllegalStateException("Variable ranges not yet implemented")
    }

    private fun recalculateDetailCategoryRVPacket(list: List<Transaction>?) {
        if (list == null) return
        viewModelScope.launch(Dispatchers.Default) {
            val numberOfDays = getNumberOfDays()
            val numberOfMonths = getNumberOfMonths()
            val rangeString = generateRangeString(numberOfMonths)

            val homeCurrency = UserPDS.getString("ccc_home_currency")
            val colourInt = ColourHandler.getColourObject(
                userRepository.categories.value!!
                    .find { it.name == category && it.type == type }!!
                    .colourString
            )

            val showRangeCurrency = list.any { it.originalCurrency != homeCurrency }
            var rangeAmount = BigDecimal.ZERO
            var highestAmount = BigDecimal.ZERO
            list.forEach {
                // Calculates rangeAmount and highestAmount
                it.homeAmount = if (it.originalCurrency == homeCurrency) {
                    BigDecimal(it.originalAmount)
                } else {
                    CurrencyHandler.convertAmount(
                        BigDecimal(it.originalAmount),
                        it.originalCurrency,
                        homeCurrency
                    )
                }
                rangeAmount = rangeAmount.plus(it.homeAmount)
                if (it.homeAmount > highestAmount) highestAmount =
                    it.homeAmount  // remember that _transactions is sorted by DESC timestamp
            }

            val transactionLLDataAL = arrayListOf<DetailCategoryTransactionLLData>()
            list//.sortedByDescending { it.homeAmount } // So that highest valued transaction stays on top
                .forEach {
                    // Calculates everything else
                    val percent = it.homeAmount.times(BigDecimal("100"))
                        .divide(rangeAmount, 3, RoundingMode.HALF_UP)
                    // should be between 0 and 100. No need LargePercentFormatter.
                    val percentDisplay = percent.setScale(1, RoundingMode.HALF_EVEN)

                    transactionLLDataAL.add(
                        DetailCategoryTransactionLLData(
                            transaction = it,
                            transactionPercent = "(${CurrencyHandler.displayAmount(percentDisplay)}%)",
                            showCurrency = it.originalCurrency != homeCurrency,
                            barData = BarData(
                                BarDataSet(
                                    listOf(
                                        BarEntry(
                                            0f,
                                            it.homeAmount.divide(
                                                highestAmount,
                                                3,
                                                RoundingMode.HALF_UP
                                            ).toFloat()
                                        )
                                    ),
                                    null
                                ).apply {
                                    color = colourInt
                                    setDrawValues(false)
                                }
                            ).apply {
                                barWidth = 1f
                            }
                        )
                    )
                }

            val result = DetailCategoryRVPacket(
                lineChartPacket = generateLineChartPacket(
                    list = list,
                    calendarStart = _displayCalendarStart.value,
                    numberOfDays = numberOfDays,
                    numberOfMonths = numberOfMonths,
                    colourInt = colourInt,
                    rangeAmount = rangeAmount,
                    showCurrency = showRangeCurrency,
                    homeCurrency = homeCurrency
                ),
                transactionsRangeString = rangeString,
                transactionsShowRangeCurrency = showRangeCurrency,
                transactionsRangeCurrency = homeCurrency,
                transactionsRangeAmount = CurrencyHandler.displayAmount(rangeAmount),
                transactionLLData = transactionLLDataAL
            )

            withContext(Dispatchers.Main) {
                _categoryTypeRVPacket.value = result
            }
        }
    }
}