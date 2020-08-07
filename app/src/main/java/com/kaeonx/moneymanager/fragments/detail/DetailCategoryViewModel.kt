package com.kaeonx.moneymanager.fragments.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
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
import java.math.MathContext
import java.math.RoundingMode
import java.util.*

class DetailCategoryViewModel(
    private val type: String,
    private val category: String,
    private val calendarStart: Calendar,
    private val calendarEnd: Calendar
) : ViewModel() {

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

    private fun recalculateTypeRVPacket(list: List<Transaction>?) {
        if (list == null) return
        viewModelScope.launch(Dispatchers.Default) {
            val homeCurrency = UserPDS.getString("ccc_home_currency")

            val showRangeCurrency = list.any { it.originalCurrency != homeCurrency }
            var rangeAmount = BigDecimal.ZERO
            var highestAmount = BigDecimal.ZERO
            list.forEach {
                // Calculates rangeAmount and highestAmount
                val homeAmount = if (it.originalCurrency == homeCurrency) {
                    BigDecimal(it.originalAmount)
                } else {
                    CurrencyHandler.convertAmount(
                        BigDecimal(it.originalAmount),
                        it.originalCurrency,
                        homeCurrency
                    )
                }
                rangeAmount = rangeAmount.plus(homeAmount)
                if (homeAmount > highestAmount) highestAmount =
                    homeAmount  // remember that _transactions is sorted by DESC timestamp
            }

            val transactionLLDataAL = arrayListOf<DetailCategoryTransactionLLData>()
            list.sortedByDescending {  // So that highest valued transaction stays on top
                if (it.originalCurrency == homeCurrency) {
                    BigDecimal(it.originalAmount)
                } else {
                    CurrencyHandler.convertAmount(
                        BigDecimal(it.originalAmount),
                        it.originalCurrency,
                        homeCurrency
                    )
                }
            }.forEach {
                // Calculates everything else
                val homeAmount = if (it.originalCurrency == homeCurrency) {
                    BigDecimal(it.originalAmount)
                } else {
                    CurrencyHandler.convertAmount(
                        BigDecimal(it.originalAmount),
                        it.originalCurrency,
                        homeCurrency
                    )
                }
                val percent = homeAmount.times(BigDecimal("100"))
                    .divide(rangeAmount, MathContext(3, RoundingMode.HALF_UP))
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
                                        homeAmount.divide(
                                            highestAmount,
                                            MathContext(3, RoundingMode.HALF_EVEN)
                                        ).toFloat()
                                    )
                                ),
                                null
                            ).apply {
                                color = ColourHandler.getColourObject(
                                    it.toIconDetail().iconBGColourString
                                )
                                setDrawValues(false)
                            }
                        ).apply {
                            barWidth = 1f
                        }
                    )
                )
            }

            val result = DetailCategoryRVPacket(
                summaryCategory = category,
                summaryLineData = null,
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