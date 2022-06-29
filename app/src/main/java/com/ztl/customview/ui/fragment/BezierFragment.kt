package com.ztl.customview.ui.fragment

import android.os.Bundle
import android.view.View
import com.ztl.customview.databinding.FragmentBezierBinding

class BezierFragment: BaseFragment<FragmentBezierBinding>() {

    override fun initBinding(): FragmentBezierBinding {
        return FragmentBezierBinding.inflate(layoutInflater)
    }

    companion object {
        fun newInstance() = BezierFragment()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.resetBtn.setOnClickListener {
            binding.dragBubbleView.reset()
        }
    }
}