package com.ztl.customview.ui.view.chart

import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator

open class BaseView : View {

    protected var mWidth = 0f
    protected var mHeight = 0f

    protected var scale = 1f
    protected val animator by lazy {
        ValueAnimator.ofFloat(0.2f, 1f).apply {
            interpolator = LinearInterpolator()
            duration = 2000
            addUpdateListener {
                scale = it.animatedValue as Float
                postInvalidate()
            }
        }
    }

    constructor(context: Context) : super(context)

    constructor(context: Context, attributeSet: AttributeSet) : this(context, attributeSet, 0)

    constructor(context: Context, attrs: AttributeSet, defStyleRes: Int)
            : super(context, attrs, defStyleRes)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int)
            : super(context, attrs, defStyleAttr, defStyleRes)

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        mWidth = MeasureSpec.getSize(widthMeasureSpec).toFloat()
        mHeight = MeasureSpec.getSize(heightMeasureSpec).toFloat()
    }
}