package com.kaeonx.moneymanager.fragments.detail

import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.databinding.BindingAdapter
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.LineData
import com.kaeonx.moneymanager.customclasses.XAxisRendererSpecificLabel
import com.kaeonx.moneymanager.databinding.RvLlItemDetailCategoryBinding
import com.kaeonx.moneymanager.handlers.ColourHandler

////////////////////////////////////////////////////////////////////////////////
/**
 * rv_item_detail_category_summary
 */
////////////////////////////////////////////////////////////////////////////////
@BindingAdapter("categorySummaryLC_lineData")
fun LineChart.setTypeSummaryPCAdapter(lineData: LineData) {
    if (legend.isEnabled) {
        setTouchEnabled(false)
        setNoDataText("Please report this bug.")
        setDrawMarkers(false)
        description.isEnabled = false


        setXAxisRenderer(
            XAxisRendererSpecificLabel(
                viewPortHandler,
                xAxis,
                getTransformer(YAxis.AxisDependency.LEFT),
                listOf(1f, 5f, 10f, 15f, 20f)
            )
        )


        xAxis.apply {
            position = XAxis.XAxisPosition.BOTTOM
            spaceMin = 1f  // 1 extra day at the start
            spaceMax = 1f  // 1 extra day at the end
            setDrawGridLines(true)

//            setLabelCount(6, false)
//            isGranularityEnabled = true
//            granularity = 1f
            setDrawLabels(true)

//            valueFormatter = object : ValueFormatter() {
//                override fun getAxisLabel(value: Float, axis: AxisBase?): String {
//                    return "lanjiaocb"
//                }
//            }
        }

        axisLeft.apply {
            axisMinimum = 0f
            setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART)
            setDrawLimitLinesBehindData(true)
            setDrawAxisLine(false)

            addLimitLine(
                LimitLine(
                    35f, "Average"
                ).apply {
                    labelPosition = LimitLine.LimitLabelPosition.RIGHT_TOP
                    lineColor = ColourHandler.getColourObject("Red,500")
                    lineWidth = 1f
                    textColor = ColourHandler.getColourObject("Red,500")
                }
            )
        }
        axisRight.isEnabled = false

        setViewPortOffsets(48f, 0f, 0f, 48f)  // remove padding
        legend.isEnabled = false
    }
    data = lineData
    notifyDataSetChanged()
    post { invalidate() }
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