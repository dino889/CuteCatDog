package com.ssafy.ccd.src.main.home.Community

import android.os.Bundle
import android.view.View
import com.ssafy.ccd.R
import com.ssafy.ccd.config.BaseFragment
import com.ssafy.ccd.databinding.FragmentShareBoardBinding
import com.ssafy.ccd.databinding.FragmentWriteBoardBinding


class WriteBoardFragment : BaseFragment<FragmentWriteBoardBinding>(FragmentWriteBoardBinding::bind,R.layout.fragment_write_board) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            WriteBoardFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}