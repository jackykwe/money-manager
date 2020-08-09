package com.kaeonx.moneymanager.fragments.detail

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.kaeonx.moneymanager.chartcomponents.generateLineChartPacket
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

private const val TAG = "dcvm"

class DetailCategoryViewModel(
    private val type: String,
    private val category: String,
    private val calendarStart: Calendar,
    private val calendarEnd: Calendar
) : ViewModel() {

    init {
        Log.d(
            TAG,
            "startMillis is ${calendarStart.timeInMillis} and endMillis is ${calendarEnd.timeInMillis}"
        )
    }

    private val userRepository = UserRepository.getInstance()
    private val xeRepository = XERepository.getInstance()

    private val _transactions = userRepository.getCategoryTransactionsBetween(
        type,
        category,
        calendarStart.timeInMillis,  // no need clone, since no edits will be made to it
        calendarEnd.timeInMillis  // no need clone, since no edits will be made to it
    )

    private val _categoryTypeRVPacket = MediatorLiveData<DetailCategoryRVPacket?>().apply {
        addSource(_transactions) { recalculateTypeRVPacket(it) }
        addSource(xeRepository.xeRows) { recalculateTypeRVPacket(_transactions.value) }
    }
    val categoryTypeRVPacket: LiveData<DetailCategoryRVPacket?>
        get() = _categoryTypeRVPacket

    private val numberOfDays = CalendarHandler.calculateNumberOfDays(calendarStart, calendarEnd)
    private val numberOfMonths = CalendarHandler.calculateNumberOfMonths(calendarStart, calendarEnd)

    private fun generateRangeString(): String = when (numberOfMonths) {
        1 -> CalendarHandler.getFormattedString(calendarStart.clone() as Calendar, "MMM yyyy")
            .toUpperCase(Locale.ROOT)
        12 -> CalendarHandler.getFormattedString(calendarStart.clone() as Calendar, "yyyy")
            .toUpperCase(Locale.ROOT)
        else -> throw IllegalStateException("Variable ranges not yet implemented")
    }

    private fun recalculateTypeRVPacket(list: List<Transaction>?) {
        if (list == null) return
        viewModelScope.launch(Dispatchers.Default) {
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
                    calendarStart = calendarStart,
                    numberOfDays = numberOfDays,
                    numberOfMonths = numberOfMonths,
                    colourInt = colourInt,
                    rangeAmount = rangeAmount,
                    showCurrency = showRangeCurrency,
                    homeCurrency = homeCurrency
                ),
                transactionsRangeString = generateRangeString(),
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