package com.kaeonx.moneymanager.fragments.type

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
import java.math.MathContext
import java.math.RoundingMode
import java.util.*

private const val LEGEND_ITEM_MAX_COUNT = 6

class DetailTypeViewModel(
    private val initType: String,
    initCalendar: Calendar,
    private val showCurrency: Boolean
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

    private val _displayCalendar = MutableLiveData2(initCalendar)

    private val _transactions = userRepository.getTransactionsBetween(
        _displayCalendar.value.timeInMillis,  // no need clone, since no edits will be made to it
        CalendarHandler.getEndOfMonthMillis(_displayCalendar.value.clone() as Calendar)
    )
    private val _typeRVPacket = MediatorLiveData<DetailTypeRVPacket?>().apply {
//        addSource(_displayCalendar) { recalculateTypeRVPacket(_transactions.value) }  // TODO: CHANGE BETWEEN MONTH, YEAR, AND CUSTOM DATE RANGE
        addSource(_type) { recalculateTypeRVPacket(_transactions.value) }
        addSource(_transactions) { recalculateTypeRVPacket(it) }
        addSource(xeRepository.xeRows) { recalculateTypeRVPacket(_transactions.value) }
    }
    val detailTypeRVPacket: LiveData<DetailTypeRVPacket?>
        get() = _typeRVPacket

    private fun recalculateTypeRVPacket(list: List<Transaction>?) {
        if (list == null) return
        viewModelScope.launch(Dispatchers.Default) {
            val homeCurrency = UserPDS.getString("ccc_home_currency")

            val amountsMap = mutableMapOf<String, BigDecimal>()
            list.filter { it.type == _type.value }.run {
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
            val detailLLDataAL = arrayListOf<DetailTypeCategoryLLData>()

            val total = amountsMap.values.asIterable().sumByBigDecimal { it }
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
                        .divide(total, MathContext(3, RoundingMode.HALF_UP))
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
                            .divide(total, MathContext(3, RoundingMode.HALF_UP))
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
                    detailLLDataAL.add(
                        DetailTypeCategoryLLData(
                            iconDetail = categoryObject.toIconDetail(),
                            categoryName = entry.key,  // Needed for onClickListener to identify the typeCategory pressed
                            categoryPercent = "($percentDisplay%)",
                            showCurrency = showCurrency,
                            currency = homeCurrency,
                            categoryAmount = CurrencyHandler.displayAmount(entry.value),
                            barData = BarData(
                                BarDataSet(
                                    listOf(
                                        BarEntry(
                                            0f,
                                            entry.value.divide(
                                                highestEntry!!.value,
                                                MathContext(3, RoundingMode.HALF_EVEN)
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
                summaryLegendLLDatumDetails = legendLLDataAL.toList(),
                categoriesMonthString = CalendarHandler.getFormattedString(
                    _displayCalendar.value,
                    "MMM yyyy"
                ).toUpperCase(Locale.ROOT),
                categoriesShowMonthCurrency = showCurrency,
                categoriesMonthCurrency = homeCurrency,
                categoriesMonthAmount = CurrencyHandler.displayAmount(total),
                detailLLDatumDetails = detailLLDataAL.toList()
            )

            withContext(Dispatchers.Main) {
                _typeRVPacket.value = result
            }
        }
    }
}