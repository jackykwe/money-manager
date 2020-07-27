package com.kaeonx.moneymanager.adapters

import android.view.View
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.kaeonx.moneymanager.customclasses.toFormattedString
import com.kaeonx.moneymanager.userrepository.domain.DayTransactions
import com.kaeonx.moneymanager.userrepository.domain.Transaction

////////////////////////////////////////////////////////////////////////////////
/**
 * For rv_item_transactions_day.xml
 */
////////////////////////////////////////////////////////////////////////////////
@BindingAdapter("dayDateTV_text")
fun TextView.setDayDateTVText(dayTransactions: DayTransactions) {
    // TODO: tie to default date format (EEE <default date format>)
    text = dayTransactions.ymdCalendar.toFormattedString("EEE ddMMyy")
}

@BindingAdapter("incomeCurrencyTV_visibility")
fun TextView.setIncomeCurrencyTVVisibility(dayTransactions: DayTransactions) {
    // TODO: SEE XML. hideCurrencyIfHome is always false & home currency is always SGD. Tie it to the setting! (open connection to repo in adapter)?
//    visibility = if (dayTransactions.dayIncome == null || (dayTransactions.incomeAllHome && hideCurrencyIfHome)) View.GONE else View.VISIBLE
    visibility = if (dayTransactions.dayIncome == null || (dayTransactions.incomeAllHome && false)) View.GONE else View.VISIBLE
}

@BindingAdapter("dayExpensesCurrencyTV_visibility")
fun TextView.setDayExpensesCurrencyTVVisibility(dayTransactions: DayTransactions) {
    // TODO: SEE XML. hideCurrencyIfHome is always false & home currency is always SGD. Tie it to the setting! (open connection to repo in adapter)?
//    visibility = if (dayTransactions.dayExpenses == null || (dayTransactions.expensesAllHome && hideCurrencyIfHome)) View.GONE else View.VISIBLE
    visibility = if (dayTransactions.dayExpenses == null || (dayTransactions.expensesAllHome && false)) View.GONE else View.VISIBLE
}

////////////////////////////////////////////////////////////////////////////////
/**
 * For rv_ll_item_transactions.xml
 */
////////////////////////////////////////////////////////////////////////////////
@BindingAdapter("transactionCurrencyTV_visibility")
fun TextView.setCurrencyVisibility(transaction: Transaction) {
    // TODO: SEE XML. hideCurrencyIfHome is always false. Tie it to the setting! (open connection to repo in adapter)?
//    visibility = if (transaction.originalCurrency == "SGD" && hideCurrencyIfHome) View.GONE else View.VISIBLE
    visibility = if (transaction.originalCurrency == "SGD" && false) View.GONE else View.VISIBLE
}