package com.ssafy.ccd.src.main.diary

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.ssafy.ccd.R
import com.ssafy.ccd.config.BaseFragment
import com.ssafy.ccd.databinding.FragmentDiaryBinding

class DiaryFragment : BaseFragment<FragmentDiaryBinding>(FragmentDiaryBinding::bind, R.layout.fragment_diary) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.fragmentDiarySearch.setOnClickListener {
            showSortDialog()
        }
    }
    fun showSortDialog(){
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.fragment_diary_sortbottom_sheet,null)
        val dialog = BottomSheetDialog(requireContext())
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val param = WindowManager.LayoutParams()
        param.width = WindowManager.LayoutParams.MATCH_PARENT
        param.height = WindowManager.LayoutParams.WRAP_CONTENT
        val window = dialog.window
        dialog.setContentView(dialogView)
        dialog.show()
    }
    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            DiaryFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}