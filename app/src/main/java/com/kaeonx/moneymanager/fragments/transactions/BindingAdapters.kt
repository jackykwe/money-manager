package com.kaeonx.moneymanager.fragments.transactions

import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.text.bold
import androidx.core.text.buildSpannedString
import androidx.databinding.BindingAdapter
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.kaeonx.moneymanager.R
import com.kaeonx.moneymanager.activities.App
import com.kaeonx.moneymanager.databinding.RvLlItemTransactionBinding
import com.kaeonx.moneymanager.handlers.CalendarHandler
import com.kaeonx.moneymanager.handlers.ColourHandler
import com.kaeonx.moneymanager.handlers.IconHandler
import com.kaeonx.moneymanager.userrepository.UserPDS
import com.kaeonx.moneymanager.userrepository.domain.DayTransactions
import com.kaeonx.moneymanager.userrepository.domain.Transaction

////////////////////////////////////////////////////////////////////////////////
/**
 * rv_item_transactions_summary
 */
////////////////////////////////////////////////////////////////////////////////
@BindingAdapter("budgetPC_adapter")
fun PieChart.setBudgetPCAdapter(pieData: PieData) {
    if (legend.isEnabled) {
        setTouchEnabled(false)
        setNoDataText("Please report this bug.")
        description.isEnabled = false

        setDrawEntryLabels(false)
        //    centerText = ""
        //    setUsePercentValues(true)
        holeRadius = 75f
        transparentCircleRadius = 80f
        setHoleColor(android.R.color.transparent)
        setTransparentCircleColor(
            when (val theme = UserPDS.getDSPString("dsp_theme", "light")) {
                "dark" -> App.context.resources.getColor(R.color.dark_surface, null)
                "light" -> App.context.resources.getColor(R.color.white, null)
                else -> throw IllegalArgumentException("Unknown dsp_theme $theme")
            }
        )

        legend.isEnabled = false
    }
    data = pieData
    notifyDataSetChanged()
    invalidate()
}

////////////////////////////////////////////////////////////////////////////////
/**
 * For rv_item_transactions_day.xml
 */
////////////////////////////////////////////////////////////////////////////////

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

@BindingAdapter(
    "dayTransactionsLL_dayTransactions",
    "dayTransactionsLL_onClickListener",
    "dayTransactionsLL_onLongClickListener"
)
fun LinearLayout.setDayTransactionsLLAdapter(
    dayTransactions: DayTransactions,
    itemOnClickListener: TransactionOnClickListener,
    itemOnLongClickListener: TransactionOnClickListener
) {
    removeAllViews()
    val layoutInflater = LayoutInflater.from(context)
    for (transaction in dayTransactions.transactions) {
        val itemBinding = RvLlItemTransactionBinding.inflate(layoutInflater, null, false)
        itemBinding.transaction = transaction
        itemBinding.onClickListener = itemOnClickListener
        itemBinding.transactionLL.setOnLongClickListener { view ->
            itemOnLongClickListener.onClick(view, transaction.transactionId!!)
            true
        }
        itemBinding.executePendingBindings()
        addView(itemBinding.root)
    }
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
 * For dialog_fragment_transactions_bsdf.xml
 */
////////////////////////////////////////////////////////////////////////////////
@BindingAdapter("tbsdHorizontalBarIVTop_tint")
fun ImageView.setTbsdHorizontalBarIVTopTint(colourFamily: String) {
    drawable.setTintList(ColourHandler.getColorStateListOf(colourFamily))
}

@BindingAdapter("tbsdHorizontalBarIVBottom_tint")
fun ImageView.setTbsdHorizontalBarIVBottomTint(colourFamily: String) {
    drawable.setTintList(ColourHandler.getColorStateListOf(colourFamily))
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