package com.ztl.customview.ui.view

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View

class LayerView(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
    View(context, attrs, defStyleAttr) {

    private val mPaint by lazy {
        Paint().apply {
            isAntiAlias = true
        }
    }

    constructor(context: Context): this(context, null, 0)

    constructor(context: Context, attrs: AttributeSet?): this(context, attrs, 0)

    override fun onDraw(canvas: Canvas?) {
        mPaint.color = Color.RED
        canvas?.let {
            it.drawColor(Color.WHITE)
            it.translate(10f, 10f)
            it.drawCircle(75f, 75f, 75f, mPaint)
            val cnt = it.saveLayerAlpha(0f, 0f, 200f, 200f, 0x88)
//            val cnt = it.saveLayer(0f, 0f, 200f, 200f, null)
            mPaint.color = Color.BLUE
            it.drawCircle(125f, 125f, 75f, mPaint)
            canvas.restoreToCount(cnt)
        }

    }
}