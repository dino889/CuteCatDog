package com.ssafy.ccd.src.main.ai

import android.app.ProgressDialog
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.RadioGroup
import com.ssafy.ccd.R
import com.ssafy.ccd.config.BaseFragment
import com.ssafy.ccd.databinding.FragmentAiSelectBinding
import com.ssafy.ccd.src.main.MainActivity
import com.ssafy.ccd.src.network.viewmodel.MainViewModels

class aiSelectFragment : BaseFragment<FragmentAiSelectBinding>(FragmentAiSelectBinding::bind,R.layout.fragment_ai_select) {

    private lateinit var imageView: ImageView
    private lateinit var analygyBtn : Button
    private lateinit var backBtn : ImageButton
    private lateinit var radioGroup: RadioGroup

    private lateinit var mainViewModels: MainViewModels



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setInstance()
        setListener()
        setInit()
    }

    private fun setInit() {
        imageView.setImageURI(mainViewModels.uploadedImageUri)
        (requireActivity() as MainActivity).hideBottomAppBar()
    }

    private fun setListener() {
        backBtn.setOnClickListener{
            parentFragmentManager.popBackStack()
        }

        analygyBtn.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.activity_main_navHost, aiFragment())
                .addToBackStack(null)
                .commit()
        }

        radioGroup.setOnCheckedChangeListener { radioGroup, i ->
            if(i == R.id.fragmentAiSelect_rbDog) mainViewModels.aiType = 0
            if(i == R.id.fragmentAiSelect_rbCat) mainViewModels.aiType = 1
        }
    }

    private fun setInstance() {
        imageView = binding.fragmentAiImage
        analygyBtn = binding.fragmentAiSelectBtnAnaylsis
        mainViewModels = (requireActivity() as MainActivity).mainViewModels
        backBtn = binding.fragmentAiBack
        radioGroup = binding.fragmentAiSelectRg
    }

    override fun onDestroy() {
        super.onDestroy()
        (requireActivity() as MainActivity).showBottomAppBar()
    }

    override fun onPause() {
        super.onPause()
        (requireActivity() as MainActivity).showBottomAppBar()
    }
}