package com.kaeonx.moneymanager.adapters

import android.view.View
import android.widget.AutoCompleteTextView
import android.widget.ImageView
import android.widget.TextView
import androidx.core.text.bold
import androidx.core.text.buildSpannedString
import androidx.core.text.italic
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import com.google.android.material.textfield.TextInputLayout
import com.kaeonx.moneymanager.handlers.CalendarHandler
import com.kaeonx.moneymanager.handlers.ColourHandler
import com.kaeonx.moneymanager.handlers.CurrencyHandler
import com.kaeonx.moneymanager.handlers.IconHandler
import com.kaeonx.moneymanager.userrepository.UserPDS
import com.kaeonx.moneymanager.userrepository.domain.DayTransactions
import com.kaeonx.moneymanager.userrepository.domain.Transaction
import java.math.BigDecimal


private const val TAG = "biad"

////////////////////////////////////////////////////////////////////////////////
/**
 * For rv_item_transactions_day.xml
 */
////////////////////////////////////////////////////////////////////////////////
@BindingAdapter("dayDateTV_text")
fun TextView.setDayDateTVText(dayTransactions: DayTransactions) {
    text = CalendarHandler.getFormattedString(
        dayTransactions.ymdCalendar,
        "EEE " + UserPDS.getString("dsp_date_format")
    )
}

@BindingAdapter("incomeCurrencyTV_textVisibility")
fun TextView.setIncomeCurrencyTVVisibility(dayTransactions: DayTransactions) {
    text = UserPDS.getString("ccc_home_currency")
    val cond1 = dayTransactions.dayIncome == null
    val cond2 = dayTransactions.incomeAllHome && UserPDS.getBoolean("ccc_hide_matching_currency")
    visibility = if (cond1 || cond2) View.GONE else View.VISIBLE
}

@BindingAdapter("dayExpensesCurrencyTV_textVisibility")
fun TextView.setDayExpensesCurrencyTVVisibility(dayTransactions: DayTransactions) {
    text = UserPDS.getString("ccc_home_currency")
    val cond1 = dayTransactions.dayExpenses == null
    val cond2 = dayTransactions.expensesAllHome && UserPDS.getBoolean("ccc_hide_matching_currency")
    visibility = if (cond1 || cond2) View.GONE else View.VISIBLE
}

////////////////////////////////////////////////////////////////////////////////
/**
 * For rv_ll_item_transactions.xml
 */
////////////////////////////////////////////////////////////////////////////////
@BindingAdapter("transactionCurrencyTV_visibility")
fun TextView.setCurrencyVisibility(transaction: Transaction) {
    val cond1 = transaction.originalCurrency == UserPDS.getString("ccc_home_currency")
    val cond2 = UserPDS.getBoolean("ccc_hide_matching_currency")
    visibility = if (cond1 && cond2) View.GONE else View.VISIBLE
}

@BindingAdapter("transactionSignTV1_textVisibility")
fun TextView.setTransactionSignTV1TextVisibility(type: String) {
    text = if (type == "Income") "+" else "-"
    visibility =
        if (UserPDS.getString("dsp_sign_position") == "before_currency") View.VISIBLE else View.GONE
}

@BindingAdapter("transactionSignTV2_textVisibility")
fun TextView.setTransactionSignTV2TextVisibility(type: String) {
    text = if (type == "Income") "+" else "-"
    visibility =
        if (UserPDS.getString("dsp_sign_position") == "after_currency") View.VISIBLE else View.GONE
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

@BindingAdapter("tbsdTypeTV_text")
fun TextView.setTbsdTypeTVText(type: String) {
    text = when (type) {
//        "?" -> IconHandler.getDisplayHex("F0BA6")
        "?" -> ""
        "Income" -> IconHandler.getDisplayHex("F0048")
        "Expenses" -> IconHandler.getDisplayHex("F0060")
        else -> throw IllegalStateException("Unknown type: $type")
    }
}

@BindingAdapter("tbsdBTDateTime_text")
fun TextView.setTbsdBTDateTimeText(timestamp: Long) {
    val time = CalendarHandler.getFormattedString(timestamp, UserPDS.getString("dsp_time_format"))
    val date = CalendarHandler.getFormattedString(timestamp, UserPDS.getString("dsp_date_format"))
    text = buildSpannedString {
        bold {
            append(time)
        }
        append("\n$date")
    }
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

////////////////////////////////////////////////////////////////////////////////
/**
 * Generic
 */
////////////////////////////////////////////////////////////////////////////////
@BindingAdapter("error_text")
fun TextInputLayout.setErrorText(errorString: String?) {
    error = errorString
}