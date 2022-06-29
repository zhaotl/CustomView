package com.ztl.customview.ui.view

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import com.ztl.customview.utils.dp
import kotlin.math.min

class RadarGradientView : View {

    private var mWidth = 0
    private var mHeight = 0

    private val pots = floatArrayOf(0.05f, 0.1f, 0.15f, 0.2f, 0.25f)
    private var scanShader: SweepGradient? = null
    private val mMatrix by lazy {
        Matrix()
    } // 旋转需要的矩阵

    private val scanSpeed = 5f // 扫描速度
    private var scanAngle = 0f // 扫描旋转的角度

    private val mPaintCircle by lazy {
        Paint().apply {
            style = Paint.Style.STROKE
            strokeWidth = 1f.dp
            alpha = 100
            isAntiAlias = true
            color = Color.parseColor("#B0C4DE")
        }
    }

    private val mPaintRadar by lazy {
        Paint().apply {
            style = Paint.Style.FILL_AND_STROKE
            isAntiAlias = true
        }
    }

    private val runnable: Runnable = object : Runnable {
        override fun run() {
            scanAngle = (scanAngle + scanSpeed) % 360 // 旋转角度 对360取余
            mMatrix.postRotate(scanSpeed, mWidth / 2f, mHeight / 2f) // 旋转矩阵
            invalidate() // 通知view重绘
            postDelayed(this, 50) // 调用自身 重复绘制
        }
    }

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) :
            super(context, attrs, defStyleAttr)

    init {
        post(runnable)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        mWidth = min(measuredWidth, measuredHeight)
        mHeight = mWidth
        scanShader = SweepGradient(
            mWidth / 2f,
            mHeight / 2f,
            intArrayOf(Color.TRANSPARENT, Color.parseColor("#84B5CA")),
            null
        )

    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        pots.forEach { pot ->
            canvas?.drawCircle(mWidth / 2f, mHeight / 2f, mWidth * pot, mPaintCircle)
        }

        canvas?.save()
        mPaintRadar.shader = scanShader
        canvas?.concat(mMatrix)
        canvas?.drawCircle(mWidth / 2f, mHeight / 2f, mWidth * pots[4], mPaintRadar)
        canvas?.restore()
    }

}