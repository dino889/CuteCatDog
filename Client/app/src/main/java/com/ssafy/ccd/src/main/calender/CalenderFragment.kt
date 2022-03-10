package com.ssafy.ccd.src.main.calender

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import com.ssafy.ccd.R
import com.ssafy.ccd.config.BaseFragment
import com.ssafy.ccd.databinding.FragmentCalenderBinding

class CalenderFragment : BaseFragment<FragmentCalenderBinding>(FragmentCalenderBinding::bind, R.layout.fragment_calender) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initCalendar()

        binding.fragmentCalenderWrite.setOnClickListener {
            val intent = Intent(requireContext(),CalenderWriteFragment::class.java)
            startActivity(intent)
        }

    }
    fun initCalendar(){
        val monthListManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL,false)
        val monthListAdapter = CalenderMonthAdapter(requireContext())

        binding.fragmentCalenderCustomCalender.apply {
            layoutManager = monthListManager
            adapter = monthListAdapter
            scrollToPosition(Int.MAX_VALUE/2)
        }
        val snap = PagerSnapHelper()
        snap.attachToRecyclerView(binding.fragmentCalenderCustomCalender)

    }
    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            CalenderFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}