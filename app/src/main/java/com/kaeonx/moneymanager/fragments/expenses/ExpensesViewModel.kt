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
    private val _expensesRVData = MediatorLiveData<ExpensesRVData?>().apply {
//        addSource(_displayCalendar) { updatePreviousLiveData() }  // added just for future compatibility
        addSource(_transactions) { recalculateExpensesRVData(it) }
        addSource(xeRepository.xeRows) { recalculateExpensesRVData(_transactions.value) }
    }
    val expensesRVData: LiveData<ExpensesRVData?>
        get() = _expensesRVData

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

            val expenseCategoryList = arrayListOf<ExpenseCategory>()
            amountsMap.forEach {
                expenseCategoryList.add(
                    ExpenseCategory(
                        categoryName = it.key,
                        showCurrency = showCurrency,
                        currency = homeCurrency,
                        categoryAmount = CurrencyHandler.displayAmount(it.value),
                        barData = null
                    )
                )
            }

            // Construction of PieData
            val total = amountsMap.values.asIterable().sumByBigDecimal { it }
//            val highestCategory = amountsMap.maxBy { it.value }
            val entries = arrayListOf<PieEntry>()
            val colourList = arrayListOf<Int>()
            val repositoryCategories = userRepository.categories.value!!
            amountsMap.asIterable().sortedByDescending { it.value }.forEach { entry ->
                val percent = entry.value.divide(total, MathContext(2, RoundingMode.HALF_UP))
                entries.add(PieEntry(percent.toFloat(), entry.key))
                colourList.add(
                    ColourHandler.getColourObject(
                        repositoryCategories
                            .find { it.name == entry.key && it.type == "Expenses" }?.colourString
                            ?: "Black"
                    )
                )
            }
            val dataSet = PieDataSet(entries, null).apply {
                colors = colourList
                setDrawValues(false)
//            sliceSpace = 1f  // in dp (as float)
            }

            val result = ExpensesRVData(
                pieData = PieData(dataSet),
                expensesCategoriesData = ExpensesCategoriesData(
                    monthString = CalendarHandler.getFormattedString(
                        _displayCalendar.value,
                        "MMM yyyy"
                    ),
                    categories = expenseCategoryList.toList()
                )
            )

            withContext(Dispatchers.Main) {
                _expensesRVData.value = result
            }
        }
    }
}