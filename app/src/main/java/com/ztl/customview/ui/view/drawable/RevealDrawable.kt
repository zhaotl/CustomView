package com.ztl.customview.ui.view.drawable

import android.R.attr
import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.PixelFormat
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.Gravity
import kotlin.math.abs
import kotlin.math.max


class RevealDrawable(
    private val mUnselectedDrawable: Drawable,
    private val mSelectedDrawable: Drawable
) : Drawable() {

    override fun onBoundsChange(bounds: Rect?) {
        bounds?.let {
            mUnselectedDrawable.bounds = it
            mSelectedDrawable.bounds = it
            Log.d("tag", "bounds === $it")
        }

    }

    override fun getIntrinsicWidth(): Int {
        return max(
            mUnselectedDrawable.intrinsicWidth,
            mSelectedDrawable.intrinsicWidth
        )
    }

    override fun getIntrinsicHeight(): Int {
        return max(
            mUnselectedDrawable.intrinsicHeight,
            mSelectedDrawable.intrinsicHeight
        )
    }

    override fun draw(canvas: Canvas) {
        if (level == 0 || level == 10000) {
            mUnselectedDrawable.draw(canvas)
        } else if (level == 5000) {
            mSelectedDrawable.draw(canvas)
        } else {
            val containerRect = bounds
            val outRect = Rect()
            Log.d("tag", "containerRect === ${containerRect.width()}")
            Log.d("tag", "containerRect === ${containerRect.height()}")
            val ratio = level / 5000f - 1f
            var gravity = if (ratio < 0) Gravity.LEFT else Gravity.RIGHT
            //从一个已有的bound矩形边界范围当中抠出一个我们想要的矩形
            Gravity.apply(
                gravity,
                (containerRect.width() * abs(ratio)).toInt(),
                containerRect.height(),
                containerRect,
                outRect
            )
            Log.d("tag", "outRect === $outRect")

            canvas.save()
            canvas.clipRect(outRect)
            mUnselectedDrawable.draw(canvas)
            canvas.restore()

            gravity = if (ratio < 0) Gravity.RIGHT else Gravity.LEFT //这里的方向跟上面的图刚好相反
            Gravity.apply(
                gravity,
                containerRect.width() - (containerRect.width() * abs(ratio)).toInt(),
                containerRect.height(),
                containerRect,
                outRect
            )

            canvas.save()
            canvas.clipRect(outRect)
            mSelectedDrawable.draw(canvas)
            canvas.restore()
        }
    }

    override fun onLevelChange(level: Int): Boolean {
        invalidateSelf()
        return true
    }

    override fun setAlpha(alpha: Int) {
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
    }

    override fun getOpacity(): Int = PixelFormat.UNKNOWN
}