package com.ztl.customview.ui.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

class BezierView: View {

    constructor(context: Context): super(context)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int): super(context, attrs, defStyleAttr)
    constructor(context: Context, attrs: AttributeSet): this(context, attrs, -1)

    private val startX = 160f
    private val startY = 330f
    private val endX = 550f
    private val endY = 350f
    private var controlX = 200f
    private var controlY = 60f

    private val mPaint by lazy {
        Paint().apply {
            isAntiAlias = true
            strokeWidth = 4f
            style = Paint.Style.STROKE
            color = Color.BLACK
        }
    }

    private val mPath by lazy {
        Path()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        mPath.reset()
        mPath.moveTo(startX, startY)
        mPath.quadTo(controlX, controlY, endX, endY)
        canvas?.drawPath(mPath, mPaint)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when(event?.action) {
            MotionEvent.ACTION_MOVE -> {
                controlX = event.x
                controlY = event.y
                invalidate()
            }
        }
        return true
    }
}