package com.kaeonx.moneymanager.fragments.categories

import android.view.View
import android.widget.AutoCompleteTextView
import android.widget.TextView
import androidx.core.text.buildSpannedString
import androidx.core.text.italic
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import com.kaeonx.moneymanager.adapters.ColourFamilyPickerArrayAdapter
import com.kaeonx.moneymanager.adapters.ColourIntensityPickerArrayAdapter
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
    text = if (name == "Add...") buildSpannedString { italic { append(name) } } else name
}

////////////////////////////////////////////////////////////////////////////////
/**
 * For fragment_category_edit.xml
 */
////////////////////////////////////////////////////////////////////////////////

@BindingAdapter("autoCompleteTextView_text")
fun AutoCompleteTextView.setText2(newText: String?) {
    if (newText != text.toString()) {
//        Log.d(TAG, "SETTING $tag displayed value to $newText")
        if (newText == null) clearListSelection() else setText(newText, false)
    }
}

@InverseBindingAdapter(attribute = "autoCompleteTextView_text", event = "android:textAttrChanged")
fun AutoCompleteTextView.getText2(): String {
//    Log.d(TAG, "GETTING $tag")
    return text.toString()
}

@BindingAdapter("colourFamilySpinner_colourFamiliesAL", "colourFamilySpinner_colourIntensity")
fun AutoCompleteTextView.updateColourFamilySpinnerAdapter(
    newColourFamilies: List<String>,
    newColourIntensity: String?
) {
//    For debugging
//    (adapter as ColourFamilyPickerArrayAdapter).apply {
//        if (newColourFamilies != colourFamilies && newColourIntensity != colourIntensity) {
//            Log.d(TAG, "WARN: SPINNER FAMILY UPDATE newColourFamilies ${newColourFamilies.size} and newColourIntensity $newColourIntensity")
//            updateData(newColourFamilies, newColourIntensity)
//        } else if (newColourFamilies != colourFamilies) {
//            Log.d(TAG, "WARN: SPINNER FAMILY UPDATE newColourFamilies ${newColourFamilies.size}")
//            updateData(newColourFamilies, newColourIntensity)
//        } else if (newColourIntensity != colourIntensity) {
//            Log.d(TAG, "WARN: SPINNER FAMILY UPDATE newColourIntensity $newColourIntensity")
//            updateData(newColourFamilies, newColourIntensity)
//        }
//    }
    (adapter as ColourFamilyPickerArrayAdapter).updateData(newColourFamilies, newColourIntensity)
}

@BindingAdapter("colourIntensitySpinner_colourFamily", "colourIntensitySpinner_colourIntensitiesAL")
fun AutoCompleteTextView.updateColourIntensitySpinnerAdapter(
    newColourFamily: String,
    newColourIntensities: List<String>
) {
//    For debugging
//    (adapter as ColourIntensityPickerArrayAdapter).apply {
//        if (newColourFamily != colourFamily && newColourIntensities != colourIntensities) {
//            Log.d(TAG, "WARN: SPINNER INTENSITY UPDATE newColourFamily $newColourFamily and newColourIntensities ${newColourIntensities.size}")
//            updateData(newColourFamily, newColourIntensities)
//        } else if (newColourFamily != colourFamily) {
//            Log.d(TAG, "WARN: SPINNER INTENSITY UPDATE newColourFamily $newColourFamily")
//            updateData(newColourFamily, newColourIntensities)
//        } else if (newColourIntensities != colourIntensities) {
//            Log.d(TAG, "WARN: SPINNER INTENSITY UPDATE newColourIntensities ${newColourIntensities.size}")
//            updateData(newColourFamily, newColourIntensities)
//        }
//    }
    (adapter as ColourIntensityPickerArrayAdapter).updateData(newColourFamily, newColourIntensities)
}

// REUSED in rv_ll_item_detail_category
@BindingAdapter("timeTV_text")
fun TextView.setTimeTVText(timestamp: Long) {
    val dateFormat = UserPDS.getString("dsp_date_format")
    val timeFormat = UserPDS.getString("dsp_time_format")
    text = CalendarHandler.getFormattedString(timestamp, "$timeFormat 'on' $dateFormat")
}

@BindingAdapter("convertedAmountHintTV_visibility")
fun TextView.setConvertedAmountHintTV_visibility(transaction: Transaction) {
    visibility = if (transaction.originalCurrency != UserPDS.getString("ccc_home_currency"))
        View.VISIBLE else View.GONE
}

@BindingAdapter("convertedCurrencyTV_textVisiibility")
fun TextView.setConvertedCurrencyTV_textVisibility(transaction: Transaction) {
    val homeCurrency = UserPDS.getString("ccc_home_currency")
    visibility = if (transaction.originalCurrency != homeCurrency) {
        text = homeCurrency
        View.VISIBLE
    } else View.GONE
}

@BindingAdapter("convertedAmountTV_textVisiibility")
fun TextView.setConvertedAmountTV_textVisibility(transaction: Transaction) {
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