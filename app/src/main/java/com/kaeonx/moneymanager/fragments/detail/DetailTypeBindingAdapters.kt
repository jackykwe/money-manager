package com.kaeonx.moneymanager.fragments.detail

import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.text.buildSpannedString
import androidx.core.text.italic
import androidx.databinding.BindingAdapter
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.kaeonx.moneymanager.databinding.LlItemDetailTypeLegendBinding
import com.kaeonx.moneymanager.databinding.LlItemDetailTypeNoDataBinding
import com.kaeonx.moneymanager.databinding.RvLlItemDetailTypeBinding

////////////////////////////////////////////////////////////////////////////////
/**
 * rv_item_detail_type_summary
 */
////////////////////////////////////////////////////////////////////////////////
@BindingAdapter("typeSummaryPC_adapter")
fun PieChart.setTypeSummaryPCAdapter(pieData: PieData?) {
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

@BindingAdapter("typeSummaryLegendLL_data")
fun LinearLayout.setTypeSummaryLegendLLData(list: List<DetailTypeLegendLLData>) {
    removeAllViews()
    val layoutInflater = LayoutInflater.from(context)
    for (typeLegendLLData in list) {
        if (typeLegendLLData.noDataFlag) {
            addView(LlItemDetailTypeNoDataBinding.inflate(layoutInflater, null, false).root)
        } else {
            val itemBinding = LlItemDetailTypeLegendBinding.inflate(layoutInflater, null, false)
            itemBinding.typeLegendLLData = typeLegendLLData
            itemBinding.executePendingBindings()
            addView(itemBinding.root)
        }
    }
}

////////////////////////////////////////////////////////////////////////////////
/**
 * ll_item_detail_type_legend
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
 * rv_item_detail_type_categories
 */
////////////////////////////////////////////////////////////////////////////////
@BindingAdapter("categoriesLL_categoryLLData", "categoriesLL_onClickListener")
fun LinearLayout.setCategoriesLLAdapter(
    list: List<DetailTypeCategoryLLData>,
    itemOnClickListener: DetailTypeOnClickListener
) {
    removeAllViews()
    val layoutInflater = LayoutInflater.from(context)
    for (typeCategoryLLData in list) {
        val itemBinding = RvLlItemDetailTypeBinding.inflate(layoutInflater, null, false)
        itemBinding.typeCategoryLLData = typeCategoryLLData
        itemBinding.onClickListener = itemOnClickListener
        itemBinding.executePendingBindings()
        addView(itemBinding.root)
    }
}