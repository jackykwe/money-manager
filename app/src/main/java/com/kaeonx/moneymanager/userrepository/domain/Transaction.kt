package com.kaeonx.moneymanager.userrepository.domain

import android.os.Parcelable
import com.kaeonx.moneymanager.customclasses.convertFrom
import com.kaeonx.moneymanager.customclasses.toCalendar
import com.kaeonx.moneymanager.customclasses.toDisplayStringNullable
import com.kaeonx.moneymanager.userrepository.database.DatabaseTransaction
import kotlinx.android.parcel.Parcelize
import java.math.BigDecimal
import java.util.*
import kotlin.collections.ArrayList

@Parcelize
data class Transaction(
    val transactionId: Long? = null,
    var timestamp: Long = 0L,
    var type: String = "",
    var category: String = "",
    var account: String = "",
    var memo: String = "",
    var originalCurrency: String = "",
    var originalAmount: String = "") : Parcelable

fun Transaction.toDatabase(): DatabaseTransaction {
    return DatabaseTransaction(
        timestamp = this.timestamp,
        type = this.type,
        category = this.category,
        account = this.account,
        memo = this.memo,
        originalCurrency = this.originalCurrency,
        originalAmount = this.originalAmount
    )
}

fun List<Transaction>.typeAllHomeCurrency(type: String, homeCurrency: String): Boolean {
    return filter { it.type == type }
        .all { it.originalCurrency == homeCurrency }
}

fun List<Transaction>.calculateIncomeExpenses(homeCurrency: String): IncomeExpenses {
    var income = BigDecimal.ZERO
    var expenses = BigDecimal.ZERO
    forEach {
        when (it.type) {
            "Income" -> {
                income = if (it.originalCurrency == homeCurrency) {
                    income.plus(BigDecimal(it.originalAmount))
                } else {
                    income.plus(BigDecimal(it.originalAmount).convertFrom(it.originalCurrency, homeCurrency))
                }
            }
            "Expenses" -> {
                expenses = if (it.originalCurrency == homeCurrency) {
                    expenses.plus(BigDecimal(it.originalAmount))
                } else {
                    expenses.plus(BigDecimal(it.originalAmount).convertFrom(it.originalCurrency, homeCurrency))
                }
            }
            else -> throw IllegalArgumentException("Unknown type given: ${it.type}")
        }
    }
    return IncomeExpenses(
        income.toDisplayStringNullable(),
        expenses.toDisplayStringNullable()
    )
}

// This function assumes that all Transactions in the List are in the same month.
fun List<Transaction>.toDayTransactions(homeCurrency: String): List<DayTransactions> {
    val initCalendar = Calendar.getInstance()  // so that calculations below won't shift when done at 23:59:59
    if (this.isEmpty()) return listOf()
    val daysInMonth = this[0].timestamp.toCalendar().getActualMaximum(Calendar.DAY_OF_MONTH)

    // Generates 1 DayTransaction per day
    var result = ArrayList<DayTransactions>()
    for (d in 1..daysInMonth) {
        val c = initCalendar.clone() as Calendar
        c.set(Calendar.DAY_OF_MONTH, d)  // This calendar only needs to be accurate to the day. Hours/minutes/etc. don't matter.
        result.add(
            DayTransactions(
                dayOfMonth = d.toLong(),
                ymdCalendar = c
            )
        )
    }

    //// Filters transactions within firstMillis <= transaction.timestamp <= lastMillis - NOT NECESSARY - SQL will handle this
    // Puts transactions them into the relevant DayTransactions
    this.forEach {
        result[it.timestamp.toCalendar().get(Calendar.DAY_OF_MONTH) - 1].transactions.add(it)
    }

    // Removes DayTransactions without any transactions
    result = result.filter { it.transactions.isNotEmpty() } as ArrayList<DayTransactions>

    // Calculates currency, dayIncome and dayExpenses.
    result = result.map {
        // Check if all transactions within each day have the same currency, then calculate new income & expense
        it.incomeAllHome = this.typeAllHomeCurrency("Income", homeCurrency)
        it.expensesAllHome =  this.typeAllHomeCurrency("Expenses", homeCurrency)

        // Note: In all calculations, each individual transaction is converted to Home Currency first (if needed)
        val (income, expenses) = it.transactions.calculateIncomeExpenses(homeCurrency)
        it.dayIncome = income
        it.dayExpenses = expenses

        // Sort by descending date then return
        it.transactions.reverse()
        it
    } as ArrayList<DayTransactions>
    result.reverse()
    return result
}