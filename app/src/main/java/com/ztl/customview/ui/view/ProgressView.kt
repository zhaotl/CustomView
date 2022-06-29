package com.ztl.customview.ui.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import com.ztl.customview.R
import com.ztl.customview.utils.dp
import com.ztl.customview.utils.sp
import kotlin.math.max
import kotlin.math.min

class ProgressView constructor(
    mContext: Context,
    attrs: AttributeSet?,
    defStyleAttr: Int
) : View(mContext, attrs, defStyleAttr) {

    companion object {
        private val default_text_color = Color.rgb(66, 145, 241)
        private val default_reached_color = Color.rgb(66, 145, 241)
        private val default_unreached_color = Color.rgb(204, 204, 204)
        private val default_reached_height = 1.5f.dp
        private val default_unreached_height = 1.0f.dp
        private val default_text_size = 10f.sp
        private val default_text_offset = 3.0f.dp
    }

    private var progressMax = 100
        set(value) {
            if (value > 0) {
                field = value
                invalidate()
            }
        }

    /** 当前的 progress， 不能超过最大 progress */
    private var progressCurrent = 0
        set(value) {
            if (value in 0..progressMax) {
                field = value
                invalidate()
            }
        }

    /** 进度条颜色 */
    private var mReachedColor: Int = 0

    /** 未触及区域的颜色 */
    private var mUnReachedColor: Int = 0

    /** 进度文本颜色 */
    private var mTextColor: Int = 0
    private var mTextSize: Float = 0f

    /** 进度条高度 */
    private var mReachedHeight: Float = 0f

    /** 未触及区域高度 */
    private var mUnreachedHeight = 0f
    private var mTextOffset = 0f

    private var mDrawText = true
    private var mDrawUnreached = true
    private var mDrawReached = true

    private var prefix: String? = ""
        set(value) {
            value?.let {
                field = it
            } ?: let { field = "" }
        }

    private var suffix: String? = "%"
        set(value) {
            value?.let {
                field = it
            } ?: let { field = "" }
        }

    private var mText = ""
    private var mTextWidth = 0f
    private var mDrawTextStart = 0f
    private var mDrawTextEnd = 0f

    private val mReachedRectF by lazy {
        RectF(0f, 0f, 0f, 0f)
    }

    private val mUnreachedRectF by lazy {
        RectF(0f, 0f, 0f, 0f)
    }

    private val mReachedBarPaint by lazy {
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = mReachedColor
        }
    }

    private val mUnReachedPaint by lazy {
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = mUnReachedColor
        }
    }

    private val mTextPaint by lazy {
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = mTextColor
            textSize = mTextSize
        }
    }

    constructor(context: Context) : this(context, null, 0)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    init {
        attrs?.let {
            val a =
                context.theme.obtainStyledAttributes(it, R.styleable.ProgressView, defStyleAttr, 0)

            mReachedColor =
                a.getColor(R.styleable.ProgressView_progress_reached_color, default_reached_color)
            mUnReachedColor = a.getColor(
                R.styleable.ProgressView_progress_unreached_color,
                default_unreached_color
            )
            mTextColor =
                a.getColor(R.styleable.ProgressView_progress_text_color, default_text_color)
            mTextSize =
                a.getDimension(R.styleable.ProgressView_progress_text_size, default_text_size)
            mReachedHeight = a.getDimension(
                R.styleable.ProgressView_progress_reached_height,
                default_reached_height
            )
            mUnreachedHeight = a.getDimension(
                R.styleable.ProgressView_progress_unreached_height,
                default_unreached_height
            )
            mTextOffset =
                a.getDimension(R.styleable.ProgressView_progress_text_offset, default_text_offset)

            val textVisible = a.getInt(R.styleable.ProgressView_progress_text_visible, 0)
            if (textVisible != 0) {
                mDrawText = false
            }
            progressCurrent = a.getInt(R.styleable.ProgressView_progress_current, 0)
            progressMax = a.getInt(R.styleable.ProgressView_progress_max, 100)
            a.recycle()
        }

    }

    override fun getSuggestedMinimumWidth(): Int {
        return mTextSize.toInt()
    }

    override fun getSuggestedMinimumHeight(): Int {
        return max(mTextSize.toInt(), max(mReachedHeight, mUnreachedHeight).toInt())
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        setMeasuredDimension(
            measure(widthMeasureSpec, true),
            measure(heightMeasureSpec, false)
        )
    }

    private fun measure(measureSpec: Int, isWidth: Boolean): Int {
        val mode = MeasureSpec.getMode(measureSpec)
        val size = MeasureSpec.getSize(measureSpec)

        val padding = if (isWidth) paddingStart + paddingEnd else paddingTop + paddingBottom

        return when (mode) {
            MeasureSpec.EXACTLY -> {
                size
            }

            else -> {
                var r = if (isWidth) suggestedMinimumWidth else suggestedMinimumHeight
                r += padding
                if (mode == MeasureSpec.AT_MOST) {
                    max(r, size)
                } else {
                    min(r, size)
                }
            }
        }
    }

    override fun onDraw(canvas: Canvas?) {

        if (mDrawText) {
            calculateDrawRectF()
        } else {
            calculateDrawRectFWithoutProgressText()
        }

        if (mDrawReached) {
            canvas?.drawRect(mReachedRectF, mReachedBarPaint)
        }

        if (mDrawUnreached) {
            canvas?.drawRect(mUnreachedRectF, mUnReachedPaint)
        }

        if (mDrawText) {
            canvas?.drawText(mText, mDrawTextStart, mDrawTextEnd, mTextPaint)
        }
    }

    private fun calculateDrawRectF() {
        mText = "$prefix$progressCurrent$suffix"
        mTextWidth = mTextPaint.measureText(mText)

        if (progressCurrent == 0) {
            mDrawReached = false
            mDrawTextStart = paddingLeft.toFloat()
        } else {
            mDrawReached = true
            mReachedRectF.also {
                it.left = paddingLeft.toFloat()
                it.top = height / 2f - mReachedHeight / 2f
                it.right =
                    (width - paddingLeft - paddingRight) / (progressMax * 1f) * progressCurrent - mTextOffset + paddingLeft
                it.bottom = height / 2f + mReachedHeight / 2f
            }
            mDrawTextStart = mReachedRectF.right + mTextOffset
        }

        mDrawTextEnd = height / 2f - (mTextPaint.descent() + mTextPaint.ascent()) / 2f

        if (mDrawTextStart + mTextWidth >= width - paddingRight) {
            mDrawTextStart = width - paddingLeft - mTextWidth
            mReachedRectF.right = mDrawTextStart - mTextOffset
        }

        val unReachedStart = mDrawTextStart + mTextWidth + mTextOffset
        if (unReachedStart >= width - paddingRight) {
            mDrawUnreached = false
        } else {
            mDrawUnreached = true
            mUnreachedRectF.left = unReachedStart
            mUnreachedRectF.right = (width - paddingRight).toFloat()
            mUnreachedRectF.top = height / 2.0f - mUnreachedHeight / 2.0f
            mUnreachedRectF.bottom = height / 2.0f + mUnreachedHeight / 2.0f
        }
    }

    private fun calculateDrawRectFWithoutProgressText() {
        mReachedRectF.left = paddingLeft.toFloat()
        mReachedRectF.top = height / 2.0f - mReachedHeight / 2.0f
        mReachedRectF.right =
            (width - paddingLeft - paddingRight) / (progressMax * 1.0f) * progressCurrent + paddingLeft
        mReachedRectF.bottom = height / 2.0f + mReachedHeight / 2.0f
        mUnreachedRectF.left = mReachedRectF.right
        mUnreachedRectF.right = (width - paddingRight).toFloat()
        mUnreachedRectF.top = height / 2.0f + -mUnreachedHeight / 2.0f
        mUnreachedRectF.bottom = height / 2.0f + mUnreachedHeight / 2.0f
    }

    fun incrementProgressBy(by: Int) {
        if (by > 0) {
            progressCurrent += by
        }
    }

    fun setCompleteProgress() {
        progressCurrent = progressMax
    }
}