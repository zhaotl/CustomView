package com.ztl.customview.ui.fragment

import com.ztl.customview.databinding.FragmentXfermodeBinding

class XFermodeFragment: BaseFragment<FragmentXfermodeBinding>() {

    override fun initBinding() = FragmentXfermodeBinding.inflate(layoutInflater)

    companion object {
        fun newInstance() = XFermodeFragment()
    }
}