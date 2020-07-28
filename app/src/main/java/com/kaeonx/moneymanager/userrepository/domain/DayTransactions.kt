package com.kaeonx.moneymanager.userrepository.domain

import java.util.*

data class DayTransactions(
    val dayOfMonth: Long,  // serves as ID
    val ymdCalendar: Calendar,
    var dayIncome: String?,  // This value is always in HomeCurrency
    var dayExpenses: String?,  // This value is always in HomeCurrency
    var incomeAllHome: Boolean,
    var expensesAllHome: Boolean,
    val transactions: ArrayList<Transaction>)