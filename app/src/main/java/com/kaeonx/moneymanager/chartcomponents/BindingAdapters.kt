package com.kaeonx.moneymanager.chartcomponents

import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.databinding.BindingAdapter
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.formatter.LargeValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.kaeonx.moneymanager.R
import com.kaeonx.moneymanager.activities.App
import com.kaeonx.moneymanager.activities.MainActivity
import com.kaeonx.moneymanager.databinding.LlItemBudgetDetailLegendBinding
import com.kaeonx.moneymanager.databinding.LlItemDetailTypeLegendBinding
import com.kaeonx.moneymanager.databinding.LlItemDetailTypeNoDataBinding
import com.kaeonx.moneymanager.handlers.ColourHandler

////////////////////////////////////////////////////////////////////////////////
/**
 * chart_component_pie_card_with_legend
 */
////////////////////////////////////////////////////////////////////////////////
@BindingAdapter("pieChart_pieData")
fun PieChart.setPieChartAdapter(pieData: PieData) {
    if (legend.isEnabled) {
        setTouchEnabled(false)
        setNoDataText("Please report this bug.")
        setDrawMarkers(false)
        description.isEnabled = false

        setDrawEntryLabels(false)
        //    centerText = ""
        //    setUsePercentValues(true)
        holeRadius = 75f
        transparentCircleRadius = 80f
        setHoleColor(android.R.color.transparent)
        setTransparentCircleColor(
            when (MainActivity.isNight) {
                true -> App.context.resources.getColor(R.color.dark_surface, null)
                false -> App.context.resources.getColor(R.color.white, null)
            }
        )

        legend.isEnabled = false
    }
    data = pieData
    notifyDataSetChanged()
    invalidate()
}

@BindingAdapter("pieChartLegendLL_data")
fun LinearLayout.setPieChartLegendLLData(list: List<PieChartLegendLLData>) {
    removeAllViews()
    val layoutInflater = LayoutInflater.from(context)
    for (typeLegendLLData in list) {
        when (typeLegendLLData) {
            is PieChartLegendLLData.DetailCategoryPCLLD -> {
                if (typeLegendLLData.noDataFlag) {
                    addView(LlItemDetailTypeNoDataBinding.inflate(layoutInflater, null, false).root)
                } else {
                    val itemBinding = LlItemDetailTypeLegendBinding.inflate(
                        layoutInflater, null, false
                    )
                    itemBinding.typeLegendLLData = typeLegendLLData
                    itemBinding.executePendingBindings()
                    addView(itemBinding.root)
                }
            }
            is PieChartLegendLLData.BudgetDetailPCLLD -> {
                val itemBinding =
                    LlItemBudgetDetailLegendBinding.inflate(layoutInflater, null, false)
                itemBinding.budgetLegendLLData = typeLegendLLData
                itemBinding.executePendingBindings()
                addView(itemBinding.root)
            }
        }
    }
}

////////////////////////////////////////////////////////////////////////////////
/**
 * chart_component_line_card
 */
////////////////////////////////////////////////////////////////////////////////
@BindingAdapter("lineChart_packet")
fun LineChart.setLineChartAdapter(lineChartPacket: LineChartPacket) {
    if (legend.isEnabled) {
        setTouchEnabled(false)
        setNoDataText("Please report this bug.")
        setDrawMarkers(false)
        description.isEnabled = false

        xAxis.apply {
            position = XAxis.XAxisPosition.BOTTOM
            spaceMin = 1f  // 1 extra day at the start
            spaceMax = 1f  // 1 extra day at the end
            setDrawGridLines(true)
            setDrawLabels(true)
            textColor = when (MainActivity.isNight) {
                true -> App.context.resources.getColor(R.color.grey_200, null)
                false -> App.context.resources.getColor(R.color.black, null)
            }
        }
        axisLeft.apply {
            axisMinimum = 0f
            setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART)
            setDrawLimitLinesBehindData(true)
            setDrawAxisLine(false)
            textColor = when (MainActivity.isNight) {
                true -> App.context.resources.getColor(R.color.grey_200, null)
                false -> App.context.resources.getColor(R.color.black, null)
            }

            valueFormatter = LargeValueFormatter()
            // to prevent rendering of decimals (for values <1) which shows weird values like 500E3 (for 0.5) and 400.3 (for 0.4)
            granularity = 1f
        }
        axisRight.isEnabled = false
        minOffset = 0f  // remove unnecessary padding
        extraBottomOffset = 1f
//        setViewPortOffsets(88f, 0f, 0f, 48f)  // remove padding
        legend.isEnabled = false
    }

    setXAxisRenderer(
        XAxisRendererSpecificLabel(
            viewPortHandler,
            xAxis,
            getTransformer(YAxis.AxisDependency.LEFT),
            lineChartPacket.xAxisLabelMap.keys.toList()
        )
    )
    xAxis.apply {
        valueFormatter = object : ValueFormatter() {
            override fun getAxisLabel(value: Float, axis: AxisBase?): String {
                return lineChartPacket.xAxisLabelMap[value] ?: ""
            }
        }
    }
    axisLeft.apply {
        removeAllLimitLines()  // prevents stacking of LimitLines when ViewHolder is reused
        addLimitLine(
            LimitLine(
                lineChartPacket.lowerLimitLineValue, lineChartPacket.lowerLimitLineText
            ).apply {
                lineWidth = 1f
                labelPosition = if (lineChartPacket.upperLimitLineValue != null)
                    LimitLine.LimitLabelPosition.RIGHT_BOTTOM
                else LimitLine.LimitLabelPosition.RIGHT_TOP
                when (MainActivity.isNight) {
                    true -> App.context.resources.getColor(R.color.dark_surface, null)
                    false -> App.context.resources.getColor(R.color.white, null)
                }
                ColourHandler.getSpecificColourObjectOf(
                    when (MainActivity.isNight) {
                        true -> "Red,300"
                        false -> "Red,500"
                    }
                ).let {
                    lineColor = it
                    textColor = it
                }
            }
        )

        if (lineChartPacket.upperLimitLineValue != null) addLimitLine(
            LimitLine(
                lineChartPacket.upperLimitLineValue, lineChartPacket.upperLimitLineText
            ).apply {
                lineWidth = 1f
                labelPosition = LimitLine.LimitLabelPosition.RIGHT_TOP
                ColourHandler.getSpecificColourObjectOf(
                    when (MainActivity.isNight) {
                        true -> "Red,300"
                        false -> "Red,500"
                    }
                ).let {
                    lineColor = it
                    textColor = it
                }
            }
        )
    }

    data = lineChartPacket.lineData
    notifyDataSetChanged()
    post { invalidate() }

    animateY(500, Easing.EaseOutQuart)
}
