package com.kaeonx.moneymanager.fragments.detail

import androidx.databinding.BindingAdapter
import com.github.mikephil.charting.charts.HorizontalBarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.kaeonx.moneymanager.customclasses.HorizontalRoundedBarChartRenderer


////////////////////////////////////////////////////////////////////////////////
/**
 * (rv_ll_item_detail_X)
 * rv_ll_item_detail_type
 * rv_ll_item_detail_category
 */
////////////////////////////////////////////////////////////////////////////////
@BindingAdapter("bodyHBC_adapter")
fun HorizontalBarChart.setBodyHBCAdapter(barData: BarData) {
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
