package com.kaeonx.moneymanager.userrepository.domain

import java.util.*

data class DayTransactions(
    val ymdIdentifier: Int,  // serves as ID
    val displayCalendarString: String,
    var dayIncome: String?,  // This value is always in HomeCurrency
    var dayExpenses: String?,  // This value is always in HomeCurrency
    var incomeAllHome: Boolean,
    var expensesAllHome: Boolean,
    val transactions: ArrayList<Transaction>
)