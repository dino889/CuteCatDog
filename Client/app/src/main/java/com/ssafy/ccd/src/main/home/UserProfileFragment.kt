package com.ssafy.ccd.src.main.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.ssafy.ccd.R
import com.ssafy.ccd.config.BaseFragment
import com.ssafy.ccd.databinding.FragmentUserProfileBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class UserProfileFragment : BaseFragment<FragmentUserProfileBinding>(FragmentUserProfileBinding::bind, R.layout.fragment_user_profile) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainViewModel.loginUserInfo.observe(viewLifecycleOwner, {
            binding.user = it
        })

        // back btn click event
        binding.userProfileFragmentIvBack.setOnClickListener {
            this@UserProfileFragment.findNavController().popBackStack()
        }
    }

    
}