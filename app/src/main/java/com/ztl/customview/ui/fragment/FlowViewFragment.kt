package com.ztl.customview.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.ztl.customview.R
import com.ztl.customview.databinding.FragmentFlowBinding

class FlowViewFragment: BaseFragment<FragmentFlowBinding>() {

    override fun initBinding() = FragmentFlowBinding.inflate(layoutInflater)

    companion object {
        @JvmStatic
        fun newInstance() = FlowViewFragment()
    }

}