package com.kaeonx.moneymanager.fragments.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.mikephil.charting.data.*
import com.kaeonx.moneymanager.chartcomponents.PieChartLegendLLData
import com.kaeonx.moneymanager.chartcomponents.PieChartWLPacket
import com.kaeonx.moneymanager.chartcomponents.generateLineChartPacket
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

private const val TAG = "dtvm"

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

    private var _transactions: LiveData<List<Transaction>>? = null
    private fun updateTransactions() {
        if (_transactions != null) {
            _detailTypeRVPacket.removeSource(_transactions!!)
        }
        _transactions = userRepository.getTransactionsBetween(
            _displayCalendarStart.value.timeInMillis,  // no need clone, since no edits will be made to it
            _displayCalendarEnd.value.timeInMillis  // no need clone, since no edits will be made to it
        )
        _detailTypeRVPacket.addSource(_transactions!!) { recalculateDetailTypeRVPacket(it) }
    }

    private val _detailTypeRVPacket = MediatorLiveData<DetailTypeRVPacket?>().apply {
        addSource(_displayCalendarEnd) { updateTransactions() }
        addSource(_type) { recalculateDetailTypeRVPacket(_transactions?.value) }
        addSource(xeRepository.xeRows) { recalculateDetailTypeRVPacket(_transactions?.value) }
    }
    val detailTypeRVPacket: LiveData<DetailTypeRVPacket?>
        get() = _detailTypeRVPacket

    private fun recalculateDetailTypeRVPacket(list: List<Transaction>?) {
        if (list == null) return
        viewModelScope.launch(Dispatchers.Default) {
            val homeCurrency = UserPDS.getString("ccc_home_currency")

            val showRangeCurrency: Boolean
            val categoryAmountsMap = mutableMapOf<String, BigDecimal>()
            val typeFilteredList = list.filter { it.type == _type.value }
            typeFilteredList.run {
                showRangeCurrency = any { it.originalCurrency != homeCurrency }
                map { it.category }.toSet().forEach {
                    categoryAmountsMap[it] = BigDecimal.ZERO
                }
                forEach {
                    it.homeAmount = if (it.originalCurrency == homeCurrency) {
                        BigDecimal(it.originalAmount)
                    } else {
                        CurrencyHandler.convertAmount(
                            BigDecimal(it.originalAmount),
                            it.originalCurrency,
                            homeCurrency
                        )
                    }
                    categoryAmountsMap[it.category] =
                        categoryAmountsMap[it.category]!!.plus(it.homeAmount)
                }
            }

            val legendLLDataAL = arrayListOf<PieChartLegendLLData.DetailCategoryPCLLD>()
            val categoryLLDataAL = arrayListOf<DetailTypeCategoryLLData>()

            val rangeAmount = categoryAmountsMap.values.asIterable().sumByBigDecimal { it }
            val highestCategory = categoryAmountsMap.maxBy { it.value }
            val repositoryCategories = userRepository.categories.value!!

            val entries = arrayListOf<PieEntry>()
            val colourList = arrayListOf<Int>()
            var valueAccumulator = BigDecimal.ZERO
            categoryAmountsMap.asIterable()
                .sortedBy { it.key }  // sort by ASCENDING name (secondary)
                .sortedByDescending { it.value }  // sort by DESCENDING amount (primary)
                .forEachIndexed { index, entry ->
                    val categoryObject = repositoryCategories
                        .find { it.name == entry.key && it.type == _type.value }
                        ?: Category(null, _type.value, entry.key, "F02D6", "Black")
                    val colourInt = ColourHandler.getColourObject(categoryObject.colourString)

                    val percent = entry.value.times(BigDecimal("100"))
                        .divide(rangeAmount, 3, RoundingMode.HALF_UP)
                    // should be between 0 and 100. No need LargePercentFormatter.
                    val percentDisplay = percent.setScale(1, RoundingMode.HALF_EVEN)

                    // For PieData & legendLLData
                    if (categoryAmountsMap.size <= LEGEND_ITEM_MAX_COUNT || index < LEGEND_ITEM_MAX_COUNT - 1) {
                        entries.add(PieEntry(percent.toFloat(), entry.key))
                        colourList.add(colourInt)
                        legendLLDataAL.add(
                            PieChartLegendLLData.DetailCategoryPCLLD(
                                noDataFlag = false,
                                colour = colourInt,
                                categoryName = entry.key,
                                categoryPercent = "($percentDisplay%)"
                            )
                        )
                    } else if (index == categoryAmountsMap.size - 1) {
                        valueAccumulator = valueAccumulator.plus(entry.value)
                        val accumulatorPercent = valueAccumulator.times(BigDecimal("100"))
                            .divide(rangeAmount, 3, RoundingMode.HALF_UP)
                        // should be between 0 and 100. No need LargePercentFormatter.
                        val accumulatorPercentDisplay =
                            accumulatorPercent.setScale(1, RoundingMode.HALF_EVEN)

                        val accumulatorColourInt = ColourHandler.getColourObject("Black")
                        entries.add(PieEntry(accumulatorPercent.toFloat(), entry.key))
                        colourList.add(accumulatorColourInt)
                        legendLLDataAL.add(
                            PieChartLegendLLData.DetailCategoryPCLLD(
                                noDataFlag = false,
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
                                                highestCategory!!.value,
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
            val dataSet = if (entries.isNotEmpty()) {
                PieDataSet(entries, null).apply {
                    colors = colourList
                    setDrawValues(false)
                    selectionShift = 0f  // removes padding
                    sliceSpace = 2f  // in dp (as float)
                }
            } else {
                PieDataSet(listOf(PieEntry(1f, "noData")), null).apply {
                    colors = listOf(ColourHandler.getColourObject("Grey,200"))
                    setDrawValues(false)
                    selectionShift = 0f  // removes padding
                    sliceSpace = 2f  // in dp (as float)
                }
            }

            if (legendLLDataAL.isEmpty()) {
                legendLLDataAL.add(
                    PieChartLegendLLData.DetailCategoryPCLLD(
                        noDataFlag = true,
                        colour = ColourHandler.getColourObject("Grey,200"),
                        categoryName = "Nothing to show",
                        categoryPercent = ""
                    )
                )
            }

            val result = DetailTypeRVPacket(
                pieChartWLPacket = PieChartWLPacket(
                    pieChartCentreText = "${_type.value}\n⇌",
                    pieData = PieData(dataSet),
                    pieChartLegendLLData = legendLLDataAL.toList()
                ),
                lineChartPacket = generateLineChartPacket(
                    list = typeFilteredList,
                    calendarStart = _displayCalendarStart.value,
                    numberOfDays = CalendarHandler.calculateNumberOfDays(
                        _displayCalendarStart.value,
                        _displayCalendarEnd.value
                    ),
                    numberOfMonths = CalendarHandler.calculateNumberOfMonths(
                        _displayCalendarStart.value,
                        _displayCalendarEnd.value
                    ),
                    colourInt = ColourHandler.getColourObject("Black"),
                    rangeAmount = rangeAmount,
                    showCurrency = showRangeCurrency,
                    homeCurrency = homeCurrency
                ),
                categoriesRangeString = if (isYearMode) {
                    CalendarHandler.getFormattedString(
                        _displayCalendarStart.value.clone() as Calendar,
                        "yyyy"
                    )
                } else {
                    CalendarHandler.getFormattedString(
                        _displayCalendarStart.value.clone() as Calendar,
                        "MMM yyyy"
                    ).toUpperCase(Locale.ROOT)
                },
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

    private var isYearMode = false
    private lateinit var archiveCalendarStart: Calendar
    internal fun toggleView() {
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
}