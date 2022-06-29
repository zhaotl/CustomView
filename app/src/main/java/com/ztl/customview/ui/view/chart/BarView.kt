package com.ztl.customview.ui.view.chart

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import com.ztl.customview.model.BarData
import com.ztl.customview.utils.dp
import com.ztl.customview.utils.sp
import kotlin.math.ceil
import kotlin.math.min

class BarView : BaseView, View.OnTouchListener {

    private var mData: MutableList<Float>? = null
    private var mDescription: MutableList<String>? = null

    // 数据显示数量
    private var mShowNum = 6
    private var mMinScrollShowNum = 6

    // 单个Bar的宽度
    private var mBarSingleWidth = 0f
    private var mBarMaxHeight = 0f
    private var mBarMaxWidth = 0f
    private var mBarBlankSize = 0f
    private val mBarCornerSize = 20f

    private var mHeightBlankSize = 0f
    private var mTextHeight = 0f

    private val mTextBounds = Rect()

    private var mMaxData = 0f

    // 渐变色
    private var shader: LinearGradient? = null

    // 手势动作
    private var detector: GestureDetector? = null

    private var offset = 0f

    // 外部设定变量
    private var mColor: Int = Color.RED

    private val mPaint by lazy {
        Paint().apply {
            color = Color.BLACK
            isAntiAlias = true
            style = Paint.Style.FILL
            textSize = 12f.sp
            strokeWidth = 1f.dp
        }
    }

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : this(context, attrs, 0)

    constructor(
        context: Context,
        attrs: AttributeSet,
        defStyleAttr: Int
    ) : super(context, attrs, defStyleAttr)

    init {
        mData = mutableListOf()
        mDescription = mutableListOf()

        detector = GestureDetector(context, BarGesture())
        setOnTouchListener(this)
        offset = 32f.dp

        // 用于文字的测量
        val fontMetrics = mPaint.fontMetrics
        mHeightBlankSize = (offset - (fontMetrics.bottom - fontMetrics.top)) / 2f
        mTextHeight = (offset + (fontMetrics.bottom - fontMetrics.top)) / 2f
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        if (mBarBlankSize == 0f) {
            mBarMaxWidth = mWidth
            mBarSingleWidth = mWidth / min(mMinScrollShowNum, mShowNum)
            mBarMaxHeight = mHeight - 2 * offset
            mBarBlankSize = (mBarSingleWidth / 4f).dp
        }

        if (shader == null) {
            shader = LinearGradient(
                0f, 0f,
                mBarBlankSize * 2f,
                mBarMaxHeight,
                Color.WHITE,
                mColor,
                Shader.TileMode.CLAMP
            )
        }
    }

    // 绘制柱状图，绘制文字
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        mData?.forEachIndexed { index, data ->
            val height = (mMaxData - data) / mMaxData * mBarMaxHeight
            mPaint.shader = null
            canvas?.let {
                drawValue(it, data, height)
                drawDescription(canvas, index)
                mPaint.shader = shader
                drawBar(canvas, height)
            }

            if (index != mShowNum - 1) {
                canvas?.translate(mBarSingleWidth, 0f)
            }
        }
    }

    private fun drawValue(canvas: Canvas, value: Float, height: Float) {
        val textWidth = mPaint.measureText(value.toString())
        mPaint.getTextBounds(value.toString(), 0, value.toString().length, mTextBounds)

        val bondsWidth = mTextBounds.right - mTextBounds.left
//        Log.d("tag", "~~!textWidth = $textWidth")
//        Log.d("tag", "~~!bondsWidth = $bondsWidth")
        var bonds = mBarSingleWidth / 2f - (mTextBounds.right - mTextBounds.left) / 2f
        val x = (mBarSingleWidth - textWidth) / 2f
        bonds = x
        mPaint.textSkewX = -0.25f
        canvas.drawText(value.toString(), bonds, mTextHeight + height, mPaint)
    }

    private fun drawDescription(canvas: Canvas, index: Int) {
        val description = mDescription?.get(index) ?: ""
        val textWidth = mPaint.measureText(description)
        var blankSize = (mBarSingleWidth - (mTextBounds.right - mTextBounds.left)) / 2
        val x = (mBarSingleWidth - textWidth) / 2f
        blankSize = x
        canvas.drawText(description, blankSize, mTextHeight + mBarMaxHeight + offset, mPaint)
    }

    private fun drawBar(canvas: Canvas, height: Float) {
        canvas.drawRoundRect(
            mBarBlankSize,
            offset + mBarMaxHeight - scale * (mBarMaxHeight - height),
            mBarSingleWidth - mBarBlankSize,
            mBarMaxHeight + offset,
            mBarCornerSize,
            mBarCornerSize,
            mPaint
        )
    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        Log.d("tag", "barView onTouch ${event?.action}")
        parent.requestDisallowInterceptTouchEvent(true)
        return detector?.onTouchEvent(event) ?: false
    }

    fun setData(datas: List<BarData>) {
        datas.forEach { data ->
            mData?.add(data.value)
            mDescription?.add(data.description)
            if (data.value > mMaxData) {
                mMaxData = data.value
            }
        }

        mShowNum = datas.size
        mMaxData = ceil(mMaxData / 40) * 40
        animator?.start()
    }

    inner class BarGesture : GestureDetector.SimpleOnGestureListener() {

        override fun onDown(e: MotionEvent?): Boolean {
            return true
        }

        override fun onScroll(
            e1: MotionEvent?,
            e2: MotionEvent?,
            distanceX: Float,
            distanceY: Float
        ): Boolean {
            if (mMinScrollShowNum >= mShowNum) return false

            val position = scrollX.toFloat()
            Log.d("tag", "barView position = $position")
            Log.d("tag", "barView distanceX = $distanceX")
            Log.d("tag", "barView mBarMaxWidth = $mBarMaxWidth")
            if (distanceX >= 0) {
                if (position <= mBarMaxWidth) {
                    parent.requestDisallowInterceptTouchEvent(true)
                    scrollBy(distanceX.toInt(), 0)
                } else {
                    parent.requestDisallowInterceptTouchEvent(false)
                }
            } else {
                if (distanceX >= -1 * position) {
                    parent.requestDisallowInterceptTouchEvent(true)
                    scrollBy(distanceX.toInt(), 0)
                    return true
                } else {
                    parent.requestDisallowInterceptTouchEvent(false)
                }
            }
            return false
        }
    }
}