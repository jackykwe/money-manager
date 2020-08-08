package com.kaeonx.moneymanager.fragments.detail

import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.databinding.BindingAdapter
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.formatter.LargeValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.kaeonx.moneymanager.customclasses.XAxisRendererSpecificLabel
import com.kaeonx.moneymanager.databinding.RvLlItemDetailCategoryBinding
import com.kaeonx.moneymanager.handlers.ColourHandler

////////////////////////////////////////////////////////////////////////////////
/**
 * rv_item_detail_category_summary
 */
////////////////////////////////////////////////////////////////////////////////
@BindingAdapter("categorySummaryLC_lineData", "categorySummaryLC_Extras")
fun LineChart.setTypeSummaryPCAdapter(lineData: LineData, extras: DetailCategorySummaryExtras) {
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
        }
        axisLeft.apply {
            axisMinimum = 0f
            setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART)
            setDrawLimitLinesBehindData(true)
            setDrawAxisLine(false)

            valueFormatter = LargeValueFormatter()
            granularity =
                1f  // to prevent rendering of decimals (for values <1) which shows weird values like 500E3 (for 0.5) and 400.3 (for 0.4)
        }
        axisRight.isEnabled = false
        minOffset = 0f  // remove unnecessary padding
        extraBottomOffset = 4f
//        setViewPortOffsets(88f, 0f, 0f, 48f)  // remove padding
        legend.isEnabled = false
    }

    setXAxisRenderer(
        XAxisRendererSpecificLabel(
            viewPortHandler,
            xAxis,
            getTransformer(YAxis.AxisDependency.LEFT),
            extras.xAxisLabelMap.keys.toList()
        )
    )
    xAxis.apply {
        valueFormatter = object : ValueFormatter() {
            override fun getAxisLabel(value: Float, axis: AxisBase?): String {
                return extras.xAxisLabelMap[value] ?: ""
            }
        }
    }
    axisLeft.apply {
        addLimitLine(
            LimitLine(
                extras.dayAverageValue, extras.dayAverageText
            ).apply {
                lineWidth = 1f
                labelPosition = if (extras.monthAverageValue != null)
                    LimitLine.LimitLabelPosition.RIGHT_BOTTOM
                else LimitLine.LimitLabelPosition.RIGHT_TOP
                ColourHandler.getColourObject("Red,500").let {
                    lineColor = it
                    textColor = it
                }
            }
        )

        if (extras.monthAverageValue != null) addLimitLine(
            LimitLine(
                extras.monthAverageValue, extras.monthAverageText
            ).apply {
                lineWidth = 1f
                labelPosition = LimitLine.LimitLabelPosition.RIGHT_TOP
                ColourHandler.getColourObject("Red,500").let {
                    lineColor = it
                    textColor = it
                }
            }
        )
    }

    data = lineData
    notifyDataSetChanged()
    post { invalidate() }

    animateY(500, Easing.EaseOutQuart)
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