package com.kaeonx.moneymanager.adapters

import android.view.View
import android.widget.AutoCompleteTextView
import android.widget.ImageView
import android.widget.TextView
import androidx.core.text.buildSpannedString
import androidx.core.text.italic
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import com.google.android.material.textfield.TextInputLayout
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
    visibility =
        if (dayTransactions.dayIncome == null || (dayTransactions.incomeAllHome && false)) View.GONE else View.VISIBLE
}

@BindingAdapter("dayExpensesCurrencyTV_visibility")
fun TextView.setDayExpensesCurrencyTVVisibility(dayTransactions: DayTransactions) {
    // TODO: SEE XML. hideCurrencyIfHome is always false & home currency is always SGD. Tie it to the setting! (open connection to repo in adapter)?
//    visibility = if (dayTransactions.dayExpenses == null || (dayTransactions.expensesAllHome && hideCurrencyIfHome)) View.GONE else View.VISIBLE
    visibility =
        if (dayTransactions.dayExpenses == null || (dayTransactions.expensesAllHome && false)) View.GONE else View.VISIBLE
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
//    if (colourString == null) {
//        visibility = View.INVISIBLE
//    } else {
//        visibility = View.VISIBLE
//        imageTintList = ColourHandler.getColorStateList(colourString)
//    }
}

@BindingAdapter("iconBG_tint")
fun ImageView.setIconBGTint(colourString: String?) {
    if (colourString == null) {
        visibility = View.INVISIBLE
    } else {
        visibility = View.VISIBLE
        imageTintList = ColourHandler.getColorStateList(colourString)
    }
}

@BindingAdapter("iconTV_text")
fun TextView.setIconTVText(iconHex: String?) {
    text = IconHandler.getDisplayHex(iconHex ?: "F02D6")
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


////////////////////////////////////////////////////////////////////////////////
/**
 * For rv_item_accounts_display.xml
 */
////////////////////////////////////////////////////////////////////////////////
@BindingAdapter("horizontalBarIV_tint")
fun ImageView.setHorizontalBarIVTint(colourString: String) {
    drawable.setTintList(ColourHandler.getColorStateList(colourString))
}

@BindingAdapter("accountNameTV_textColor")
fun TextView.setAccountNameTVTextColor(name: String) {
    setTextColor(
        if (name == "Add...") ColourHandler.getColorStateList("Black") else ColourHandler.getColorStateList(
            "White"
        )
    )
}

@BindingAdapter("accountNameTV_text")
fun TextView.setAccountNameTVText(name: String) {
    text = if (name == "Add...") buildSpannedString { italic { append(name) } } else name
}


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
fun AutoCompleteTextView.updateColourFamilySpinnerAdapter(newColourFamilies: List<String>, newColourIntensity: String?) {
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
fun AutoCompleteTextView.updateColourIntensitySpinnerAdapter(newColourFamily: String, newColourIntensities: List<String>) {
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

////////////////////////////////////////////////////////////////////////////////
/**
 * Generic
 */
////////////////////////////////////////////////////////////////////////////////
@BindingAdapter("error_text")
fun TextInputLayout.setErrorText(errorString: String?) {
    error = errorString
}