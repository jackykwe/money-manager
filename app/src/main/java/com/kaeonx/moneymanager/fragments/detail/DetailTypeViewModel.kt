package com.kaeonx.moneymanager.fragments.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.mikephil.charting.data.*
import com.kaeonx.moneymanager.customclasses.MutableLiveData2
import com.kaeonx.moneymanager.customclasses.sumByBigDecimal
import com.kaeonx.moneymanager.handlers.CalendarHandler
import com.kaeonx.moneymanager.handlers.ColourHandler
import com.kaeonx.moneymanager.handlers.CurrencyHandler
import com.kaeonx.moneymanager.userrepository.UserPDS
import com.kaeonx.moneymanager.userrepository.UserRepository
import com.kaeonx.moneymanager.userrepository.domain.Category
import com.kaeonx.moneymanager.userrepository.domain.Transaction
import com.kaeonx.moneymanager.xerepository.XERepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.*

private const val LEGEND_ITEM_MAX_COUNT = 6

class DetailTypeViewModel(
    initType: String,
    initCalendar: Calendar
) : ViewModel() {

    private val userRepository = UserRepository.getInstance()
    private val xeRepository = XERepository.getInstance()

    private val _type = MutableLiveData2(initType)  // current Type
    val type: LiveData<String>
        get() = _type

    internal fun swapType() {
        _type.value = when (_type.value) {
            "Income" -> "Expenses"
            "Expenses" -> "Income"
            else -> throw IllegalStateException("Unknown type ${_type.value}")
        }
    }

    private val _displayCalendarStart = MutableLiveData2(initCalendar)
    val displayCalendarStart: LiveData<Calendar>
        get() = _displayCalendarStart
    private val _displayCalendarEnd =
        MutableLiveData2(CalendarHandler.getEndOfMonthCalendar(initCalendar.clone() as Calendar))
    val displayCalendarEnd: LiveData<Calendar>
        get() = _displayCalendarEnd

    private val _transactions = userRepository.getTransactionsBetween(
        _displayCalendarStart.value.timeInMillis,  // no need clone, since no edits will be made to it
        _displayCalendarEnd.value.timeInMillis
    )
    private val _detailTypeRVPacket = MediatorLiveData<DetailTypeRVPacket?>().apply {
//        addSource(_displayCalendarStart) { recalculateTypeRVPacket(_transactions.value) }  // TODO: CHANGE BETWEEN MONTH, YEAR, AND CUSTOM DATE RANGE
        addSource(_type) { recalculateDetailTypeRVPacket(_transactions.value) }
        addSource(_transactions) { recalculateDetailTypeRVPacket(it) }
        addSource(xeRepository.xeRows) { recalculateDetailTypeRVPacket(_transactions.value) }
    }
    val detailTypeRVPacket: LiveData<DetailTypeRVPacket?>
        get() = _detailTypeRVPacket

    private fun recalculateDetailTypeRVPacket(list: List<Transaction>?) {
        if (list == null) return
        viewModelScope.launch(Dispatchers.Default) {
            val homeCurrency = UserPDS.getString("ccc_home_currency")

            val showRangeCurrency: Boolean
            val amountsMap = mutableMapOf<String, BigDecimal>()
            list.filter { it.type == _type.value }.run {
                showRangeCurrency = any { it.originalCurrency != homeCurrency }
                map { it.category }.toSet().forEach {
                    amountsMap[it] = BigDecimal.ZERO
                }
                forEach {
                    if (it.originalCurrency == homeCurrency) {
                        amountsMap[it.category] =
                            amountsMap[it.category]!!.plus(BigDecimal(it.originalAmount))
                    } else {
                        val value = CurrencyHandler.convertAmount(
                            BigDecimal(it.originalAmount),
                            it.originalCurrency,
                            homeCurrency
                        )
                        amountsMap[it.category] = amountsMap[it.category]!!.plus(value)
                    }
                }
            }

            val legendLLDataAL = arrayListOf<DetailTypeLegendLLData>()
            val categoryLLDataAL = arrayListOf<DetailTypeCategoryLLData>()

            val rangeAmount = amountsMap.values.asIterable().sumByBigDecimal { it }
            val highestEntry = amountsMap.maxBy { it.value }
            val repositoryCategories = userRepository.categories.value!!

            val entries = arrayListOf<PieEntry>()
            val colourList = arrayListOf<Int>()
            var valueAccumulator = BigDecimal.ZERO
            amountsMap.asIterable()
                .sortedBy { it.key }  // sort by ASCENDING name (secondary)
                .sortedByDescending { it.value }  // sort by DESCENDING amount (primary)
                .forEachIndexed { index, entry ->
                    val categoryObject = repositoryCategories
                        .find { it.name == entry.key && it.type == _type.value }
                        ?: Category(null, _type.value, entry.key, "F02D6", "Black")
                    val colourInt = ColourHandler.getColourObject(categoryObject.colourString)

                    val percent = entry.value.times(BigDecimal("100"))
                        .divide(rangeAmount, 3, RoundingMode.HALF_UP)
                    val percentDisplay = percent.setScale(1, RoundingMode.HALF_EVEN)

                    // For PieData & legendLLData
                    if (amountsMap.size <= LEGEND_ITEM_MAX_COUNT || index < LEGEND_ITEM_MAX_COUNT - 1) {
                        entries.add(PieEntry(percent.toFloat(), entry.key))
                        colourList.add(colourInt)
                        legendLLDataAL.add(
                            DetailTypeLegendLLData(
                                colour = colourInt,
                                categoryName = entry.key,
                                categoryPercent = "($percentDisplay%)"
                            )
                        )
                    } else if (index == amountsMap.size - 1) {
                        valueAccumulator = valueAccumulator.plus(entry.value)
                        val accumulatorPercent = valueAccumulator.times(BigDecimal("100"))
                            .divide(rangeAmount, 3, RoundingMode.HALF_UP)
                        val accumulatorPercentDisplay = percent.setScale(1, RoundingMode.HALF_EVEN)

                        val accumulatorColourInt = ColourHandler.getColourObject("Black")
                        entries.add(PieEntry(accumulatorPercent.toFloat(), entry.key))
                        colourList.add(accumulatorColourInt)
                        legendLLDataAL.add(
                            DetailTypeLegendLLData(
                                colour = accumulatorColourInt,  // todo: sensitive to theme (white or sth for dark theme)
                                categoryName = "(multiple)",
                                categoryPercent = "($accumulatorPercentDisplay%)"
                            )
                        )
                    } else {
                        valueAccumulator = valueAccumulator.plus(entry.value)
                    }

                    // For detailLLData
                    categoryLLDataAL.add(
                        DetailTypeCategoryLLData(
                            iconDetail = categoryObject.toIconDetail(),
                            type = _type.value,
                            categoryName = entry.key,  // Needed for onClickListener to identify the typeCategory pressed
                            categoryPercent = "($percentDisplay%)",
                            showCurrency = showRangeCurrency,
                            currency = homeCurrency,
                            categoryAmount = CurrencyHandler.displayAmount(entry.value),
                            barData = BarData(
                                BarDataSet(
                                    listOf(
                                        BarEntry(
                                            0f,
                                            entry.value.divide(
                                                highestEntry!!.value,
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
            val dataSet = PieDataSet(entries, null).apply {
                colors = colourList
                setDrawValues(false)
                selectionShift = 0f  // removes padding
                sliceSpace = 2f  // in dp (as float)
            }

            val result = DetailTypeRVPacket(
                summaryType = _type.value,
                summaryPieData = if (entries.isEmpty()) null else PieData(dataSet),
                summaryLegendLLData = legendLLDataAL.toList(),
                categoriesRangeString = CalendarHandler.getFormattedString(  // TODO: Refactor Month into Range
                    _displayCalendarStart.value,
                    "MMM yyyy"
                ).toUpperCase(Locale.ROOT),
                categoriesShowRangeCurrency = showRangeCurrency,
                categoriesRangeCurrency = homeCurrency,
                categoriesRangeAmount = CurrencyHandler.displayAmount(rangeAmount),
                categoryLLData = categoryLLDataAL.toList()
            )

            withContext(Dispatchers.Main) {
                _detailTypeRVPacket.value = result
            }
        }
    }
}