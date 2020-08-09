package com.kaeonx.moneymanager.chartcomponents

import com.github.mikephil.charting.data.LineData

data class LineChartPacket(
    val lineData: LineData,
    val lowerLimitLineValue: Float,
    val lowerLimitLineText: String,
    val upperLimitLineValue: Float?,
    val upperLimitLineText: String?,
    val xAxisLabelMap: Map<Float, String>
)