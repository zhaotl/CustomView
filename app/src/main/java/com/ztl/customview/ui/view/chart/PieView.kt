package com.ztl.customview.ui.view.chart

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.util.Log
import com.ztl.customview.utils.dp
import com.ztl.customview.utils.sp
import com.ztl.myndkdemo.model.PieData
import kotlin.math.floor
import kotlin.math.min

class PieView : BaseView {

    private var mRadius = 0f
    private var mRatios: MutableList<Float>? = null
    private var mDescriptions: MutableList<String>? = null
    private var mArcColors: MutableList<Int>? = null
    private var arcRect = RectF()
    private var mCircleWidth = 0f

    private val mPaint by lazy {
        Paint().apply {
            isAntiAlias = true
            style = Paint.Style.FILL
            strokeWidth = 2f.dp
            textSize = 12f.sp
        }
    }

    constructor(context: Context) : this(context, null, 0)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs!!,
        defStyleAttr
    )

    init {
        mRatios = mutableListOf()
        mDescriptions = mutableListOf()
        mArcColors = mutableListOf()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        mCircleWidth = min(measuredWidth.toFloat(), measuredHeight.toFloat())
        Log.d("tag", "~~ circle width = $mCircleWidth")
        mRadius = mCircleWidth * 0.3f
        arcRect.set(
            mCircleWidth / 2f - mRadius,
            mCircleWidth / 2f - mRadius,
            mCircleWidth / 2f + mRadius,
            mCircleWidth / 2f + mRadius
        )
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.let {
            // 画圆弧
            drawArc(canvas)
            // 画文字
            drawDescription(canvas)
        }
    }

    private fun drawArc(canvas: Canvas) {
        val drawArc = 360 * scale
        mRatios?.forEachIndexed { index, fl ->
            Log.d("tag", "draw ~~~~~~ $fl")
            Log.d("tag", "rect ~~~~~~ $arcRect")
            mArcColors?.get(index)?.let {
                mPaint.color = it
            }
            val ratioSum = getRatioSum(index)
            Log.d("tag", "ratioSum ~~~~~~ $ratioSum")
            canvas.drawArc(
                arcRect,
                ratioSum * drawArc,
                fl * drawArc, true, mPaint
            )
        }

    }

    private fun drawDescription(canvas: Canvas) {
        mDescriptions?.forEachIndexed { index, s ->
            mArcColors?.get(index)?.let {
                mPaint.color = it
            }

            // 每进行一次绘制，需要保存一次
            // 因为对整个布局进行了旋转
            canvas.save()
            canvas.translate(measuredWidth / 2f, mCircleWidth / 2f)
            canvas.rotate(getRatioHalfSumDegrees(index))
            canvas.drawLine(mRadius, 0f, mRadius + 32f.dp, 0f, mPaint)
            drawDictateLines(canvas, index)
            canvas.restore()
        }
    }

    private fun drawDictateLines(canvas: Canvas, i: Int) {
        canvas.save()
        canvas.translate(mRadius + 32f.dp, 0f)
        var halfSum = getRatioHalfSumDegrees(i)
        var isLeft = false
        if (halfSum > 90 && halfSum <= 270) {
            halfSum += 180
            isLeft = true
        }
        canvas.rotate(-halfSum)
        canvas.drawLine(0f, 0f, 16f.dp, 0f, mPaint)
        mArcColors?.get(i)?.let {
            mPaint.color = it
        }
        if (isLeft) {
            canvas.save()
            canvas.rotate(-180f)
            val textWidth = mPaint.measureText(mDescriptions!![i])
            canvas.drawText(
                mDescriptions!![i],
                -(18f.dp + textWidth),
                (-4f).dp,
                mPaint
            )
            canvas.drawText(
                floor(mRatios!![i] * scale * 100).toString() + "%",
                -(18f.dp + textWidth),
                8f.dp,
                mPaint
            )
            canvas.restore()
        } else {
            canvas.drawText(
                mDescriptions!![i],
                18f.dp,
                (-4f).dp,
                mPaint
            )
            canvas.drawText(
                floor(mRatios!![i] * scale * 100).toString() + "%",
                18f.dp,
                8f.dp,
                mPaint
            )
        }


        canvas.restore()
    }

    private fun getRatioSum(index: Int): Float {
        var sum = 0f
        for (i in 0 until index) {
            sum += mRatios?.get(i) ?: 0f
        }

        return sum
    }

    private fun getRatioHalfSumDegrees(j: Int): Float {
        var sum = getRatioSum(j)
        sum += (mRatios?.get(j) ?: 0f) / 2f
        Log.d("tag", "sum * 360 = ${sum * 360}")
        return sum * 360
    }

    private fun transformData(datas: List<Float>): List<Float> {
        val sum = datas.sum()
        return datas.map {
            it / sum
        }
    }

    fun setData(pieDatas: List<PieData>?) {
        val rios = mutableListOf<Float>()
        pieDatas?.forEach { pieData ->
            rios.add(pieData.ratio)
            mDescriptions?.add(pieData.description)
            mArcColors?.add(pieData.color)
        }

        mRatios?.addAll(transformData(rios))
    }
}