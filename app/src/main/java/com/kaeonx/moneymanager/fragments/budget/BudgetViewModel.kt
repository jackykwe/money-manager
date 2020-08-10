package com.kaeonx.moneymanager.fragments.budget

import android.text.SpannedString
import androidx.core.text.bold
import androidx.core.text.buildSpannedString
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
import com.kaeonx.moneymanager.userrepository.domain.Budget
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.*
import kotlin.collections.ArrayList

private const val TAG = "budgetvm"

class BudgetViewModel : ViewModel() {

    private val userRepository = UserRepository.getInstance()

    internal var addOptions: Array<SpannedString>? = null
        private set

    private val _budgets = userRepository.getAllBudgets()
    private val _budgetLLData = MediatorLiveData<List<BudgetLLData>?>().apply {
        addSource(_budgets) { if (it != null) recalculateBudgetLLData() }
    }
    val budgetLLData: LiveData<List<BudgetLLData>?>
        get() = _budgetLLData

    private fun recalculateBudgetLLData() {
        viewModelScope.launch(Dispatchers.Default) {
            val updateAddOptionsJob = launch(Dispatchers.Default) {
                val categoriesWithBudget = _budgets.value!!.map { SpannedString(it.category) }
                addOptions = if (categoriesWithBudget.contains(SpannedString("Overall"))) {
                    ArrayList(userRepository.categories.value!!
                        .filter { it.type == "Expenses" }
                        .map { SpannedString(it.name) })
                        .apply {
                            removeAll(categoriesWithBudget)
                        }.toTypedArray()
                } else {
                    arrayListOf(buildSpannedString { bold { append("Overall") } })
                        .apply {
                            addAll(userRepository.categories.value!!
                                .filter { it.type == "Expenses" }
                                .map { SpannedString(it.name) }
                            )
                            removeAll(categoriesWithBudget)
                        }.toTypedArray()
                }
            }

            val homeCurrency = UserPDS.getString("ccc_home_currency")

            val displayMonth = Calendar.getInstance()  //TODO: ANY MONTH
            val startMillis =
                CalendarHandler.getStartOfMonthMillis(displayMonth.clone() as Calendar)
            val endMillis = CalendarHandler.getEndOfMonthMillis(displayMonth.clone() as Calendar)

            val result = _budgets.value!!.map { budget ->
                val spentAmountIBC: BigDecimal  // In Budget's Currency
                userRepository.getTransactionsBetweenSuspend(
                    type = "Expenses",
                    category = budget.category,
                    startMillis = startMillis,
                    endMillis = endMillis
                )
                    .run {
                        var rangeAmount = BigDecimal.ZERO
                        forEach {
                            rangeAmount = rangeAmount.plus(
                                if (it.originalCurrency == budget.originalCurrency) {
                                    BigDecimal(it.originalAmount)
                                } else {
                                    CurrencyHandler.convertAmountViaProxy(
                                        BigDecimal(it.originalAmount),
                                        foreignCurrencySrc = it.originalCurrency,
                                        homeCurrencyPxy = homeCurrency,
                                        foreignCurrencyDst = budget.originalCurrency
                                    )
                                }
                            )
                        }
                        spentAmountIBC = rangeAmount
                    }

                val budgetAmount = BigDecimal(budget.originalAmount)

                // Calculations for barData
                val dayDivDays = CalendarHandler.getDayDivDays(
                    CalendarHandler.getStartOfMonthCalendar(displayMonth.clone() as Calendar)
                )
                val entries: List<BarEntry>
                val colourList: List<Int>
                if (spentAmountIBC < budgetAmount) {
                    val exDivBud = spentAmountIBC.divide(budgetAmount, 2, RoundingMode.HALF_UP)
                    if (exDivBud <= dayDivDays) {
                        entries = listOf(
                            BarEntry(
                                0f, floatArrayOf(
                                    exDivBud.toFloat(),  // ex
                                    dayDivDays.minus(exDivBud).toFloat(),  // target ex
                                    BigDecimal.ONE.minus(dayDivDays).toFloat()  // remainder
                                )
                            )
                        )
                        colourList = listOf(
                            ColourHandler.getColourObject("Green,500"),
                            ColourHandler.getColourObject("Grey,200"),
                            ColourHandler.getColourObject("White")
                        )
                    } else {
                        entries = listOf(
                            BarEntry(
                                0f, floatArrayOf(
                                    dayDivDays.toFloat(),  // target ex
                                    exDivBud.minus(dayDivDays).toFloat(),  // ex
                                    BigDecimal.ONE.minus(exDivBud).toFloat()  // remainder
                                )
                            )
                        )
                        colourList = listOf(
                            ColourHandler.getColourObject("Green,500"),
                            ColourHandler.getColourObject("Amber,500"),
                            ColourHandler.getColourObject("White")
                        )
                    }
                } else {
                    val budDivEx = budgetAmount.divide(spentAmountIBC, 2, RoundingMode.HALF_UP)
                    val dayDivDaysTimesBudDivEx = dayDivDays.times(budDivEx)
                    entries = listOf(
                        BarEntry(
                            0f, floatArrayOf(
                                dayDivDaysTimesBudDivEx.toFloat(),  // target ex
                                budDivEx.minus(dayDivDaysTimesBudDivEx).toFloat(),  // bud
                                BigDecimal.ONE.minus(budDivEx).toFloat()  // over ex
                            )
                        )
                    )
                    colourList = listOf(
                        ColourHandler.getColourObject("Green,500"),
                        ColourHandler.getColourObject("Amber,500"),
                        ColourHandler.getColourObject("Red,500")
                    )
                }
                val barData = BarData(
                    BarDataSet(entries, null).apply {
                        colors = colourList
                        setDrawValues(false)
                    }
                ).apply {
                    barWidth = 1f
                }

                BudgetLLData(
                    budget = budget,
                    showCurrency = budget.originalCurrency != homeCurrency,
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
            }

            updateAddOptionsJob.join()
            withContext(Dispatchers.Main) { _budgetLLData.value = result }
        }
    }
}

class BudgetOnClickListener(val clickListener: (category: String) -> Unit) {
    fun onClick(category: String) = clickListener(category)
}

data class BudgetLLData(
    val budget: Budget,
    val showCurrency: Boolean,
    val spentAmount: String,
    val spentPercent: String,
    val barData: BarData
)