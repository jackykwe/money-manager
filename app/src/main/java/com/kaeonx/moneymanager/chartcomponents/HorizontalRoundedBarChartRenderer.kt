package com.kaeonx.moneymanager.chartcomponents

import android.graphics.Canvas
import android.graphics.RectF
import com.github.mikephil.charting.animation.ChartAnimator
import com.github.mikephil.charting.interfaces.dataprovider.BarDataProvider
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.github.mikephil.charting.renderer.HorizontalBarChartRenderer
import com.github.mikephil.charting.utils.Utils
import com.github.mikephil.charting.utils.ViewPortHandler
import kotlin.math.ceil

// Courtesy of
// https://github.com/PhilJay/MPAndroidChart/issues/803
// https://github.com/WW-Digital/MPAndroidChart/commit/f940a527418aa0b695cd4f7fdf8b07922317cb17
internal class HorizontalRoundedBarChartRenderer(
    chart: BarDataProvider,
    animator: ChartAnimator,
    viewPortHandler: ViewPortHandler
) : HorizontalBarChartRenderer(chart, animator, viewPortHandler) {

//    override fun drawDataSet(c: Canvas?, dataSet: IBarDataSet?, index: Int) {
//        super.drawDataSet(c, dataSet, index)
//    }

    private val mBarShadowRectBuffer = RectF()

    override fun drawDataSet(c: Canvas, dataSet: IBarDataSet, index: Int) {
        val cornerDimens =
            c.height / 2f  // Courtesy of https://stackoverflow.com/a/37206613/7254995

        val trans = mChart.getTransformer(dataSet.axisDependency)
        mBarBorderPaint.color = dataSet.barBorderColor
        mBarBorderPaint.strokeWidth = Utils.convertDpToPixel(dataSet.barBorderWidth)
        val drawBorder = dataSet.barBorderWidth > 0f
        val phaseX = mAnimator.phaseX
        val phaseY = mAnimator.phaseY

        // draw the bar shadow before the values
        if (mChart.isDrawBarShadowEnabled) {
            mShadowPaint.color = dataSet.barShadowColor
            val barData = mChart.barData
            val barWidth = barData.barWidth
            val barWidthHalf = barWidth / 2.0f
            var x: Float
            var i = 0
            val count = ceil(dataSet.entryCount.toFloat() * phaseX.toDouble()).toInt()
                .coerceAtMost(dataSet.entryCount)
            while (i < count) {
                val e = dataSet.getEntryForIndex(i)
                x = e.x
                mBarShadowRectBuffer.top = x - barWidthHalf
                mBarShadowRectBuffer.bottom = x + barWidthHalf
                trans.rectValueToPixel(mBarShadowRectBuffer)
                if (!mViewPortHandler.isInBoundsTop(mBarShadowRectBuffer.bottom)) {
                    i++
                    continue
                }
                if (!mViewPortHandler.isInBoundsBottom(mBarShadowRectBuffer.top)) break
                mBarShadowRectBuffer.left = mViewPortHandler.contentLeft()
                mBarShadowRectBuffer.right = mViewPortHandler.contentRight()
//                c.drawRect(mBarShadowRectBuffer, mShadowPaint)
//                c.drawRect(  // I added this
//                    RectF(
//                        mBarShadowRectBuffer.left,
//                        mBarShadowRectBuffer.top,
//                        mBarShadowRectBuffer.right - cornerDimens,
//                        mBarShadowRectBuffer.bottom
//                    ),
//                    mShadowPaint
//                )
                c.drawRoundRect(mBarShadowRectBuffer, cornerDimens, cornerDimens, mShadowPaint)
                i++
            }
        }

        // initialize the buffer
        val buffer = mBarBuffers[index]
        buffer.setPhases(phaseX, phaseY)
        buffer.setDataSet(index)
        buffer.setInverted(mChart.isInverted(dataSet.axisDependency))
        buffer.setBarWidth(mChart.barData.barWidth)
        buffer.feed(dataSet)
        trans.pointValuesToPixel(buffer.buffer)
        val isSingleColor = dataSet.colors.size == 1
        if (isSingleColor) {
            mRenderPaint.color = dataSet.color
        }
        var j = 0
        while (j < buffer.size()) {
            if (!mViewPortHandler.isInBoundsTop(buffer.buffer[j + 3])) break
            if (!mViewPortHandler.isInBoundsBottom(buffer.buffer[j + 1])) {
                j += 4
                continue
            }
            if (!isSingleColor) {
                // Set the color for the currently drawn value. If the index
                // is out of bounds, reuse colors.
                mRenderPaint.color = dataSet.getColor(j / 4)
            }
//            c.drawRect(  // I modified this with - cornerDimens
//                buffer.buffer[j], buffer.buffer[j + 1], buffer.buffer[j + 2] - cornerDimens,
//                buffer.buffer[j + 3], mRenderPaint
//            )
            c.drawRoundRect(
                buffer.buffer[j], buffer.buffer[j + 1], buffer.buffer[j + 2],
                buffer.buffer[j + 3], cornerDimens, cornerDimens, mRenderPaint
            )
            if (drawBorder) {
//                c.drawRect(  // I modified this with - cornerDimens
//                    buffer.buffer[j], buffer.buffer[j + 1], buffer.buffer[j + 2] - cornerDimens,
//                    buffer.buffer[j + 3], mBarBorderPaint
//                )
                c.drawRoundRect(
                    buffer.buffer[j], buffer.buffer[j + 1], buffer.buffer[j + 2],
                    buffer.buffer[j + 3], cornerDimens, cornerDimens, mBarBorderPaint
                )
            }
            j += 4
        }
    }
}