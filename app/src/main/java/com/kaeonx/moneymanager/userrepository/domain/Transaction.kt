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
    var originalAmount: String
) : Parcelable {

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

internal suspend fun List<Transaction>.toDayTransactions(): List<DayTransactions> {
    // List<Transaction> is sorted in DESCENDING timestamp order.
    if (isEmpty()) return listOf()
    fun getYMDIdentifier(timestamp: Long): Int {
        val cal = Calendar.getInstance()
        cal.timeInMillis = timestamp
        return cal.get(Calendar.YEAR) * 10000 + cal.get(Calendar.MONTH) * 100 + cal.get(Calendar.DAY_OF_MONTH)
    }
    return withContext(Dispatchers.Default) {
        val grouping = mutableMapOf<Int, DayTransactions>()
        forEach {
            val identifier = getYMDIdentifier(it.timestamp)
            grouping[identifier] = (grouping[identifier] ?: DayTransactions(
                ymdIdentifier = identifier,
                ymdCalendar = CalendarHandler.getCalendar(it.timestamp),  // This calendar only needs to be accurate to the day. Hours/minutes/etc. don't matter.
                dayIncome = null,
                dayExpenses = null,
                incomeAllHome = false,
                expensesAllHome = false,
                transactions = ArrayList()
            )).apply {
                transactions.add(it)
            }
        }

        // Calculates currency, dayIncome and dayExpenses.
        return@withContext grouping.values.map {
            it.apply {
                // Check if all transactions within each day have the same currency, then calculate new income & expense
                incomeAllHome = transactions.typeAllHomeCurrency("Income")
                expensesAllHome = transactions.typeAllHomeCurrency("Expenses")

                // Note: In all calculations, each individual transaction is converted to Home Currency first (if needed)
                val (income, expenses) = transactions.calculateIncomeExpenses()
                dayIncome = income
                dayExpenses = expenses
            }
        }
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