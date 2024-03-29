package com.kaeonx.moneymanager.fragments.budget

import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.text.bold
import androidx.core.text.buildSpannedString
import androidx.databinding.BindingAdapter
import com.github.mikephil.charting.charts.HorizontalBarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.kaeonx.moneymanager.chartcomponents.HorizontalRoundedStackedBarChartRenderer
import com.kaeonx.moneymanager.chartcomponents.PieChartLegendLLData
import com.kaeonx.moneymanager.databinding.LlItemBudgetBinding
import com.kaeonx.moneymanager.databinding.LlItemDetailTypeNoDataBinding

////////////////////////////////////////////////////////////////////////////////
/**
 * rv_item_budgets
 */
////////////////////////////////////////////////////////////////////////////////
@BindingAdapter(
    "budgetsLL_budgetLLData",
    "budgetsLL_onClickListener",
    "budgetsLL_onLongClickListener"
)
fun LinearLayout.setBudgetsLLAdapter(
    list: List<BudgetLLData>,
    onClickListener: BudgetOnClickListener,
    onLongClickListener: BudgetOnClickListener
) {
    removeAllViews()
    val layoutInflater = LayoutInflater.from(context)
    if (list.isEmpty()) {
        addView(LlItemDetailTypeNoDataBinding.inflate(layoutInflater, null, false).root)
    } else {
        for (budgetLLData in list) {
            val itemBinding = LlItemBudgetBinding.inflate(layoutInflater, null, false)
            itemBinding.budgetLLData = budgetLLData
            itemBinding.onClickListener = onClickListener
            itemBinding.budgetCL.setOnLongClickListener { view ->
                onLongClickListener.onClick(view, budgetLLData.budget.category)
                true
            }
            itemBinding.executePendingBindings()
            addView(itemBinding.root)
        }
    }
}

////////////////////////////////////////////////////////////////////////////////
/**
 * ll_item_budget
 */
////////////////////////////////////////////////////////////////////////////////
@BindingAdapter("stackedHBC_adapter")
fun HorizontalBarChart.setStackedHBCAdapter(barData: BarData) {
    if (legend.isEnabled) {
        setTouchEnabled(false)
        setNoDataText("Please report this bug.")
        setDrawMarkers(false)
        description.isEnabled = false

        axisRight.isEnabled = false
        setDrawValueAboveBar(false)
        setDrawBarShadow(false)

        setViewPortOffsets(0f, 0f, 0f, 0f)  // remove padding
        renderer =
            HorizontalRoundedStackedBarChartRenderer(
                this,
                this.animator,
                this.viewPortHandler
            )
        legend.isEnabled = false
    }

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

    data = barData
    notifyDataSetChanged()
//    invalidate()
    // Courtesy of https://stackoverflow.com/a/35111662/7254995
    // Forces viewPortOffsets to take effect
    post { invalidate() }
}


////////////////////////////////////////////////////////////////////////////////
/**
 * ll_item_budget_detail_legend
 */
////////////////////////////////////////////////////////////////////////////////
@BindingAdapter("budgetCurrencyTV_typeface")
fun TextView.setBudgetCurrencyTVTypeface(data: PieChartLegendLLData.BudgetDetailPCLLD) {
    text = when (data.name) {
        "Expenses" -> buildSpannedString { bold { append(data.currency) } }
        else -> data.currency
    }
}

@BindingAdapter("budgetAmountTV_typeface")
fun TextView.setBudgetAmountTVTypeface(data: PieChartLegendLLData.BudgetDetailPCLLD) {
    text = when (data.name) {
        "Expenses" -> buildSpannedString { bold { append(data.amount) } }
        else -> data.amount
    }
}
