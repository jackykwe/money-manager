package com.kaeonx.moneymanager.fragments.detail

import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.databinding.BindingAdapter
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.LineData
import com.kaeonx.moneymanager.databinding.RvLlItemDetailCategoryBinding

////////////////////////////////////////////////////////////////////////////////
/**
 * rv_item_detail_category_summary
 */
////////////////////////////////////////////////////////////////////////////////
@BindingAdapter("categorySummaryLC_adapter")
fun LineChart.setTypeSummaryPCAdapter(lineData: LineData) {
    if (legend.isEnabled) {
        setTouchEnabled(false)
        setNoDataText("Hello, you wanna provide some data?")
        setDrawMarkers(false)
        description.isEnabled = false

        setViewPortOffsets(0f, 0f, 0f, 0f)  // remove padding
        legend.isEnabled = false
    }
    data = lineData
    notifyDataSetChanged()
    invalidate()
}


//////////////////////////////////////////////////////////////////////////////////
///**
// * ll_item_detail_type_legend
// */
//////////////////////////////////////////////////////////////////////////////////
//@BindingAdapter("nameTV_typeface")
//fun TextView.setNameTVTypeface(name: String) {
//    if (name == "(multiple)") {
//        text = buildSpannedString { italic { append(name) } }
//    }
//}
//
////////////////////////////////////////////////////////////////////////////////
/**
 * rv_item_detail_category_transactions
 */
////////////////////////////////////////////////////////////////////////////////
@BindingAdapter("transactionsLL_transactionLLData", "transactionsLL_onClickListener")
fun LinearLayout.setTransactionsLLAdapter(
    list: List<DetailCategoryTransactionLLData>,
    itemOnClickListener: DetailCategoryOnClickListener
) {
    removeAllViews()
    val layoutInflater = LayoutInflater.from(context)
    for (categoryTransactionLLData in list) {
        val itemBinding = RvLlItemDetailCategoryBinding.inflate(layoutInflater, null, false)
        itemBinding.categoryTransactionLLData = categoryTransactionLLData
        itemBinding.onClickListener = itemOnClickListener
        itemBinding.executePendingBindings()
        addView(itemBinding.root)
    }
}