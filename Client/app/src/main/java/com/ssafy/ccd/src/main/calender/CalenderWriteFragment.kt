package com.ssafy.ccd.src.main.calender

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ssafy.ccd.R
import com.ssafy.ccd.config.BaseFragment
import com.ssafy.ccd.databinding.FragmentCalenderWriteBinding

class CalenderWriteFragment : BaseFragment<FragmentCalenderWriteBinding>(FragmentCalenderWriteBinding::bind, R.layout.fragment_calender_write) {

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
            CalenderWriteFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }
}