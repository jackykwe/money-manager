package com.kaeonx.moneymanager.userrepository.domain

import android.os.Parcelable
import com.kaeonx.moneymanager.handlers.CalendarHandler
import com.kaeonx.moneymanager.handlers.CurrencyHandler
import com.kaeonx.moneymanager.userrepository.UserPDS
import com.kaeonx.moneymanager.userrepository.UserRepository
import com.kaeonx.moneymanager.userrepository.database.DatabaseTransaction
import kotlinx.android.parcel.IgnoredOnParcel
import kotlinx.android.parcel.Parcelize
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.math.BigDecimal
import java.util.*
import kotlin.collections.ArrayList

@Parcelize
data class Transaction(
    val transactionId: Int?,
    var timestamp: Long,
    var type: String,
    var category: String,
    var account: String,
    var memo: String,
    var originalCurrency: String,
    var originalAmount: String) : Parcelable {

    @IgnoredOnParcel
    internal lateinit var homeAmount: BigDecimal

    fun toDatabase(): DatabaseTransaction {
        return DatabaseTransaction(
            transactionId = this.transactionId ?: 0,
            timestamp = this.timestamp,
            type = this.type,
            category = this.category,
            account = this.account,
            memo = this.memo,
            originalCurrency = this.originalCurrency,
            originalAmount = this.originalAmount
        )
    }

    fun toIconDetail(): IconDetail {
        val repository = UserRepository.getInstance()
        val categoryObj =
            repository.categories.value!!.find { it.name == this.category && it.type == this.type }
                ?: Category(null, type, category, "F02D6", "Black")
        val accountObj = repository.accounts.value!!.find { it.name == this.account }
            ?: Account(null, account, "TRANSPARENT")
        return IconDetail(categoryObj.iconHex, categoryObj.colourString, accountObj.colourString)
    }
}


// TODO: shift this to inside ViewModel.
// This function assumes that all Transactions in the List are in the same month.
internal suspend fun List<Transaction>.toDayTransactions(): List<DayTransactions> {
    // List<Transaction> is sorted in DESCENDING timestamp order.
    return withContext(Dispatchers.Default) {
        val initCalendar =
            Calendar.getInstance()  // so that calculations below won't shift when done at 23:59:59
        if (this@toDayTransactions.isEmpty()) return@withContext listOf<DayTransactions>()
        val daysInMonth =
            CalendarHandler.getCalendar(this@toDayTransactions[0].timestamp)
                .getActualMaximum(Calendar.DAY_OF_MONTH)

        // Generates 1 DayTransaction per day
        var result = ArrayList<DayTransactions>()
        for (d in 1..daysInMonth) {
            val c = initCalendar.clone() as Calendar
            c.set(
                Calendar.DAY_OF_MONTH,
                d
            )  // This calendar only needs to be accurate to the day. Hours/minutes/etc. don't matter.
            result.add(
                DayTransactions(
                    dayOfMonth = d,
                    ymdCalendar = c,
                    dayIncome = null,
                    dayExpenses = null,
                    incomeAllHome = false,
                    expensesAllHome = false,
                    transactions = ArrayList()
                )
            )
        }

        //// Filters transactions within firstMillis <= transaction.timestamp <= lastMillis - NOT NECESSARY - SQL will handle this
        // Puts transactions them into the relevant DayTransactions
        this@toDayTransactions.forEach {
            result[CalendarHandler.getCalendar(it.timestamp)
                .get(Calendar.DAY_OF_MONTH) - 1].transactions.add(it)
        }

        // Removes DayTransactions without any transactions
        result = result.filter { it.transactions.isNotEmpty() } as ArrayList<DayTransactions>

        // Calculates currency, dayIncome and dayExpenses.
        result = result.map {
            // Check if all transactions within each day have the same currency, then calculate new income & expense
            it.incomeAllHome = it.transactions.typeAllHomeCurrency("Income")
            it.expensesAllHome = it.transactions.typeAllHomeCurrency("Expenses")

            // Note: In all calculations, each individual transaction is converted to Home Currency first (if needed)
            val (income, expenses) = it.transactions.calculateIncomeExpenses()
            it.dayIncome = income
            it.dayExpenses = expenses

            it
        } as ArrayList<DayTransactions>
        result.reverse()
        result
    }
}

// Helper function for List<Transaction>.toDayTransactions()
private fun List<Transaction>.typeAllHomeCurrency(type: String): Boolean {
    return filter { it.type == type }
        .all { it.originalCurrency == UserPDS.getString("ccc_home_currency") }
}

// Helper function for List<Transaction>.toDayTransactions()
private fun List<Transaction>.calculateIncomeExpenses(): IncomeExpenses {
    var gotIncome = false
    var gotExpenses = false
    var income = BigDecimal.ZERO
    var expenses = BigDecimal.ZERO
    forEach {
        when (it.type) {
            "Income" -> {
                gotIncome = true
                income = if (it.originalCurrency == UserPDS.getString("ccc_home_currency")) {
                    income.plus(BigDecimal(it.originalAmount))
                } else {
                    val value = CurrencyHandler.convertAmount(
                        BigDecimal(it.originalAmount),
                        it.originalCurrency,
                        UserPDS.getString("ccc_home_currency")
                    )
                    income.plus(value)
                }
            }
            "Expenses" -> {
                gotExpenses = true
                expenses = if (it.originalCurrency == UserPDS.getString("ccc_home_currency")) {
                    expenses.plus(BigDecimal(it.originalAmount))
                } else {
                    val value = CurrencyHandler.convertAmount(
                        BigDecimal(it.originalAmount),
                        it.originalCurrency,
                        UserPDS.getString("ccc_home_currency")
                    )
                    expenses.plus(value)
                }
            }
            else -> throw IllegalArgumentException("Unknown type given: ${it.type}")
        }
    }
    val finalIncome = if (gotIncome) CurrencyHandler.displayAmount(income) else null
    val finalExpenses = if (gotExpenses) CurrencyHandler.displayAmount(expenses) else null
    return IncomeExpenses(finalIncome, finalExpenses)
}