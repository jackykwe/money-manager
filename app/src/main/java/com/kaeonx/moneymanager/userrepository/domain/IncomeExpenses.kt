package com.kaeonx.moneymanager.userrepository.domain

class IncomeExpenses(private val income: String?, private val expenses: String?) {
    operator fun component1(): String? {
        return income
    }

    operator fun component2(): String? {
        return expenses
    }
}