package com.ztl.customview.ui.fragment

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.View
import com.ztl.customview.R
import com.ztl.customview.databinding.FragmentCanvasBinding
import com.ztl.customview.ui.view.drawable.RevealDrawable


class CanvasFragment : BaseFragment<FragmentCanvasBinding>() {

    private var avft: Drawable? = null
    private var avft_active: Drawable? = null

    private val imgIds = intArrayOf( //7个
        R.drawable.avft,
        R.drawable.box_stack,
        R.drawable.bubble_frame,
        R.drawable.bubbles,
        R.drawable.bullseye,
        R.drawable.circle_filled,
        R.drawable.circle_outline,
        R.drawable.avft,
        R.drawable.box_stack,
        R.drawable.bubble_frame,
        R.drawable.bubbles,
        R.drawable.bullseye,
        R.drawable.circle_filled,
        R.drawable.circle_outline
    )
    private val imgIds_active = intArrayOf(
        R.drawable.avft_active,
        R.drawable.box_stack_active,
        R.drawable.bubble_frame_active,
        R.drawable.bubbles_active,
        R.drawable.bullseye_active,
        R.drawable.circle_filled_active,
        R.drawable.circle_outline_active,
        R.drawable.avft_active,
        R.drawable.box_stack_active,
        R.drawable.bubble_frame_active,
        R.drawable.bubbles_active,
        R.drawable.bullseye_active,
        R.drawable.circle_filled_active,
        R.drawable.circle_outline_active
    )
    lateinit var revealDrawables: Array<Drawable?>

    override fun initBinding(): FragmentCanvasBinding {
        return FragmentCanvasBinding.inflate(layoutInflater)
    }

    companion object {
        fun newInstance() = CanvasFragment()
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("tag", "~~!!!!!!!!!!!!!")
        avft = context?.getDrawable(R.drawable.avft)
        avft_active = context?.getDrawable(R.drawable.avft_active)
        if (avft != null && avft_active != null) {
            Log.d("tag", "!!!!!!!!!!!!!")
            val revealDrawable = RevealDrawable(avft!!, avft_active!!)
            binding.imageview.setImageDrawable(revealDrawable)
        }

        initData()
    }

    private fun initData() {
        revealDrawables = Array(imgIds.size) { i ->
            val img = context?.getDrawable(imgIds[i])
            val imgActive = context?.getDrawable(imgIds_active[i])
            if (img != null && imgActive != null) {
                val rd = RevealDrawable(
                    img,
                    imgActive
                )
                rd
            } else {
                null
            }
        }

        binding.horizontalScroll.addImageViews(revealDrawables)
    }

}