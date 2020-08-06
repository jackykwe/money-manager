package com.kaeonx.moneymanager.fragments.expenses

import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.databinding.BindingAdapter
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.kaeonx.moneymanager.databinding.RvLlItemCategoryBinding

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
        setDrawEntryLabels(false)
        setDrawMarkers(false)

        description.isEnabled = false
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
//    Log.d(TAG, "${pieData.dataSet.getEntryForIndex(0)}")
}

////////////////////////////////////////////////////////////////////////////////
/**
 * rv_item_expenses_detail
 */
////////////////////////////////////////////////////////////////////////////////

@BindingAdapter(
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
        val itemBinding = RvLlItemCategoryBinding.inflate(layoutInflater, null, false)
        itemBinding.expenseLLData = expenseCategory
        itemBinding.onClickListener = itemOnClickListener
        itemBinding.executePendingBindings()
        addView(itemBinding.root)
    }
}