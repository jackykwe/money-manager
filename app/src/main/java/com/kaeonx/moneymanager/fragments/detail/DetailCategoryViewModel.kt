package com.kaeonx.moneymanager.fragments.detail

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.*
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

    private fun generateRangeString(): String {
        val start = CalendarHandler.getFormattedString(calendarStart, "MMM yyyy")
            .toUpperCase(Locale.ROOT)
        val end = CalendarHandler.getFormattedString(calendarEnd, "MMM yyyy")
            .toUpperCase(Locale.ROOT)
        return "$start - $end"
    }

    private val numberOfDays =
        BigDecimal(calendarEnd.timeInMillis - calendarStart.timeInMillis)
            .divide(BigDecimal("86400000"), 0, RoundingMode.UP)
            .toInt()

    private fun calculateDayNumber(millis: Long): Int =
        BigDecimal(millis - calendarStart.timeInMillis)
            .divide(BigDecimal("86400000"), 4, RoundingMode.HALF_UP)
            .setScale(0, RoundingMode.FLOOR)
            .toInt()
            .plus(1)  // Floor and plus(1) to account for 00:00 transactions.

    private fun recalculateTypeRVPacket(list: List<Transaction>?) {
        if (list == null) return
        viewModelScope.launch(Dispatchers.Default) {
            val homeCurrency = UserPDS.getString("ccc_home_currency")
            val colourInt = ColourHandler.getColourObject(
                userRepository.categories.value!!
                    .find { it.name == category && it.type == type }!!
                    .colourString
            )

            // CALCULATION OF EVERYTHING ELSE (START)
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
            list.sortedByDescending { it.homeAmount } // So that highest valued transaction stays on top
                .forEach {
                    // Calculates everything else
                    val percent = it.homeAmount.times(BigDecimal("100"))
                        .divide(rangeAmount, 3, RoundingMode.HALF_UP)
                    val percentDisplay = percent.setScale(1, RoundingMode.HALF_EVEN)

                    transactionLLDataAL.add(
                        DetailCategoryTransactionLLData(
                            transaction = it,
                            transactionPercent = CurrencyHandler.displayAmount(percentDisplay),
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
            // CALCULATION OF EVERYTHING ELSE (END)

            // CALCULATION OF LINE DATA (START)
            val dayAmountMap = mutableMapOf<Float, BigDecimal>()
            for (i in 1..numberOfDays) {
                dayAmountMap[i.toFloat()] = BigDecimal.ZERO
            }
            list.forEach {
                val dayNumber = calculateDayNumber(it.timestamp).toFloat()
                dayAmountMap.replace(dayNumber, dayAmountMap[dayNumber]!!.plus(it.homeAmount))
            }
            val dataSet =
                LineDataSet(
                    dayAmountMap.map { Entry(it.key, it.value.toFloat()) },
                    null
                ).apply {
                    setDrawCircles(false)
                    color = colourInt
                    lineWidth = 1f
                    mode = LineDataSet.Mode.HORIZONTAL_BEZIER
                    cubicIntensity = 1f
                    axisDependency = YAxis.AxisDependency.LEFT
                }
            // CALCULATION OF LINE DATA (END)

            val result = DetailCategoryRVPacket(
                summaryCategory = category,
                summaryLineData = LineData(dataSet).apply {
                    setDrawValues(false)
                },
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