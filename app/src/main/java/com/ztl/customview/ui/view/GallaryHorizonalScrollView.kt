package com.ztl.customview.ui.view

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.widget.HorizontalScrollView
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.view.children
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.round

class GallaryHorizonalScrollView constructor(
    context: Context,
    attrs: AttributeSet?,
    defStyleAttr: Int
) : HorizontalScrollView(context, attrs, defStyleAttr) {

    private val container by lazy {
        LinearLayout(context).apply {
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            layoutParams = params
        }
    }

    private var centerX = 0
    private var iconWidth = 0

    constructor(context: Context) : this(context, null, 0)
    constructor(context: Context, attrs: AttributeSet) : this(context, attrs, 0)

    init {
        isSmoothScrollingEnabled = true
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        super.onLayout(changed, l, t, r, b)
        val view = container.getChildAt(0)
        iconWidth = view.width
        centerX = width / 2
        centerX -= iconWidth / 2
        container.setPadding(centerX, 0, centerX, 0)
    }

    override fun onScrollChanged(l: Int, t: Int, oldl: Int, oldt: Int) {
        super.onScrollChanged(l, t, oldl, oldt)
        reveal()
    }

    override fun fling(velocityX: Int) {
        super.fling(velocityX)
        Log.d("tag", "~~@@ fling scrollX = $scrollX")
        val index = round(scrollX / iconWidth.toFloat()).toInt()
        Log.d("tag", "~~@@ fling index = $index")
        Log.d("tag", "~~@@ fling velocityX = $velocityX")
        scrollTo(iconWidth * index, 0)
        invalidate()
    }

    override fun onTouchEvent(ev: MotionEvent?): Boolean {
        when (ev?.action) {
            MotionEvent.ACTION_CANCEL,
            MotionEvent.ACTION_UP -> {
                Log.d("tag", "~~@@ action up scrollX = $scrollX")

                val index = round(scrollX / iconWidth.toFloat()).toInt()
                scrollTo(iconWidth * index, 0)
                Log.d("tag", "~~@@ action up index = $index")
            }

            MotionEvent.ACTION_MOVE -> {
                Log.d("tag", "~~@@ action move")
            }
        }
        return super.onTouchEvent(ev)
    }

    private fun reveal() {
        val indexLeft = scrollX / iconWidth
        val indexRight = indexLeft + 1
        val ratio = 5000f / iconWidth
        container.children.forEachIndexed { index, view ->
            if (index == indexLeft || index == indexRight) {
                // 渐变合成效果处理
                // 左图处理
                val ivLeft = container.getChildAt(indexLeft) as ImageView
                val leftLevel = (5000 - scrollX % iconWidth * ratio).toInt()
                ivLeft.setImageLevel(leftLevel)

                // 右图处理
                if (indexRight < container.childCount) {
                    val rightLevel = (10000 - scrollX % iconWidth * ratio).toInt()
                    val ivRight = container.getChildAt(indexRight) as ImageView
                    ivRight.setImageLevel(rightLevel)
                }

            } else {
                // 显示灰色效果
                val iv = view as ImageView
                iv.setImageLevel(0)
            }
        }

    }

    fun addImageViews(revealDrawable: Array<Drawable?>) {
        revealDrawable.forEachIndexed { index, drawable ->
            drawable?.let {
                val imageView = ImageView(context).apply {
                    setImageDrawable(drawable)
                    setPadding(2, 0, 2, 0)
                }
                container.addView(imageView)
                if (index == 0) {
                    imageView.setImageLevel(5000)
                }
            }
        }
        addView(container)
    }
}