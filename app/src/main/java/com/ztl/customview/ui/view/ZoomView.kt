package com.ztl.customview.ui.view

import android.content.Context
import android.graphics.*
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.OvalShape
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.ztl.customview.R

class ZoomView : View {

    companion object {
        // 放大倍数
        private const val factor = 2

        // 放大镜半径
        private const val radius = 80
    }

    // 原图
    private var mBitmap: Bitmap? = null

    // 放大后的图片
    private var mScaleBitmap: Bitmap? = null

    // 放大后的局部
    private val mShapeDrawable by lazy {
        ShapeDrawable(OvalShape())
    }

    private val mMatrix by lazy {
        Matrix()
    }

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    constructor(context: Context, attrs: AttributeSet) : this(context, attrs, 0)

    init {
        mBitmap = BitmapFactory.decodeResource(resources, R.mipmap.zoom)
        mScaleBitmap = mBitmap
        mScaleBitmap?.let {
            mScaleBitmap =
                Bitmap.createScaledBitmap(it, it.width * factor, it.height * factor, true)
        }
        mScaleBitmap?.let {
            val bitmapShader = BitmapShader(it, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
            mShapeDrawable.paint.shader = bitmapShader
            mShapeDrawable.setBounds(0, 0, radius * 2, radius * 2)
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        canvas?.run {
            mBitmap?.let {
                drawBitmap(it, 0f, 0f, null)
                mShapeDrawable.draw(this)
            }
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {

        val x: Float = event?.x ?: 0f
        val y: Float = event?.y ?: 0f

        mMatrix.postTranslate(radius - x * factor, radius - y * factor)
        mShapeDrawable.paint.shader.setLocalMatrix(mMatrix)
        mShapeDrawable.setBounds(
            x.toInt() - radius,
            y.toInt() - radius,
            x.toInt() + radius,
            y.toInt() + radius
        )
        invalidate()

        return true
    }

}