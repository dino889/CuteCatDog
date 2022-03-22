package com.ssafy.ccd.src.main.home

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.ccd.R
import com.ssafy.ccd.config.ApplicationClass
import com.ssafy.ccd.config.BaseFragment
import com.ssafy.ccd.databinding.FragmentHomeBinding
import com.ssafy.ccd.src.dto.Pet
import com.ssafy.ccd.src.main.MainActivity
import kotlinx.coroutines.runBlocking


class HomeFragment : BaseFragment<FragmentHomeBinding>(FragmentHomeBinding::bind, R.layout.fragment_home) {

    private lateinit var petAdapter:HomeProfilePetsAdapter
    private lateinit var mainActivity : MainActivity
    
    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    // Binding items
    private lateinit var ivKnowledge : ImageView
    private lateinit var ivHomeUserImg : ImageView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setInstance()
        setListener()

        runBlocking {
            val userId = ApplicationClass.sharedPreferencesUtil.getUser().id
            mainViewModel.getMyPetsAllList(userId)
            mainViewModel.getUserInfo(userId, true)
        }

        mainViewModel.loginUserInfo.observe(viewLifecycleOwner) {
            binding.loginUser = it
        }

        initAdapter()
    }

    private fun setListener() {
        ivHomeUserImg.setOnClickListener {
            this@HomeFragment.findNavController().navigate(R.id.action_homeFragment_to_userProfileFragment)
        }

        ivKnowledge.setOnClickListener {
            this@HomeFragment.findNavController().navigate(R.id.informationMainFragment)
        }
    }

    private fun setInstance() {
        mainActivity = context as MainActivity
        binding.viewModel = mainViewModel

        // binding
        ivHomeUserImg = binding.fragmentHomeUserImg
        ivKnowledge = binding.fragmentHomeIvKnowledge

    }

    private fun initAdapter(){
        mainViewModel.myPetsList.observe(viewLifecycleOwner) {
            petAdapter = HomeProfilePetsAdapter(it)
            binding.fragmentHomeRvPets.apply {
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                adapter = petAdapter
                adapter!!.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
            }

            petAdapter.setAddClickListener(object : HomeProfilePetsAdapter.AddClickListener {
                override fun onClick(view: View, position: Int) {
                    this@HomeFragment.findNavController().navigate(R.id.action_homeFragment_to_addPetFragment)
                }
            })

            petAdapter.setItemClickListener(object : HomeProfilePetsAdapter.ItemClickListener {
                override fun onClick(view: View, position: Int, pet: Pet) {

                }
            })
        }
    }
}