package com.kaeonx.moneymanager.userrepository.domain

import java.util.*
import kotlin.collections.ArrayList

data class DayTransactions(
    val ymdCalendar: Calendar,
    var dayIncome: String? = null,  // This value is always in HomeCurrency
    var dayExpenses: String? = null,  // This value is always in HomeCurrency
    var incomeAllHome: Boolean = false,
    var expensesAllHome: Boolean = false,
    val transactions: ArrayList<Transaction> = ArrayList()) {
}
