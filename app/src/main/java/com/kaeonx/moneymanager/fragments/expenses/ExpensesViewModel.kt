package com.kaeonx.moneymanager.fragments.expenses

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
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

class ExpensesViewModel(
    private val initCalendar: Calendar,
    private val showCurrency: Boolean
) : ViewModel() {

    private val userRepository = UserRepository.getInstance()
    private val xeRepository = XERepository.getInstance()

    private val _displayCalendar = MutableLiveData2(initCalendar)

    private val _transactions = userRepository.getTransactionsBetween(
        _displayCalendar.value.timeInMillis,  // no need clone, since no edits will be made to it
        CalendarHandler.getEndOfMonthMillis(_displayCalendar.value.clone() as Calendar)
    )
    private val _expensesRVData = MediatorLiveData<ExpensesRVPackage?>().apply {
//        addSource(_displayCalendar) { updatePreviousLiveData() }  // added just for future compatibility
        addSource(_transactions) { recalculateExpensesRVData(it) }
        addSource(xeRepository.xeRows) { recalculateExpensesRVData(_transactions.value) }
    }
    val expensesRVPackage: LiveData<ExpensesRVPackage?>
        get() = _expensesRVData

    private fun getLegendName(name: String): String {
        return if (name.length > 10) "${name.substring(1..10)}..." else name
    }

    private fun recalculateExpensesRVData(list: List<Transaction>?) {
        if (list == null) return
        viewModelScope.launch(Dispatchers.Default) {
            val homeCurrency = UserPDS.getString("ccc_home_currency")

            val amountsMap = mutableMapOf<String, BigDecimal>()
            list.filter { it.type == "Expenses" }.run {
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

            val expensesLLDataList = arrayListOf<ExpenseLLData>()

            val total = amountsMap.values.asIterable().sumByBigDecimal { it }
//            val highestCategory = amountsMap.maxBy { it.value }
            val entries = arrayListOf<PieEntry>()
            val colourList = arrayListOf<Int>()
            val repositoryCategories = userRepository.categories.value!!
            amountsMap.asIterable()
                .sortedBy { it.key }  // sort by ASCENDING name (secondary)
                .sortedByDescending { it.value }  // sort by DESCENDING amount (primary)
                .forEach { entry ->
                    val percent = entry.value.times(BigDecimal("100"))
                        .divide(total, MathContext(3, RoundingMode.HALF_UP))
                    val truncatedName = getLegendName(entry.key)
                    val percentDisplay = percent.setScale(1, RoundingMode.HALF_EVEN)

                    // For PieData
                    entries.add(PieEntry(percent.toFloat(), "$truncatedName ($percentDisplay%)"))

                    val categoryObject =
                        repositoryCategories.find { it.name == entry.key && it.type == "Expenses" }
                            ?: Category(null, "Expenses", entry.key, "F02D6", "Black")
                    colourList.add(
                        ColourHandler.getColourObject(categoryObject.colourString)
                    )

                    // For expensesLLData
                    expensesLLDataList.add(
                        ExpenseLLData(
                            iconDetail = categoryObject.toIconDetail(),
                            categoryName = entry.key,  // Needed for onClickListener to identify the expensesCategory
                            categoryNamePercent = "${entry.key} ($percentDisplay%)",
                            showCurrency = showCurrency,
                            currency = homeCurrency,
                            categoryAmount = CurrencyHandler.displayAmount(entry.value),
                            barData = null
                        )
                    )
                }
            val dataSet = PieDataSet(entries, null).apply {
                colors = colourList
                setDrawValues(false)
                sliceSpace = 2f  // in dp (as float)
            }

            val result = ExpensesRVPackage(
                pieData = if (entries.isEmpty()) null else PieData(dataSet),
                expensesLLPackage = ExpensesLLPackage(
                    monthString = CalendarHandler.getFormattedString(
                        _displayCalendar.value,
                        "MMM yyyy"
                    ).toUpperCase(),
                    showCurrency = showCurrency,
                    currency = homeCurrency,
                    monthAmount = CurrencyHandler.displayAmount(total),
                    LLData = expensesLLDataList.toList()
                )
            )

            withContext(Dispatchers.Main) {
                _expensesRVData.value = result
            }
        }
    }
}