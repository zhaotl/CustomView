package com.ztl.customview.ui.fragment

import com.ztl.customview.databinding.FragmentPathBinding

class PathFragment: BaseFragment<FragmentPathBinding>() {

    override fun initBinding(): FragmentPathBinding {
        return FragmentPathBinding.inflate(layoutInflater)
    }

    companion object {
        fun newInstance() = PathFragment()
    }
}