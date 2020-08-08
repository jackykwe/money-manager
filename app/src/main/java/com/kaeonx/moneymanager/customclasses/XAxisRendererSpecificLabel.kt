package com.kaeonx.moneymanager.customclasses

import android.graphics.Canvas
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.renderer.XAxisRenderer
import com.github.mikephil.charting.utils.MPPointF
import com.github.mikephil.charting.utils.Transformer
import com.github.mikephil.charting.utils.Utils
import com.github.mikephil.charting.utils.ViewPortHandler

// Courtesy of and inspired by
// https://stackoverflow.com/a/44589204/7254995
// https://github.com/philippeauriach/MPAndroidChart/commits/custom-labels
// https://github.com/philippeauriach/MPAndroidChart/commit/93e12e48c275115197ea5cc7ec78f06c9a7ba1b6
class XAxisRendererSpecificLabel(
    viewPortHandler: ViewPortHandler,
    xAxis: XAxis,
    trans: Transformer,
    private val specificLabelPositions: List<Float>
) : XAxisRenderer(viewPortHandler, xAxis, trans) {

    override fun drawLabels(c: Canvas?, pos: Float, anchor: MPPointF?) {
        val labelRotationAngleDegrees = mXAxis.labelRotationAngle
//        val positions = FloatArray(mXAxis.mEntryCount * 2)
        val positions = FloatArray(specificLabelPositions.size * 2)

        for (i in positions.indices step 2) {
            positions[i] = specificLabelPositions[i / 2]
        }

        mTrans.pointValuesToPixel(positions)

        // Don't really understand this block fully, but fair enough - it works.
        for (i in positions.indices step 2) {
            var x = positions[i]
            if (mViewPortHandler.isInBoundsX(x)) {
//                val label = mXAxis.valueFormatter.getAxisLabel(mXAxis.mEntries[i / 2], mXAxis)
                val label =
                    mXAxis.valueFormatter.getAxisLabel(specificLabelPositions[i / 2], mXAxis)
                if (mXAxis.isAvoidFirstLastClippingEnabled) {

                    // avoid clipping of the last
                    if (i / 2 == mXAxis.mEntryCount - 1 && mXAxis.mEntryCount > 1) {
                        val width =
                            Utils.calcTextWidth(
                                mAxisLabelPaint,
                                label
                            ).toFloat()
                        if (width > mViewPortHandler.offsetRight() * 2
                            && x + width > mViewPortHandler.chartWidth
                        ) x -= width / 2

                        // avoid clipping of the first
                    } else if (i == 0) {
                        val width =
                            Utils.calcTextWidth(
                                mAxisLabelPaint,
                                label
                            ).toFloat()
                        x += width / 2
                    }
                }
                drawLabel(c, label, x, pos, anchor, labelRotationAngleDegrees)
            }
        }
    }

    override fun renderGridLines(c: Canvas) {

        if (!mXAxis.isDrawGridLinesEnabled || !mXAxis.isEnabled) return

        val clipRestoreCount = c.save()
        c.clipRect(gridClippingRect)

        if (mRenderGridLinesBuffer.size != specificLabelPositions.size * 2) {
            mRenderGridLinesBuffer = FloatArray(specificLabelPositions.size * 2)
        }
        val positions = mRenderGridLinesBuffer

        for (i in positions.indices step 2) {
            positions[i] = specificLabelPositions[i / 2]
            positions[i + 1] = specificLabelPositions[i / 2]
        }

        mTrans.pointValuesToPixel(positions)

        setupGridPaint()

        val gridLinePath = mRenderGridLinesPath
        gridLinePath.reset()

        for (i in positions.indices step 2) {
            drawGridLine(c, positions[i], positions[i + 1], gridLinePath)
        }

        c.restoreToCount(clipRestoreCount)
    }
}