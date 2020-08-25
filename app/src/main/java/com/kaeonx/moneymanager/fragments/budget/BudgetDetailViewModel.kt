package com.kaeonx.moneymanager.fragments.budget

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.mikephil.charting.data.*
import com.kaeonx.moneymanager.chartcomponents.PieChartLegendLLData
import com.kaeonx.moneymanager.chartcomponents.PieChartWLPacket
import com.kaeonx.moneymanager.customclasses.MutableLiveData2
import com.kaeonx.moneymanager.handlers.CalendarHandler
import com.kaeonx.moneymanager.handlers.ColourHandler
import com.kaeonx.moneymanager.handlers.CurrencyHandler
import com.kaeonx.moneymanager.userrepository.UserPDS
import com.kaeonx.moneymanager.userrepository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.*

class BudgetDetailViewModel(category: String, initCalendar: Calendar) : ViewModel() {

    private val userRepository = UserRepository.getInstance()

    private val _displayCalendar = MutableLiveData2(initCalendar)
    val displayCalendar: LiveData<Calendar>
        get() = _displayCalendar

    internal fun selectMonth(newMonth: Int, newYear: Int) {
        _displayCalendar.value = _displayCalendar.value.apply {
            this.set(Calendar.MONTH, newMonth)
            this.set(Calendar.YEAR, newYear)
        }
    }

    internal val budget = userRepository.getBudget(category)
    private val _budgetDetailRVPacket = MediatorLiveData<BudgetDetailRVPacket?>().apply {
        addSource(_displayCalendar) { if (budget.value != null) recalculateBudgetDetailRVPacket() }
        addSource(budget) { if (it != null) recalculateBudgetDetailRVPacket() }
    }
    val budgetDetailRVPacket: LiveData<BudgetDetailRVPacket?>
        get() = _budgetDetailRVPacket

    private fun recalculateBudgetDetailRVPacket() {
        viewModelScope.launch(Dispatchers.Default) {
            val homeCurrency = UserPDS.getString("ccc_home_currency")

            val startMillis = _displayCalendar.value.timeInMillis
            val endMillis = CalendarHandler.getEndOfMonthMillis(
                _displayCalendar.value.clone() as Calendar
            )

            val budgetObj = budget.value!!
            val budgetAmount = BigDecimal(budgetObj.originalAmount)
            val spentAmountIBC: BigDecimal  // In Budget's Currency
            userRepository.getTransactionsBetweenSuspend(
                type = "Expenses",
                category = budgetObj.category,
                startMillis = startMillis,
                endMillis = endMillis
            ).run {
                var rangeAmount = BigDecimal.ZERO
                forEach {
                    rangeAmount = rangeAmount.plus(
                        if (it.originalCurrency == budgetObj.originalCurrency) {
                            BigDecimal(it.originalAmount)
                        } else {
                            CurrencyHandler.convertAmountViaSGDProxy(
                                BigDecimal(it.originalAmount),
                                foreignCurrencySrc = it.originalCurrency,
                                foreignCurrencyDst = budgetObj.originalCurrency
                            )
                        }
                    )
                }
                spentAmountIBC = rangeAmount
            }

            // Calculations for pieData and barData
            val dayDivDays = CalendarHandler.getDayDivDays(
                displayMonthStartCalendar = CalendarHandler.getStartOfMonthCalendar(
                    _displayCalendar.value
                ),
                scale = 4
            )

            val pieEntries: List<PieEntry>
            val pieChartLegendLLData: List<PieChartLegendLLData.BudgetDetailPCLLD>
            val barEntries: List<BarEntry>
            val colourList: List<Int>
            if (spentAmountIBC <= budgetAmount) {
                val exDivBud = spentAmountIBC.divide(budgetAmount, 2, RoundingMode.HALF_UP)
                if (exDivBud <= dayDivDays) {
                    val float1 = exDivBud.toFloat()
                    val float2 = dayDivDays.minus(exDivBud).toFloat()
                    val float3 = BigDecimal.ONE.minus(dayDivDays).toFloat()
                    colourList = listOf(
                        ColourHandler.getColourObjectThemedOf("Green"),
                        ColourHandler.getColourObjectThemedOf("Grey"),
                        ColourHandler.getColourObjectThemedOf("TRANSPARENT")
                    )
                    pieEntries = listOf(
                        PieEntry(float1, "ex"),
                        PieEntry(float2, "target ex"),
                        PieEntry(float3, "remainder")
                    )
                    pieChartLegendLLData = listOf(
                        PieChartLegendLLData.BudgetDetailPCLLD(
                            showFormsFromLeft = 2,
                            form0Colour = 0,
                            form1Colour = colourList[0],
                            form2Colour = 0,
                            name = "Expenses",
                            showCurrency = budgetObj.originalCurrency != homeCurrency,
                            currency = budgetObj.originalCurrency,
                            amount = CurrencyHandler.displayAmount(spentAmountIBC)
                        ),
                        PieChartLegendLLData.BudgetDetailPCLLD(
                            showFormsFromLeft = 2,
                            form0Colour = 0,
                            form1Colour = colourList[1],
                            form2Colour = 0,
                            name = "Left within Target",
                            showCurrency = budgetObj.originalCurrency != homeCurrency,
                            currency = budgetObj.originalCurrency,
                            amount = CurrencyHandler.displayAmount(
                                dayDivDays.times(budgetAmount).minus(spentAmountIBC)
                            )
                        ),
                        PieChartLegendLLData.BudgetDetailPCLLD(
                            showFormsFromLeft = 2,
                            form0Colour = colourList[1],
                            form1Colour = colourList[2],
                            form2Colour = 0,
                            name = "Left within Budget",
                            showCurrency = budgetObj.originalCurrency != homeCurrency,
                            currency = budgetObj.originalCurrency,
                            amount = CurrencyHandler.displayAmount(
                                budgetAmount.minus(spentAmountIBC)
                            )
                        )
                    )
                    barEntries = listOf(
                        BarEntry(
                            0f, floatArrayOf(
                                float1,  // ex
                                float2,  // target ex
                                float3  // remainder
                            )
                        )
                    )
                } else {
                    val float1 = dayDivDays.toFloat()
                    val float2 = exDivBud.minus(dayDivDays).toFloat()
                    val float3 = BigDecimal.ONE.minus(exDivBud).toFloat()
                    colourList = listOf(
                        ColourHandler.getColourObjectThemedOf("Green"),
                        ColourHandler.getColourObjectThemedOf("Amber"),
                        ColourHandler.getColourObjectThemedOf("TRANSPARENT")
                    )
                    pieEntries = listOf(
                        PieEntry(float1, "target ex"),
                        PieEntry(float2, "ex"),
                        PieEntry(float3, "remainder")
                    )
                    barEntries = listOf(
                        BarEntry(
                            0f, floatArrayOf(
                                float1,  // target ex
                                float2,  // ex
                                float3  // remainder
                            )
                        )
                    )
                    pieChartLegendLLData = listOf(
                        PieChartLegendLLData.BudgetDetailPCLLD(
                            showFormsFromLeft = 2,
                            form0Colour = colourList[0],
                            form1Colour = colourList[1],
                            form2Colour = 0,
                            name = "Expenses",
                            showCurrency = budgetObj.originalCurrency != homeCurrency,
                            currency = budgetObj.originalCurrency,
                            amount = CurrencyHandler.displayAmount(spentAmountIBC)
                        ),
                        PieChartLegendLLData.BudgetDetailPCLLD(
                            showFormsFromLeft = 2,
                            form0Colour = 0,
                            form1Colour = colourList[1],
                            form2Colour = 0,
                            name = "Exceeded Target",
                            showCurrency = budgetObj.originalCurrency != homeCurrency,
                            currency = budgetObj.originalCurrency,
                            amount = CurrencyHandler.displayAmount(
                                spentAmountIBC.minus(dayDivDays.times(budgetAmount))
                            )
                        ),
                        PieChartLegendLLData.BudgetDetailPCLLD(
                            showFormsFromLeft = 2,
                            form0Colour = 0,
                            form1Colour = colourList[2],
                            form2Colour = 0,
                            name = "Left within Budget",
                            showCurrency = budgetObj.originalCurrency != homeCurrency,
                            currency = budgetObj.originalCurrency,
                            amount = CurrencyHandler.displayAmount(
                                budgetAmount.minus(spentAmountIBC)
                            )
                        )
                    )
                }
            } else {
                val budDivEx = budgetAmount.divide(spentAmountIBC, 2, RoundingMode.HALF_UP)
                val dayDivDaysTimesBudDivEx = dayDivDays.times(budDivEx)
                val float1 = dayDivDaysTimesBudDivEx.toFloat()  // target ex
                val float2 = budDivEx.minus(dayDivDaysTimesBudDivEx).toFloat()  // bud
                val float3 = BigDecimal.ONE.minus(budDivEx).toFloat()  // over ex
                colourList = listOf(
                    ColourHandler.getColourObjectThemedOf("Green"),
                    ColourHandler.getColourObjectThemedOf("Amber"),
                    ColourHandler.getColourObjectThemedOf("Red")
                )
                pieEntries = listOf(
                    PieEntry(float1, "target ex"),
                    PieEntry(float2, "bud"),
                    PieEntry(float3, "over ex")
                )
                barEntries = listOf(
                    BarEntry(
                        0f, floatArrayOf(
                            float1,  // target ex
                            float2,  // bud
                            float3  // over ex
                        )
                    )
                )
                pieChartLegendLLData = listOf(
                    PieChartLegendLLData.BudgetDetailPCLLD(
                        showFormsFromLeft = 3,
                        form0Colour = colourList[0],
                        form1Colour = colourList[1],
                        form2Colour = colourList[2],
                        name = "Expenses",
                        showCurrency = budgetObj.originalCurrency != homeCurrency,
                        currency = budgetObj.originalCurrency,
                        amount = CurrencyHandler.displayAmount(spentAmountIBC)
                    ),
                    PieChartLegendLLData.BudgetDetailPCLLD(
                        showFormsFromLeft = 3,
                        form0Colour = 0,
                        form1Colour = colourList[1],
                        form2Colour = colourList[2],
                        name = "Exceeded Target",
                        showCurrency = budgetObj.originalCurrency != homeCurrency,
                        currency = budgetObj.originalCurrency,
                        amount = CurrencyHandler.displayAmount(
                            spentAmountIBC.minus(dayDivDays.times(budgetAmount))
                        )
                    ),
                    PieChartLegendLLData.BudgetDetailPCLLD(
                        showFormsFromLeft = 3,
                        form0Colour = 0,
                        form1Colour = 0,
                        form2Colour = colourList[2],
                        name = "Exceeded Budget",
                        showCurrency = budgetObj.originalCurrency != homeCurrency,
                        currency = budgetObj.originalCurrency,
                        amount = CurrencyHandler.displayAmount(
                            spentAmountIBC.minus(budgetAmount)
                        )
                    )
                )
            }
            val pieData = PieData(PieDataSet(pieEntries, null).apply {
                colors = colourList
                setDrawValues(false)
//                    sliceSpace = 1f  // in dp (as float)
            })
            val barData = BarData(
                BarDataSet(barEntries, null).apply {
                    colors = colourList
                    setDrawValues(false)
                }
            ).apply {
                barWidth = 1f
            }

            val budgetLLData = BudgetLLData(
                budget = budgetObj,
                showCurrency = budgetObj.originalCurrency != homeCurrency,
                spentAmount = CurrencyHandler.displayAmount(spentAmountIBC),
                spentPercent = CurrencyHandler.largePercentFormatter(
                    spentAmountIBC.times(BigDecimal("100")).divide(
                        budgetAmount,
                        1,
                        RoundingMode.HALF_EVEN
                    )
                ),
                barData = barData
            )
            val result = BudgetDetailRVPacket(
                pieChartWLPacket = PieChartWLPacket(
                    pieData = pieData,
                    pieChartCentreText = null,
                    pieChartLegendLLData = pieChartLegendLLData
                ),
                budgetText = CalendarHandler.getFormattedString(
                    _displayCalendar.value.clone() as Calendar,
                    "MMM yyyy"
                ).toUpperCase(Locale.ROOT),
                budgetLLData = budgetLLData
            )

            withContext(Dispatchers.Main) { _budgetDetailRVPacket.value = result }
        }
    }
}