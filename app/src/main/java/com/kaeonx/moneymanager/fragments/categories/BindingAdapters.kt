package com.kaeonx.moneymanager.fragments.categories

import android.view.View
import android.widget.AutoCompleteTextView
import android.widget.TextView
import androidx.core.text.buildSpannedString
import androidx.core.text.italic
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import com.kaeonx.moneymanager.handlers.CalendarHandler
import com.kaeonx.moneymanager.handlers.CurrencyHandler
import com.kaeonx.moneymanager.userrepository.UserPDS
import com.kaeonx.moneymanager.userrepository.domain.Transaction
import java.math.BigDecimal

////////////////////////////////////////////////////////////////////////////////
/**
 * For rv_item_type_display.xml
 */
////////////////////////////////////////////////////////////////////////////////
@BindingAdapter("categoryNameTV_text")
fun TextView.setCategoryNameTVText(name: String) {
    text = if (name == "Add…") buildSpannedString { italic { append(name) } } else name
}

////////////////////////////////////////////////////////////////////////////////
/**
 * For fragment_category_edit.xml
 * For fragment_account_edit.xml
 */
////////////////////////////////////////////////////////////////////////////////

@BindingAdapter("autoCompleteTextView_text")
fun AutoCompleteTextView.setText2(newText: String?) {
    if (newText != text.toString()) {
        if (newText == null) clearListSelection() else setText(newText, false)
    }
}

@InverseBindingAdapter(attribute = "autoCompleteTextView_text", event = "android:textAttrChanged")
fun AutoCompleteTextView.getText2(): String {
    return text.toString()
}


// REUSED in rv_ll_item_detail_category
@BindingAdapter("timeTV_text")
fun TextView.setTimeTVText(timestamp: Long) {
    val dateFormat = UserPDS.getString("dsp_date_format")
    val timeFormat = UserPDS.getString("dsp_time_format")
    text = CalendarHandler.getFormattedString(timestamp, "$timeFormat 'on' $dateFormat")
}

@BindingAdapter("convertedAmountHintTV_visibility")
fun TextView.setConvertedAmountHintTVVisibility(transaction: Transaction) {
    visibility = if (transaction.originalCurrency != UserPDS.getString("ccc_home_currency"))
        View.VISIBLE else View.GONE
}

@BindingAdapter("convertedCurrencyTV_textVisibility")
fun TextView.setConvertedCurrencyTVTextVisibility(transaction: Transaction) {
    val homeCurrency = UserPDS.getString("ccc_home_currency")
    visibility = if (transaction.originalCurrency != homeCurrency) {
        text = homeCurrency
        View.VISIBLE
    } else View.GONE
}

@BindingAdapter("convertedAmountTV_textVisibility")
fun TextView.setConvertedAmountTVTextVisibility(transaction: Transaction) {
    val transactionCurrency = transaction.originalCurrency
    val homeCurrency = UserPDS.getString("ccc_home_currency")
    visibility = if (transactionCurrency != homeCurrency) {
        text = CurrencyHandler.displayAmount(
            CurrencyHandler.convertAmount(
                BigDecimal(transaction.originalAmount),
                transactionCurrency,
                homeCurrency
            )
        )
        View.VISIBLE
    } else View.GONE
}