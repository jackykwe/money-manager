package com.kaeonx.moneymanager.fragments.detail

import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.databinding.BindingAdapter
import com.kaeonx.moneymanager.databinding.RvLlItemDetailCategoryBinding

//
//////////////////////////////////////////////////////////////////////////////////
///**
// * rv_item_detail_type_summary
// */
//////////////////////////////////////////////////////////////////////////////////
//@BindingAdapter("typeSummaryPC_adapter")
//fun PieChart.setTypeSummaryPCAdapter(pieData: PieData?) {
//    if (legend.isEnabled) {
//        setTouchEnabled(false)
//        setNoDataText("Hello, you wanna provide some data?")
//        setDrawMarkers(false)
//        description.isEnabled = false
//
//        setDrawEntryLabels(false)
//        //    centerText = ""
//        //    setUsePercentValues(true)
//        holeRadius = 75f
//        transparentCircleRadius = 80f
//
//        legend.isEnabled = false
//    }
//    data = pieData
//    notifyDataSetChanged()
//    invalidate()
//}
//
//@BindingAdapter("typeSummaryLegendLL_data")
//fun LinearLayout.setTypeSummaryLegendLLData(list: List<DetailTypeLegendLLData>) {
//    removeAllViews()
//    val layoutInflater = LayoutInflater.from(context)
//    for (typeLegendLLData in list) {
//        val itemBinding = LlItemDetailTypeLegendBinding.inflate(layoutInflater, null, false)
//        itemBinding.typeLegendLLData = typeLegendLLData
//        itemBinding.executePendingBindings()
//        addView(itemBinding.root)
//    }
//}
//
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