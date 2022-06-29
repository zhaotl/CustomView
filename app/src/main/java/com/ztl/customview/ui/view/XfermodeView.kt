package com.ztl.customview.ui.view

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View

class XfermodeView : View {

    var mItemSize = 0f
    var mItemHorizontalOffset = 0f
    var mItemVerticalOffset = 0f
    var mCircleRadius = 0f
    var mRectSize = 0f
    var mCircleColor = -0x33bc //黄色
    var mRectColor = -0x995501 //蓝色
    var mTextSize = 25f

    companion object {
        private val sLabels = arrayOf<String>(
            "Clear", "Src", "Dst", "SrcOver",
            "DstOver", "SrcIn", "DstIn", "SrcOut",
            "DstOut", "SrcATop", "DstATop", "Xor",
            "Darken", "Lighten", "Multiply", "Screen"
        )
    }

    private val xfermode: Array<PorterDuffXfermode> by lazy {
        arrayOf(
            PorterDuffXfermode(PorterDuff.Mode.CLEAR),
            PorterDuffXfermode(PorterDuff.Mode.SRC),
            PorterDuffXfermode(PorterDuff.Mode.DST),
            PorterDuffXfermode(PorterDuff.Mode.SRC_OVER),
            PorterDuffXfermode(PorterDuff.Mode.DST_OVER),
            PorterDuffXfermode(PorterDuff.Mode.SRC_IN),
            PorterDuffXfermode(PorterDuff.Mode.DST_IN),
            PorterDuffXfermode(PorterDuff.Mode.SRC_OUT),
            PorterDuffXfermode(PorterDuff.Mode.DST_OUT),
            PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP),
            PorterDuffXfermode(PorterDuff.Mode.DST_ATOP),
            PorterDuffXfermode(PorterDuff.Mode.XOR),
            PorterDuffXfermode(PorterDuff.Mode.DARKEN),
            PorterDuffXfermode(PorterDuff.Mode.LIGHTEN),
            PorterDuffXfermode(PorterDuff.Mode.MULTIPLY),
            PorterDuffXfermode(PorterDuff.Mode.SCREEN)
        )
    }

    private val mPaint by lazy {
        Paint().apply {
            isAntiAlias = true
            textSize = mTextSize
            textAlign = Paint.Align.CENTER
            strokeWidth = 2f
        }
    }

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int)
            : super(context, attrs, defStyleAttr)

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mItemSize = w / 4.5f
        mItemHorizontalOffset = mItemSize / 6f
        mItemVerticalOffset = mItemSize * 0.426f
        mCircleRadius = mItemSize / 3
        mRectSize = mItemSize * 0.6f
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.let {
            val canvasWidth = it.width.toFloat()
            val canvasHeight = it.height.toFloat()
            for (row in 0 until 4) {
                for (column in 0 until 4) {
                    it.save()
                    val cnt =
                        it.saveLayer(0f, 0f, canvasWidth, canvasHeight, null)
                    mPaint.xfermode = null
                    val index = row * 4 + column
                    val translateX = (mItemSize + mItemHorizontalOffset) * column
                    val translateY = (mItemSize + mItemVerticalOffset) * row
                    it.translate(translateX, translateY)

                    // 画文字
                    mPaint.color = Color.BLACK
                    val textXOffset = mItemSize / 2
                    val textYOffset = mTextSize + (mItemVerticalOffset - mTextSize) / 2
                    it.drawText(sLabels[index], textXOffset, textYOffset, mPaint)
                    it.translate(0f, mItemVerticalOffset)

                    // 画边框
                    mPaint.style = Paint.Style.STROKE
                    it.drawRect(2f, 2f, mItemSize - 2f, mItemSize - 2, mPaint)

                    // 画目标图（圆）
                    mPaint.style = Paint.Style.FILL
                    mPaint.color = mCircleColor
                    it.drawCircle(mCircleRadius + 3, mCircleRadius + 3, mCircleRadius, mPaint)

                    // 画源图（矩形）
                    mPaint.xfermode = xfermode[index]
                    mPaint.color = mRectColor
                    it.drawRect(
                        mCircleRadius, mCircleRadius,
                        mCircleRadius + mRectSize, mCircleRadius + mRectSize, mPaint
                    )

                    it.restoreToCount(cnt)
                }
            }
        }

    }
}