package com.ztl.customview.ui.view.loading

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import kotlin.math.abs

class LoadingView2 : View {

    private var viewWidth = 0
    private var viewHeight = 0

    private val circlePaint by lazy {
        Paint().apply {
            color = Color.DKGRAY
            strokeWidth = 10f
            style = Paint.Style.STROKE
        }
    }

    private val mPath by lazy {
        Path()
    }

    private val dstPath by lazy {
        Path()
    }

    private val pathMeasure by lazy {
        PathMeasure()
    }

    private var animatorValue = 0f
    private val valueAnimator by lazy {
        ValueAnimator.ofFloat(0f, 1f).apply {
            addUpdateListener {
                animatorValue = it.animatedValue as Float
                invalidate()
            }
            duration = 2000
            repeatCount = ValueAnimator.INFINITE
        }
    }

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : this(context, attrs, -1)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int)
            : super(context, attrs, defStyleAttr)

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        viewWidth = w
        viewHeight = h
        valueAnimator.start()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        canvas?.translate(viewWidth / 2f, viewHeight / 2f)
        mPath.reset()
        mPath.addCircle(0f, 0f, 200f, Path.Direction.CW)
//        canvas?.drawPath(mPath, circlePaint)

        dstPath.reset()
        pathMeasure.setPath(mPath, false)
        val stop = pathMeasure.length * animatorValue
        val start = stop - (0.5f - abs(animatorValue - 0.5f))*pathMeasure.length
        pathMeasure.getSegment(
            start,
            stop,
            dstPath,
            true
        )
        canvas?.drawPath(dstPath, circlePaint)
    }
}