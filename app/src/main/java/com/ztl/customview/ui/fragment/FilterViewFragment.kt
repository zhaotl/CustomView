package com.ztl.customview.ui.fragment

import com.ztl.customview.databinding.FragmentFilterviewBinding

class FilterViewFragment : BaseFragment<FragmentFilterviewBinding>() {

    companion object {
        fun newInstance() = FilterViewFragment()
    }

    override fun initBinding() =
        FragmentFilterviewBinding.inflate(layoutInflater)
}