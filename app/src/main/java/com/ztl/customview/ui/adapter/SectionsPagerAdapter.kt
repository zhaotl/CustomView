package com.ztl.customview.ui.adapter

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.ztl.customview.R
import com.ztl.customview.ui.fragment.*

class SectionsPagerAdapter(private val context: Context, fm: FragmentManager) :
    FragmentPagerAdapter(fm) {

    private val TAB_TITLES by lazy {
        context.resources.getStringArray(R.array.custom_view)
    }

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0, 1 -> ChartViewFragment.newInstance(position + 1)
            2 -> FlowViewFragment.newInstance()
            3 -> FilterViewFragment.newInstance()
            4 -> XFermodeFragment.newInstance()
            5 -> CanvasFragment.newInstance()
            6 -> BezierFragment.newInstance()
            else -> {
                EmptyFragment.newInstance()
            }
        }
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return TAB_TITLES[position]
    }

    override fun getCount(): Int {
        return TAB_TITLES.size
    }
}