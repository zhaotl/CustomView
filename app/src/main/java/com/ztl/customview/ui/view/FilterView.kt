package com.ztl.customview.ui.view

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import com.ztl.customview.R

class FilterView constructor(
    context: Context,
    attrs: AttributeSet?,
    defStyleAttr: Int
) :
    View(context, attrs, defStyleAttr) {

    private var mBitmap: Bitmap
    private var rectf: RectF
    private val mPaint by lazy {
        Paint().apply {
            color = Color.RED
        }
    }

    // 偏绿
    private val gColorMatrix by lazy {
        ColorMatrix(
            floatArrayOf(
                1f, 0f, 0f, 0f, 0f,
                0f, 1f, 0f, 0f, 100f,
                0f, 0f, 1f, 0f, 0f,
                0f, 0f, 0f, 1f, 0f
            )
        )
    }

    // 反相效果
    private val antiColorMatrix by lazy {
        ColorMatrix(
            floatArrayOf(
                -1f, 0f, 0f, 0f, 255f,
                0f, -1f, 0f, 0f, 255f,
                0f, 0f, -1f, 0f, 255f,
                0f, 0f, 0f, 1f, 0f
            )
        )
    }

    // 颜色加强效果（美颜）
    private val ehColorMatrix by lazy {
        ColorMatrix(
            floatArrayOf(
                1.2f, 0f, 0f, 0f, 0f,
                0f, 1.2f, 0f, 0f, 0f,
                0f, 0f, 1.2f, 0f, 0f,
                0f, 0f, 0f, 1.2f, 0f
            )
        )
    }

    // 黑白
    private val bwColorMatrix by lazy {
        ColorMatrix(
            floatArrayOf(
                0.213f, 0.715f, 0.072f, 0f, 0f,
                0.213f, 0.715f, 0.072f, 0f, 0f,
                0.213f, 0.715f, 0.072f, 0f, 0f,
                0f, 0f, 0f, 1f, 0f
            )
        )
    }

    // 发色效果 （绿色和蓝色交换）
    private val gbColorMatrix by lazy {
        ColorMatrix(
            floatArrayOf(
                1f, 0f, 0f, 0f, 0f,
                0f, 0f, 1f, 0f, 100f,
                0f, 1f, 0f, 0f, 0f,
                0f, 0f, 0f, 1f, 0f
            )
        )
    }

    // 复古效果
    private val oldColorMatrix by lazy {
        ColorMatrix(
            floatArrayOf(
                1 / 2f, 1 / 2f, 1 / 2f, 0f, 0f,
                1 / 3f, 1 / 3f, 1 / 3f, 0f, 0f,
                1 / 4f, 1 / 4f, 1 / 4f, 0f, 0f,
                0f, 0f, 0f, 1f, 0f
            )
        )
    }

    private val colorFilter: Array<ColorMatrixColorFilter> by lazy {
        arrayOf(
            ColorMatrixColorFilter(gColorMatrix), // 偏绿
            ColorMatrixColorFilter(antiColorMatrix), // 反相
            ColorMatrixColorFilter(ehColorMatrix), // 美颜
            ColorMatrixColorFilter(bwColorMatrix), // 黑白
            ColorMatrixColorFilter(gbColorMatrix), // 蓝绿色交换
            ColorMatrixColorFilter(oldColorMatrix), // 复古
        )
    }

    private var filter_type = -1

    constructor(context: Context) : this(context, null, 0)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    init {

        attrs?.let {
            val a = context.obtainStyledAttributes(it, R.styleable.FilterView, defStyleAttr, 0)
            filter_type = a.getInt(R.styleable.FilterView_filter_type, -1)
            a.recycle()
        }

//        mBitmap = BitmapFactory.decodeResource(resources, R.mipmap.icon2)
        mBitmap = BitmapFactory.decodeResource(resources, R.mipmap.icon7)
        rectf = RectF(
            200f, 0f,
            mBitmap.width.toFloat() + 200, mBitmap.height.toFloat()
        )
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        mPaint.reset()
        mPaint.color = Color.RED

        if (filter_type !=-1) {
            mPaint.colorFilter = colorFilter[filter_type]
            canvas?.drawBitmap(mBitmap, 200f, 0f, mPaint)
        } else {
            canvas?.drawBitmap(mBitmap, null, rectf, mPaint)
        }

//        for (index in 0..5) {
//            mPaint.colorFilter = colorFilter[index]
//            canvas?.drawBitmap(mBitmap, 200f, 80f + mBitmap.height * (index + 1), mPaint)
//        }

    }
}