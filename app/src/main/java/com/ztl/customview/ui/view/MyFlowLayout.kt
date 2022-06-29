package com.ztl.customview.ui.view

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.core.view.children
import kotlin.math.max

class MyFlowLayout : ViewGroup {

    private var listLineHeight = mutableListOf<Int>()
    private var listLineView = mutableListOf<MutableList<View>>()

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        listLineHeight.clear()
        listLineView.clear()

        // 算出宽高的mode 和 size
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)

        if (widthMode == MeasureSpec.EXACTLY && heightMode == MeasureSpec.EXACTLY) {
            measureChild(widthMeasureSpec, heightMeasureSpec, true)
        } else {
            measureChild(widthMeasureSpec, heightMeasureSpec, false)
        }

    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        var curleft = 0
        var curTop = 0
        var left = 0
        var top = 0
        var bottom = 0
        var right = 0

        listLineView.forEachIndexed { index, listView ->
            for (view in listView) {
                val layoutParams = view.layoutParams as MarginLayoutParams
                left = curleft + layoutParams.leftMargin
                top = curTop + layoutParams.topMargin
                right = left + view.measuredWidth
                bottom = top + view.measuredHeight
                view.layout(left, top, right, bottom)

                curleft += view.measuredWidth + layoutParams.leftMargin + layoutParams.rightMargin
            }

            curleft = 0
            curTop += listLineHeight[index]
        }

        listLineView.clear()
        listLineHeight.clear()
    }

    override fun generateLayoutParams(attrs: AttributeSet?): LayoutParams {
        return MarginLayoutParams(context, attrs)
    }

    private fun measureChild(widthMeasureSpec: Int, heightMeasureSpec: Int, isExact: Boolean) {

        var currentLineCalWidth = 0
        var currentLineCalHeight = 0
        var measureWidth = 0
        var measureHeight = 0

        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)

        if(isExact) {
            measureWidth = widthSize
            measureHeight = heightSize
        }

        // 当前控件设置了match_parent 或 wrap_content
        var newList = mutableListOf<View>()
        for (child in children) {
            measureChild(child, widthMeasureSpec, heightMeasureSpec)
            val childLayoutParams = child.layoutParams as MarginLayoutParams

            val childWidth =
                child.measuredWidth + childLayoutParams.leftMargin + childLayoutParams.rightMargin
            val childHeight =
                child.measuredHeight + childLayoutParams.topMargin + childLayoutParams.bottomMargin

            if (childWidth + currentLineCalWidth > widthSize) {
                if (!isExact) {
                    measureWidth = max(measureWidth, childWidth)
                    measureHeight += currentLineCalHeight
                }

                listLineHeight.add(currentLineCalHeight)
                listLineView.add(newList)

                currentLineCalWidth = childWidth
                currentLineCalHeight = childHeight

                newList = mutableListOf()
                newList.add(child)
            } else {
                currentLineCalWidth += childWidth
                currentLineCalHeight = max(currentLineCalHeight, childHeight)
                newList.add(child)
            }

            if (child == children.last()) {
                Log.d("tag", "~~!! last child")
                if (!isExact) {
                    measureWidth = max(measureWidth, childWidth)
                    measureHeight += currentLineCalHeight
                }

                listLineHeight.add(currentLineCalHeight)
                listLineView.add(newList)
            }
        }

        setMeasuredDimension(measureWidth, measureHeight)
    }
}