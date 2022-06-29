package com.ztl.customview.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.ztl.customview.databinding.FragmentChartBinding
import com.ztl.customview.ui.viewmodel.ChartViewModel
import com.ztl.customview.utils.gone

class ChartViewFragment : BaseFragment<FragmentChartBinding>() {

    private lateinit var pageViewModel: ChartViewModel
    private var index = 1

    override fun initBinding(): FragmentChartBinding {
        return FragmentChartBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        index = arguments?.getInt(ARG_SECTION_NUMBER) ?: 1
        pageViewModel = ViewModelProvider(this).get(ChartViewModel::class.java).apply {
            initPieData()
            initBarData()
            initTimer()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (index == 1) {
            binding.barView.gone()
            pageViewModel.pieData.observe(viewLifecycleOwner) {
                binding.pieView.setData(it)
            }
            pageViewModel.startTimer()
            pageViewModel.step.observe(viewLifecycleOwner) {
                binding.progressView.incrementProgressBy(1)
                binding.progressView2.incrementProgressBy(2)
                binding.progressView3.incrementProgressBy(3)
                if (it == 0L) {
                    binding.progressView.setCompleteProgress()
                    binding.progressView2.setCompleteProgress()
                    binding.progressView3.setCompleteProgress()
                }
            }
        } else {
            binding.progressView.gone()
            binding.progressView2.gone()
            binding.progressView3.gone()
            binding.pieView.gone()

            pageViewModel.barData.observe(viewLifecycleOwner) {
                binding.barView.setData(it)
            }
        }
    }

    companion object {
        private const val ARG_SECTION_NUMBER = "section_number"

        @JvmStatic
        fun newInstance(sectionNumber: Int): ChartViewFragment {
            return ChartViewFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_SECTION_NUMBER, sectionNumber)
                }
            }
        }
    }

}