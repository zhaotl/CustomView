package com.ztl.customview.ui.viewmodel

import android.graphics.Color
import android.os.CountDownTimer
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ztl.customview.model.BarData
import com.ztl.myndkdemo.model.PieData

class ChartViewModel : ViewModel() {
    
    private val _pieData = MutableLiveData<List<PieData>>()
    private val _barData = MutableLiveData<List<BarData>>()
    private val _step = MutableLiveData<Long>()

    val pieData: LiveData<List<PieData>> = _pieData
    val barData: LiveData<List<BarData>> = _barData
    val step: LiveData<Long> = _step

    private var timer: CountDownTimer? = null

    fun initTimer() {
        timer = object : CountDownTimer(10000, 100) {

            override fun onTick(millisUntilFinished: Long) {
//                Log.d("tag", "millisUntilFinished = $millisUntilFinished")
                _step.value = millisUntilFinished
            }

            override fun onFinish() {
                _step.value = 0
            }
        }
    }

    fun startTimer() {
        timer?.start()
    }

    fun initPieData() {
        val datas = mutableListOf<PieData>()
        val pie1 = PieData(3f, "一月份", Color.BLUE)
        val pie2 = PieData(3f, "二月份", Color.RED)
        val pie3 = PieData(2f, "三月份", Color.CYAN)
        val pie4 = PieData(1f, "四月份", Color.MAGENTA)

        datas.add(pie1)
        datas.add(pie2)
        datas.add(pie3)
        datas.add(pie4)
        _pieData.value = datas
    }

    fun initBarData() {
        val datas = mutableListOf<BarData>()
        val bar1 = BarData(30f, "描述一")
        val bar2 = BarData(50f, "描述二")
        val bar3 = BarData(60f, "描述三")
        val bar4 = BarData(80f, "描述四")
        val bar5 = BarData(20f, "描述五")
        val bar6 = BarData(10f, "描述六")

        datas.add(bar1)
        datas.add(bar2)
        datas.add(bar3)
        datas.add(bar4)
        datas.add(bar5)
        datas.add(bar6)

        datas.add(bar1)
        datas.add(bar2)
        datas.add(bar3)
        datas.add(bar4)
        datas.add(bar5)
        datas.add(bar6)

        _barData.value = datas
    }


}