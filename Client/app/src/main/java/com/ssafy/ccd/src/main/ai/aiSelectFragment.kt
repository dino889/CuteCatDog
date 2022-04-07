package com.ssafy.ccd.src.main.ai

import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.MimeTypeMap
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.RadioGroup
import androidx.core.content.FileProvider
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.ssafy.ccd.R
import com.ssafy.ccd.config.BaseFragment
import com.ssafy.ccd.databinding.FragmentAiSelectBinding
import com.ssafy.ccd.src.main.MainActivity
import com.ssafy.ccd.src.network.service.PetService
import com.ssafy.ccd.src.network.viewmodel.MainViewModels
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.io.InputStream

class aiSelectFragment : BaseFragment<FragmentAiSelectBinding>(FragmentAiSelectBinding::bind,R.layout.fragment_ai_select) {

    private lateinit var imageView: ImageView
    private lateinit var analygyBtn : Button
    private lateinit var backBtn : ImageButton
    private lateinit var radioGroup: RadioGroup
    private lateinit var mainActivity:MainActivity
    private lateinit var mainViewModels: MainViewModels
    private lateinit var contentResolver : ContentResolver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        contentResolver = mainActivity.contentResolver
    }
    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainActivity.hideBottomNavi(true)
        setInstance()
        setListener()
        setInit()
    }

    private fun setInit() {
        Glide.with(requireContext())
            .load(mainViewModels.uploadedImageUri)
            .circleCrop()
            .into(imageView)
//        imageView.setImageURI(mainViewModels.uploadedImageUri)
    }

    private fun setListener() {
        backBtn.setOnClickListener{
            (requireActivity() as MainActivity).onBackPressed()

//            this@aiSelectFragment.findNavController().navigate(R.id.homeFragment)
        }

        analygyBtn.setOnClickListener {
            if(mainViewModel.aiType == 2){
                showCustomToast("이 사진으로는 검사하실 수 없어요 :(")
            }else{
                this@aiSelectFragment.findNavController().navigate(R.id.aiFragment)
            }
        }
        binding.fragmentSelectAiDog.setOnClickListener {
            binding.fragmentSelectAiDogCheck.visibility = View.VISIBLE
            binding.fragmentSelectAiCatCheck.visibility = View.INVISIBLE
            mainViewModels.aiType = 0
        }
        binding.fragmentSelectAiCat.setOnClickListener {
            binding.fragmentSelectAiCatCheck.visibility = View.VISIBLE
            binding.fragmentSelectAiDogCheck.visibility = View.INVISIBLE
            mainViewModels.aiType = 1
        }
//        radioGroup.setOnCheckedChangeListener { _, i ->
//            if(i == R.id.fragmentAiSelect_rbDog) mainViewModels.aiType = 0
//            if(i == R.id.fragmentAiSelect_rbCat) mainViewModels.aiType = 1
//        }
    }

    private fun setInstance() {
        imageView = binding.fragmentAiImage
        analygyBtn = binding.fragmentAiSelectBtnAnaylsis
        mainViewModels = (requireActivity() as MainActivity).mainViewModels
        backBtn = binding.fragmentAiSelectIvBack
        if(mainViewModels.aiType == 0){
            binding.fragmentSelectAiDogCheck.visibility = View.VISIBLE
            binding.fragmentSelectAiCatCheck.visibility = View.INVISIBLE
        }else if(mainViewModels.aiType == 1){
            binding.fragmentSelectAiCatCheck.visibility = View.VISIBLE
            binding.fragmentSelectAiDogCheck.visibility = View.INVISIBLE
        }else{
            binding.textView33.text = "반려동물이 고양이나 강아지가 아닌 것 같습니다 :(\n 다시 시도해주세요"
            binding.selectType.visibility = View.INVISIBLE
            binding.fragmentAiSelectBtnAnaylsis.setBackgroundColor(Color.GRAY)
//            binding.fragmentSelectAiDogCheck.visibility = View.INVISIBLE
//            binding.fragmentSelectAiCatCheck.visibility = View.INVISIBLE
        }
//        radioGroup = binding.fragmentAiSelectRg
    }

    override fun onDestroy() {
        super.onDestroy()
        mainActivity.hideBottomNavi(false)
//        (requireActivity() as MainActivity).showBottomAppBar()
    }

    override fun onPause() {
        super.onPause()
//        (requireActivity() as MainActivity).showBottomAppBar()
    }

    override fun onResume() {
        super.onResume()
//        (requireActivity() as MainActivity).hideBottomAppBar()
        mainActivity.hideBottomNavi(true)
    }

    override fun onStart() {
        super.onStart()
        mainActivity.hideBottomNavi(true)
    }
}