package com.kaeonx.moneymanager.chartcomponents

import com.github.mikephil.charting.data.PieData

// PieChartWithLegendPacket
data class PieChartWLPacket(
    val pieData: PieData,
    val pieChartCentreText: String?,
    val pieChartLegendLLData: List<PieChartLegendLLData>
)

data class PieChartLegendLLData(
    val noDataFlag: Boolean,
    val colour: Int,
    val categoryName: String,
    val categoryPercent: String
)