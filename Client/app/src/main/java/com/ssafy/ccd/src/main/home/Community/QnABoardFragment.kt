package com.ssafy.ccd.src.main.home.Community

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ssafy.ccd.R
import com.ssafy.ccd.config.BaseFragment
import com.ssafy.ccd.databinding.FragmentQnaBoardBinding

class QnABoardFragment : BaseFragment<FragmentQnaBoardBinding>(FragmentQnaBoardBinding::bind,R.layout.fragment_qna_board) {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

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
            QnABoardFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }
}