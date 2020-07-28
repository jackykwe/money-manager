package com.kaeonx.moneymanager.adapters

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.kaeonx.moneymanager.handlers.CalendarHandler
import com.kaeonx.moneymanager.handlers.ColourHandler
import com.kaeonx.moneymanager.handlers.IconHandler
import com.kaeonx.moneymanager.userrepository.domain.DayTransactions
import com.kaeonx.moneymanager.userrepository.domain.Transaction

private const val TAG = "biad"

////////////////////////////////////////////////////////////////////////////////
/**
 * For rv_item_transactions_day.xml
 */
////////////////////////////////////////////////////////////////////////////////
@BindingAdapter("dayDateTV_text")
fun TextView.setDayDateTVText(dayTransactions: DayTransactions) {
    // TODO: tie to default date format (EEE <default date format>)
    text = CalendarHandler.getFormattedString(dayTransactions.ymdCalendar, "EEE ddMMyy")
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

////////////////////////////////////////////////////////////////////////////////
/**
 * For icon_transaction.xml
 */
////////////////////////////////////////////////////////////////////////////////
@BindingAdapter("iconRing_tint")
fun ImageView.setIconRingTint(colourString: String) {
    imageTintList = ColourHandler.getColorStateList(colourString)
}

@BindingAdapter("iconBG_tint")
fun ImageView.setIconBGTint(colourString: String) {
    imageTintList = ColourHandler.getColorStateList(colourString)
}

@BindingAdapter("iconTV_text")
fun TextView.setIconTVText(iconHex: String) {
    text = IconHandler.getDisplayHex(iconHex)
}

////////////////////////////////////////////////////////////////////////////////
/**
 * For dialog_fragment_transactions_bsdf.xml
 */
////////////////////////////////////////////////////////////////////////////////
@BindingAdapter("tbsdHorizontalBarIVTop_tint")
fun ImageView.setTbsdHorizontalBarIVTopTint(colourString: String) {
    drawable.setTintList(ColourHandler.getColorStateList(colourString))
}

@BindingAdapter("tbsdHorizontalBarIVBottom_tint")
fun ImageView.setTbsdHorizontalBarIVBottomTint(colourString: String) {
    drawable.setTintList(ColourHandler.getColorStateList(colourString))
}