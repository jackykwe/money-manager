package com.kaeonx.moneymanager.chartcomponents

import com.github.mikephil.charting.data.PieData

// PieChartWithLegendPacket
data class PieChartWLPacket(
    val pieData: PieData,
    val pieChartCentreText: String?,
    val pieChartLegendLLData: List<PieChartLegendLLData>
)

sealed class PieChartLegendLLData {

    data class DetailCategoryPCLLD(
        val noDataFlag: Boolean,
        val colour: Int,
        val categoryName: String,
        val categoryPercent: String
    ) : PieChartLegendLLData()

    data class BudgetDetailPCLLD(
        val showFormsFromLeft: Int,
        val form0Colour: Int,
        val form1Colour: Int,
        val form2Colour: Int,
        val name: String,
        val showCurrency: Boolean,
        val currency: String,
        val amount: String
    ) : PieChartLegendLLData()

}

