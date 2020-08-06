package com.kaeonx.moneymanager.fragments.type

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
import com.kaeonx.moneymanager.databinding.LlItemTypeDetailLegendBinding
import com.kaeonx.moneymanager.databinding.RvLlItemTypeBinding

////////////////////////////////////////////////////////////////////////////////
/**
 * rv_item_type_detail_summary
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
fun LinearLayout.setTypeSummaryLegendLLData(list: List<TypeLegendLLData>) {
    removeAllViews()
    val layoutInflater = LayoutInflater.from(context)
    for (typeLegendLLData in list) {
        val itemBinding = LlItemTypeDetailLegendBinding.inflate(layoutInflater, null, false)
        itemBinding.typeLegendLLData = typeLegendLLData
        itemBinding.executePendingBindings()
        addView(itemBinding.root)
    }
}

////////////////////////////////////////////////////////////////////////////////
/**
 * ll_item_type_detail_legend
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
 * rv_item_type_detail_categories
 */
////////////////////////////////////////////////////////////////////////////////
@BindingAdapter("categoriesLL_typeCategoryList", "categoriesLL_onClickListener")
fun LinearLayout.setCategoriesLLAdapter(
    list: List<TypeCategoryLLData>,
    itemOnClickListener: TypeDetailOnClickListener
) {
    removeAllViews()
    val layoutInflater = LayoutInflater.from(context)
    for (typeCategoryLLData in list) {
        val itemBinding = RvLlItemTypeBinding.inflate(layoutInflater, null, false)
        itemBinding.typeCategoryLLData = typeCategoryLLData
        itemBinding.onClickListener = itemOnClickListener
        itemBinding.executePendingBindings()
        addView(itemBinding.root)
    }
}

////////////////////////////////////////////////////////////////////////////////
/**
 * rv_ll_item_type
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

        setViewPortOffsets(0f, 0f, 0f, 0f)  // remove padding
        renderer = HorizontalRoundedBarChartRenderer(this, this.animator, this.viewPortHandler)
        legend.isEnabled = false
    }
    data = barData
    notifyDataSetChanged()
//    invalidate()
    // Courtesy of https://stackoverflow.com/a/35111662/7254995
    // Forces viewPortOffsets to take effect
    post { invalidate() }
}
