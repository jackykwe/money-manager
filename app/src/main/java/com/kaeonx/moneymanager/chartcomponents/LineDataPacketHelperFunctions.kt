package com.kaeonx.moneymanager.chartcomponents

import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.kaeonx.moneymanager.handlers.CalendarHandler
import com.kaeonx.moneymanager.handlers.CurrencyHandler
import com.kaeonx.moneymanager.userrepository.domain.Transaction
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.*

/**
 * There is no need to clone for `calendarStart`. It is handled properly internally.
 * @param list The list of transactions to iterate through. **Important:** Ensure that every
 * Transaction's `homeAmount` has been calculated and initialised before passing the list in to
 * this function.
 * @param calendarStart The start calendar of the month/year.
 * @param numberOfDays The number of days in the period to iterate through transactions.
 * @param numberOfMonths The number of months in the period to iterate through transactions.
 * @param colourInt Use `ColourHandler.getColourObject()` to obtain this Int. It's the colour of
 * the line graph.
 * @param rangeAmount The sum of the homeAmounts of all transactions in `list`
 * @param showCurrency Whether to show currency in Line Chart
 * @param homeCurrency The 3 character string representing the home currency
 */
internal fun generateLineChartPacket(
    list: List<Transaction>,
    calendarStart: Calendar,
    numberOfDays: Int,
    numberOfMonths: Int,
    colourInt: Int,
    rangeAmount: BigDecimal,
    showCurrency: Boolean,
    homeCurrency: String
): LineChartPacket {
    val dayAmountMap = mutableMapOf<Float, BigDecimal>()
    for (i in 1..numberOfDays) {
        dayAmountMap[i.toFloat()] = BigDecimal.ZERO
    }

    list.forEach {
        val dayNumber = CalendarHandler.calculateDayNumberWRTStartCalendar(
            it.timestamp, calendarStart
        ).toFloat()
        dayAmountMap.replace(dayNumber, dayAmountMap[dayNumber]!!.plus(it.homeAmount))
    }
    val dataSet =
        LineDataSet(
            dayAmountMap.map { Entry(it.key, it.value.toFloat()) },
            null
        ).apply {
            setDrawCircles(false)
            color = colourInt
            lineWidth = 1f
            mode = LineDataSet.Mode.HORIZONTAL_BEZIER
            cubicIntensity = 1f
            axisDependency = YAxis.AxisDependency.LEFT
        }

    val dayAverageBD = rangeAmount.divide(BigDecimal(numberOfDays), 2, RoundingMode.HALF_UP)
    val monthAverageBD = if (numberOfMonths > 1) {
        rangeAmount.divide(
            BigDecimal(numberOfMonths),
            2,
            RoundingMode.HALF_UP
        )
    } else null
    return LineChartPacket(
        lineData = LineData(dataSet).apply {
            setDrawValues(false)
        },
        lowerLimitLineValue = dayAverageBD.toFloat(),
        lowerLimitLineText = "Daily Average: " +
                (if (showCurrency) "$homeCurrency " else "") +
                CurrencyHandler.displayAmount(dayAverageBD),
        upperLimitLineValue = monthAverageBD?.toFloat(),
        upperLimitLineText = monthAverageBD?.let {
            "Monthly Average: " +
                    (if (showCurrency) "$homeCurrency " else "") +
                    CurrencyHandler.displayAmount(it)
        },
        xAxisLabelMap = generateXAxisLabelMap(
            numberOfMonths,
            calendarStart.clone() as Calendar
        )
    )
}

/**
 * You **must** clone `calendarStart`.
 */
private fun generateXAxisLabelMap(
    numberOfMonths: Int,
    calendarStart: Calendar
): Map<Float, String> {
    return when (numberOfMonths) {
        1 -> mapOf(
            1f to "1",
            5f to "5",
            10f to "10",
            15f to "15",
            20f to "20",
            25f to "25",
            30f to "30"
        )
        12 -> {
            val result = mutableMapOf<Float, String>()
            do {
                result[calendarStart.get(Calendar.DAY_OF_YEAR).toFloat()] =
                    CalendarHandler.getFormattedString(calendarStart.clone() as Calendar, "MMM")
                calendarStart.add(Calendar.MONTH, 1)
            } while (calendarStart.get(Calendar.MONTH) != 0)
            result.toMap()
        }
        else -> throw IllegalStateException("Variable ranges are not implemented yet")
    }
}