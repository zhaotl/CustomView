package com.ztl.customview.ui.view

import android.animation.PointFEvaluator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.animation.LinearInterpolator
import android.view.animation.OvershootInterpolator
import androidx.core.animation.addListener
import com.ztl.customview.R
import com.ztl.customview.utils.sp
import kotlin.math.hypot


class DragBubbleView(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
    View(context, attrs, defStyleAttr) {

    companion object {
        // 气泡默认状态-静止
        private const val bubble_state_default = 0

        // 气泡相连
        private const val bubble_state_connect = 1

        // 气泡分离
        private const val bubble_state_apart = 2

        // 汽包消失
        private const val bubble_state_dismiss = 3
    }

    private var bubbleState = bubble_state_default
    private var bubbleStillRadius = 20f
    private var bubbleMoveRadius = 20f
    private var bubbleRadius = 10f
    private var bubbleColor = Color.RED
    private var dist = 0f
    private var text: String = ""
    private var textSize = 12f.sp
    private var textColor = Color.WHITE

    private var maxDist = 0f
    private var moveOffset = 0f

    private val bubbleStillCenter: PointF by lazy {
        PointF()
    }

    private val bubbleMoveCenter by lazy {
        PointF()
    }

    private val textRect by lazy {
        Rect()
    }

    private val mPaint by lazy {
        Paint().apply {
            isAntiAlias = true
            color = bubbleColor
            style = Paint.Style.FILL
        }
    }

    private val textPaint by lazy {
        Paint().apply {
            isAntiAlias = true
            color = textColor
            textSize = this@DragBubbleView.textSize
        }
    }

    /* 气泡爆炸的bitmap数组 */
    private var burstBitmapsArray: Array<Bitmap>

    /* 是否在执行气泡爆炸动画 */
    private var isBurstAnimStart = false

    /* 爆炸绘制区域 */
    private val burstRect: Rect by lazy {
        Rect()
    }
    private val burstPaint: Paint by lazy {
        Paint().apply {
            isAntiAlias = true
            isFilterBitmap = true
        }
    }

    /* 气泡爆炸的图片id数组 */
    private val burstDrawablesArray = intArrayOf(
        R.drawable.bomb1,
        R.drawable.bomb2,
        R.drawable.bomb3,
        R.drawable.bomb4,
        R.drawable.bomb5
    )

    /* 当前气泡爆炸图片index */
    private var curDrawableIndex = 0

    private val bezierPath by lazy {
        Path()
    }

    constructor(context: Context) : this(context, null, -1)

    constructor(context: Context, attrs: AttributeSet) : this(context, attrs, -1)

    init {
        attrs?.let {
            val a = context.obtainStyledAttributes(it, R.styleable.DragBubbleView, defStyleAttr, 0)
            bubbleRadius = a.getDimension(R.styleable.DragBubbleView_bubble_radius, 10f)
            bubbleColor = a.getColor(R.styleable.DragBubbleView_bubble_color, bubbleColor)
            text = a.getString(R.styleable.DragBubbleView_bubble_text) ?: ""
            textSize = a.getDimension(R.styleable.DragBubbleView_bubble_textSize, 12f.sp)
            textColor = a.getColor(R.styleable.DragBubbleView_bubble_textColor, Color.WHITE)
            a.recycle()

            bubbleStillRadius = bubbleRadius
            bubbleMoveRadius = bubbleStillRadius

            maxDist = 8f * bubbleRadius
            moveOffset = maxDist / 4f
        }

        burstBitmapsArray = Array(burstDrawablesArray.size) { i ->
            BitmapFactory.decodeResource(resources, burstDrawablesArray[i])
        }
    }

    private fun initView(w: Int, h: Int) {
        bubbleStillCenter.set(w / 2f, h / 2f)
        bubbleMoveCenter.set(w / 2f, h / 2f)
        dist = hypot(
            bubbleMoveCenter.x - bubbleStillCenter.x,
            bubbleMoveCenter.y - bubbleStillCenter.y
        )
        bubbleState = bubble_state_default
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        initView(w, h)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.let {
            if (bubbleState != bubble_state_dismiss) {
                // 绘制拖拽的圆
                it.drawCircle(bubbleMoveCenter.x, bubbleMoveCenter.y, bubbleMoveRadius, mPaint)
                textPaint.getTextBounds(text, 0, text.length, textRect)
                it.drawText(
                    text, bubbleMoveCenter.x - textRect.width() / 2,
                    bubbleMoveCenter.y + textRect.height() / 2f, textPaint
                )
            }

            if (bubbleState == bubble_state_connect) {
                // 画静止气泡
                it.drawCircle(
                    bubbleStillCenter.x,
                    bubbleStillCenter.y, bubbleStillRadius, mPaint
                )

                // 画相连曲线
                val anchorX = (bubbleStillCenter.x + bubbleMoveCenter.x) / 2
                val anchorY = (bubbleStillCenter.y + bubbleMoveCenter.y) / 2

                val cosTheta = (bubbleMoveCenter.x - bubbleStillCenter.x) / dist
                val sinTheta = (bubbleMoveCenter.y - bubbleStillCenter.y) / dist

                val iBubStillStartX = bubbleStillCenter.x - bubbleStillRadius * sinTheta
                val iBubStillStartY = bubbleStillCenter.y + bubbleStillRadius * cosTheta
                val iBubMoveableEndX = bubbleMoveCenter.x - bubbleMoveRadius * sinTheta
                val iBubMoveableEndY = bubbleMoveCenter.y + bubbleMoveRadius * cosTheta
                val iBubMoveableStartX = bubbleMoveCenter.x + bubbleMoveRadius * sinTheta
                val iBubMoveableStartY = bubbleMoveCenter.y - bubbleMoveRadius * cosTheta
                val iBubStillEndX = bubbleStillCenter.x + bubbleStillRadius * sinTheta
                val iBubStillEndY = bubbleStillCenter.y - bubbleStillRadius * cosTheta

                bezierPath.reset()
                bezierPath.moveTo(iBubStillStartX, iBubStillStartY)
                bezierPath.quadTo(anchorX, anchorY, iBubMoveableEndX, iBubMoveableEndY)

                bezierPath.lineTo(iBubMoveableStartX, iBubMoveableStartY)
                bezierPath.quadTo(anchorX, anchorY, iBubStillEndX, iBubStillEndY)
                bezierPath.close()
                it.drawPath(bezierPath, mPaint)
            }

            if (isBurstAnimStart) {
                burstRect.set(
                    (bubbleMoveCenter.x - bubbleMoveRadius).toInt(),
                    (bubbleMoveCenter.y - bubbleMoveRadius).toInt(),
                    (bubbleMoveCenter.x + bubbleMoveRadius).toInt(),
                    (bubbleMoveCenter.y + bubbleMoveRadius).toInt()
                )
                it.drawBitmap(burstBitmapsArray[curDrawableIndex], null, burstRect, burstPaint)
            }
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                if (bubbleState != bubble_state_dismiss) {
                    dist = hypot(
                        event.x - bubbleStillCenter.x,
                        event.y - bubbleStillCenter.y
                    )
                    if (dist < bubbleRadius + moveOffset) {
                        bubbleState = bubble_state_connect
                    } else {
                        bubbleState = bubble_state_default
                    }
                }
            }

            MotionEvent.ACTION_MOVE -> {
                parent.requestDisallowInterceptTouchEvent(true)
                if (bubbleState != bubble_state_default) {
                    bubbleMoveCenter.x = event.x
                    bubbleMoveCenter.y = event.y
                    dist = hypot(
                        event.x - bubbleStillCenter.x,
                        event.y - bubbleStillCenter.y
                    )
                    if (bubbleState == bubble_state_connect) {
                        if (dist < maxDist - moveOffset) {
                            bubbleStillRadius = bubbleRadius - dist / 8f
                        } else {
                            bubbleState = bubble_state_apart
                        }
                    }
                    invalidate()
                }
            }

            MotionEvent.ACTION_UP -> {
                parent.requestDisallowInterceptTouchEvent(false)
                if (bubbleState == bubble_state_connect) {
                    startBubbleRestAnim()
                } else if (bubbleState == bubble_state_apart) {
                    // 达到拉断条件，让气泡消失
                    if (dist < 2 * bubbleRadius) {
                        startBubbleRestAnim()
                    } else {
                        startBubbleBurstAnim()
                    }
                }
            }
        }
        return true
    }

    private fun startBubbleBurstAnim() {
        bubbleState = bubble_state_dismiss
        isBurstAnimStart = true
        val anim = ValueAnimator.ofInt(0, burstDrawablesArray.size).apply {
            interpolator = LinearInterpolator()
            duration = 500
            addUpdateListener {
                curDrawableIndex = it.animatedValue as Int
                invalidate()
            }
            addListener(onEnd = {
                isBurstAnimStart = false
            })
        }
        anim.start()
    }

    private fun startBubbleRestAnim() {
        val anim = ValueAnimator.ofObject(
            PointFEvaluator(),
            PointF(bubbleMoveCenter.x, bubbleMoveCenter.y),
            PointF(bubbleStillCenter.x, bubbleStillCenter.y)
        ).apply {
            duration = 200
            interpolator = OvershootInterpolator(5f)
            addUpdateListener {
                bubbleMoveCenter.set(it.animatedValue as PointF)
                invalidate()
            }
            addListener(onEnd = {
                bubbleState = bubble_state_default
            })
        }
        anim.start()
    }

    fun reset() {
        initView(width, height)
        invalidate()
    }
}