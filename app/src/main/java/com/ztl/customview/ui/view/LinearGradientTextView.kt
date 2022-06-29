package com.ztl.customview.ui.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.LinearGradient
import android.graphics.Matrix
import android.graphics.Shader
import android.util.AttributeSet
import android.util.Log
import androidx.appcompat.widget.AppCompatTextView

class LinearGradientTextView : AppCompatTextView {

    private var deltax = 20
    private var mTranslate = 0f
    private var mLinearGradient: LinearGradient? = null
    private val mMatrix by lazy {
        Matrix()
    }

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) :
            super(context, attrs, defStyleAttr)

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        val textWidth = paint.measureText(text.toString())
        val gradientSize = (textWidth / text.toString().length) * 5
        Log.d("tag", "gradientSize === $gradientSize")
        mLinearGradient = LinearGradient(
            -gradientSize, 0f, 0f, 0f,
            intArrayOf(0x22ffffff, 0xffffffff.toInt(), 0x22ffffff), null,
            Shader.TileMode.CLAMP
        )
        paint.shader = mLinearGradient
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        val textWidth = paint.measureText(text.toString())
        mTranslate += deltax
        if (mTranslate > textWidth + 1 || mTranslate < 1) {
            deltax = -deltax
        }
        Log.d("tag", "mTranslate === $mTranslate")
        mMatrix.reset()
        mMatrix.postTranslate(mTranslate, 0f)
        mLinearGradient?.setLocalMatrix(mMatrix)
        postInvalidateDelayed(80)
    }
}