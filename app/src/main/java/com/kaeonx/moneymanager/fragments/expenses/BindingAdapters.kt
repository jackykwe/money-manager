package com.kaeonx.moneymanager.fragments.expenses

import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.text.buildSpannedString
import androidx.core.text.italic
import androidx.databinding.BindingAdapter
import com.github.mikephil.charting.charts.HorizontalBarChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.PieData
import com.kaeonx.moneymanager.customclasses.HorizontalRoundedBarChartRenderer
import com.kaeonx.moneymanager.databinding.LlItemExpensesLegendBinding
import com.kaeonx.moneymanager.databinding.RvLlItemExpenseBinding

////////////////////////////////////////////////////////////////////////////////
/**
 * rv_item_expenses_summary
 */
////////////////////////////////////////////////////////////////////////////////
@BindingAdapter("expensesPC_adapter")
fun PieChart.setExpensesPCAdapter(pieData: PieData?) {
    if (legend.isEnabled) {
        setTouchEnabled(false)
        setNoDataText("Hello, you wanna provide some data?")
        setDrawMarkers(false)
        description.isEnabled = false

        setDrawEntryLabels(false)
        //    centerText = ""
        //    setUsePercentValues(true)
        holeRadius = 75f
        transparentCircleRadius = 80f

        legend.isEnabled = false
    }
    data = pieData
    notifyDataSetChanged()
    invalidate()
}

@BindingAdapter("expensesLegendLL_data")
fun LinearLayout.setExpensesLegendLLData(list: List<ExpensesLegendLLData>) {
    removeAllViews()
    val layoutInflater = LayoutInflater.from(context)
    for (legendLLData in list) {
        val itemBinding = LlItemExpensesLegendBinding.inflate(layoutInflater, null, false)
        itemBinding.legendLLData = legendLLData
        itemBinding.executePendingBindings()
        addView(itemBinding.root)
    }
}

////////////////////////////////////////////////////////////////////////////////
/**
 * ll_item_expenses_legend
 */
////////////////////////////////////////////////////////////////////////////////
@BindingAdapter("nameTV_typeface")
fun TextView.setNameTVTypeface(name: String) {
    if (name == "(multiple)") {
        text = buildSpannedString { italic { append(name) } }
    }
}

////////////////////////////////////////////////////////////////////////////////
/**
 * rv_item_expenses_detail
 */
////////////////////////////////////////////////////////////////////////////////
@BindingAdapter(  //TODO: CHANGE THIS UGLY NAME
    "categoriesTransactionsLL_expenseCategoryList",
    "categoriesTransactionsLL_onClickListener"
)
fun LinearLayout.setCategoriesTransactionsLLAdapter(
    list: List<ExpenseDetailLLData>,
    itemOnClickListener: ExpensesOnClickListener
) {
    removeAllViews()
    val layoutInflater = LayoutInflater.from(context)
    for (expenseCategory in list) {
        val itemBinding = RvLlItemExpenseBinding.inflate(layoutInflater, null, false)
        itemBinding.expenseLLData = expenseCategory
        itemBinding.onClickListener = itemOnClickListener
        itemBinding.executePendingBindings()
        addView(itemBinding.root)
    }
}

////////////////////////////////////////////////////////////////////////////////
/**
 * rv_ll_item_expense
 */
////////////////////////////////////////////////////////////////////////////////
@BindingAdapter("categoryHBC_adapter")
fun HorizontalBarChart.setCategoryHBCAdapter(barData: BarData) {
    if (legend.isEnabled) {
        setTouchEnabled(false)
        setNoDataText("Please report this bug.")
        setDrawMarkers(false)
        description.isEnabled = false

        xAxis.apply {
            isEnabled = false
            position = XAxis.XAxisPosition.BOTH_SIDED
            axisMinimum = -0.5f
            axisMaximum = 0.5f
        }
        axisLeft.apply {
            isEnabled = false
            axisMinimum = 0f
            axisMaximum = 1f
        }
        axisRight.isEnabled = false
        setDrawValueAboveBar(false)
        setDrawBarShadow(false)

        legend.isEnabled = false
    }
    setViewPortOffsets(0f, 0f, 0f, 0f)  // remove padding

    data = barData
    notifyDataSetChanged()

    renderer = HorizontalRoundedBarChartRenderer(this, this.animator, this.viewPortHandler)
//    invalidate()
    // Courtesy of https://stackoverflow.com/a/35111662/7254995
    // Forces viewPortOffsets to take effect
    post { invalidate() }
}
