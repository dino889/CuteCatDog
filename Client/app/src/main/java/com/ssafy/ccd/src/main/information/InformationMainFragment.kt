package com.ssafy.ccd.src.main.information

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.cardview.widget.CardView
import com.ssafy.ccd.R
import com.ssafy.ccd.config.BaseFragment
import com.ssafy.ccd.databinding.FragmentInformationMainBinding

class InformationMainFragment : BaseFragment<FragmentInformationMainBinding>(FragmentInformationMainBinding::bind, R.layout.fragment_information_main) {

    private lateinit var cvDogTrain : CardView
    private lateinit var cvCatTrain : CardView
    private lateinit var cvDogCon : CardView
    private lateinit var cvCatCon : CardView
    private lateinit var ivBack : ImageView
    private lateinit var intent : Intent

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setInstance()
        setListener()
    }

    private fun setListener() {
        cvDogTrain.setOnClickListener {
            intent.putExtra("type", 0)
            startActivity(intent)
        }

        cvDogCon.setOnClickListener {
            intent.putExtra("type", 1)
            startActivity(intent)
        }

        cvCatTrain.setOnClickListener {
            intent.putExtra("type", 2)
            startActivity(intent)
        }

        cvCatCon.setOnClickListener {
            intent.putExtra("type", 3)
            startActivity(intent)
        }

        ivBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    private fun setInstance() {
        cvDogTrain = binding.fragmentInfoMainCvDogTrain
        cvCatTrain = binding.fragmentInfoMainCvCatTrain
        cvDogCon = binding.fragmentInfoMainCvDogCon
        cvCatCon = binding.fragmentInfoMainCvCatCon
        ivBack = binding.fragmentInfoMainIvBack
        intent = Intent(requireActivity(), InformationActivity::class.java)
    }
}