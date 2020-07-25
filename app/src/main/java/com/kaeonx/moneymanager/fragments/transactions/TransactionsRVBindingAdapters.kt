package com.kaeonx.moneymanager.fragments.transactions

import android.view.View
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.kaeonx.moneymanager.customclasses.toFormattedString
import com.kaeonx.moneymanager.userrepository.domain.DayTransactions
import com.kaeonx.moneymanager.userrepository.domain.Transaction

////////////////////////////////////////////////////////////////////////////////
/**
 * For rv_item_transactions_day
 */
////////////////////////////////////////////////////////////////////////////////
@BindingAdapter("dayDateTV_text")
fun TextView.setDayDateTVText(dayTransactions: DayTransactions) {
    text = dayTransactions.ymdCalendar.toFormattedString("EEE ddMMyy")
}

@BindingAdapter("incomeCurrencyVisibility", "hideCurrencyIfHome")
fun TextView.setIncomeCurrencyVisibility(dayTransactions: DayTransactions, hideCurrencyIfHome: Boolean) {
    // TODO: SEE XML. hideCurrencyIfHome is always false & home currency is always SGD. Tie it to the setting! (open connection to repo in adapter)?
    visibility = if (dayTransactions.dayIncome == null || (dayTransactions.incomeAllHome && hideCurrencyIfHome)) View.GONE else View.VISIBLE
}

@BindingAdapter("expensesCurrencyVisibility", "hideCurrencyIfHome")
fun TextView.setExpensesCurrencyVisibility(dayTransactions: DayTransactions, hideCurrencyIfHome: Boolean) {
    // TODO: SEE XML. hideCurrencyIfHome is always false & home currency is always SGD. Tie it to the setting! (open connection to repo in adapter)?
    visibility = if (dayTransactions.dayExpenses == null || (dayTransactions.expensesAllHome && hideCurrencyIfHome)) View.GONE else View.VISIBLE
}

////////////////////////////////////////////////////////////////////////////////
/**
 * For rv_ll_item_transactions
 */
////////////////////////////////////////////////////////////////////////////////
@BindingAdapter("currencyVisibility", "hideCurrencyIfHome")
fun TextView.setCurrencyVisibility(transaction: Transaction, hideCurrencyIfHome: Boolean) {
    // TODO: SEE XML. hideCurrencyIfHome is always false. Tie it to the setting! (open connection to repo in adapter)?
    visibility = if (transaction.originalCurrency == "SGD" && hideCurrencyIfHome) View.GONE else View.VISIBLE
}