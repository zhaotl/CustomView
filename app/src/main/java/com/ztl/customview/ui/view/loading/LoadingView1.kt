package com.ztl.customview.ui.view.loading

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.View
import com.ztl.customview.R
import kotlin.math.PI
import kotlin.math.atan2

class LoadingView1 : View {

    private val circlePaint by lazy {
        Paint().apply {
            isAntiAlias = true
            color = Color.RED
            strokeWidth = 10f
            style = Paint.Style.STROKE
        }
    }

    private val arrowPaint by lazy {
        Paint().apply {
            color = Color.DKGRAY
            strokeWidth = 2f
            style = Paint.Style.STROKE
        }
    }

    private val arrowBmp by lazy {
        val options = BitmapFactory.Options()
        options.inSampleSize = 8
        BitmapFactory.decodeResource(resources, R.drawable.arrow, options)
    }

    private val mPath by lazy {
        Path()
    }

    private val pos by lazy {
        floatArrayOf(0f, 0f)
    }

    private val tan by lazy {
        floatArrayOf(0f, 0f)
    }

    private val measure by lazy {
        PathMeasure()
    }

    private val mMatrix by lazy {
        Matrix()
    }

    private var viewWidth = 0
    private var viewHeight = 0

    // 记录当前的位置， 取值范围[0..1]映射path的整个长度
    private var currentValue = 0f

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : this(context, attrs, -1)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int)
            : super(context, attrs, defStyleAttr)

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        viewWidth = w
        viewHeight = h
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        currentValue += 0.01f
        if (currentValue >= 1f) {
            currentValue = 0f
        }

        mPath.reset()
        canvas?.translate(viewWidth / 2f, viewHeight / 2f)
        mPath.addCircle(0f, 0f, 200f, Path.Direction.CW)
        canvas?.drawPath(mPath, circlePaint)

        measure.setPath(mPath, false)
        measure.getPosTan(measure.length * currentValue, pos, tan)

        val degress = atan2(tan[1].toDouble(), tan[0].toDouble()) * 180 / PI
//        Log.d("tag", "degree = $degress")
        mMatrix.reset()
        mMatrix.postRotate(
            degress.toFloat(),
            arrowBmp.width / 2f,
            arrowBmp.height / 2f
        )
        mMatrix.postTranslate(pos[0] - arrowBmp.width / 2f, pos[1] - arrowBmp.height / 2f)
        canvas?.drawBitmap(arrowBmp, mMatrix, arrowPaint)

        invalidate()
    }
}