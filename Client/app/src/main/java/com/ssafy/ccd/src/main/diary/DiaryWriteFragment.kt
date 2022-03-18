package com.ssafy.ccd.src.main.diary

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ssafy.ccd.R
import com.ssafy.ccd.config.BaseFragment
import com.ssafy.ccd.databinding.FragmentDiaryWriteBinding

class DiaryWriteFragment : BaseFragment<FragmentDiaryWriteBinding>(FragmentDiaryWriteBinding::bind,R.layout.fragment_diary_write) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.fragmentDiaryWriteSuccessBtn.setOnClickListener {
            
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            DiaryWriteFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}