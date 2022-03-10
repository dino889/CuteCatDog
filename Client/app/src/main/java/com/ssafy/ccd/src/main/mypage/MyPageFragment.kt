package com.ssafy.ccd.src.main.mypage

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.tabs.TabLayoutMediator
import com.ssafy.ccd.R
import com.ssafy.ccd.config.BaseFragment
import com.ssafy.ccd.databinding.FragmentMyPageBinding
import com.ssafy.ccd.src.dto.Pet


class MyPageFragment : BaseFragment<FragmentMyPageBinding>(FragmentMyPageBinding::bind, R.layout.fragment_my_page) {

    private lateinit var petListRecyclerView: PetListRecyclerviewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initRecyclerAdapter()
        initTabAdapter()
    }


    private fun initRecyclerAdapter() {
        val petList = mutableListOf<Pet>()
        petList.add(Pet(1, "1"))
        petList.add(Pet(2, "2"))
        petListRecyclerView = PetListRecyclerviewAdapter(petList = petList)
        petListRecyclerView.setItemClickListener(object : PetListRecyclerviewAdapter.ItemClickListener {
            override fun onClick(view: View, position: Int, pet: Pet) {
                // recyclerview 하단 pet 정보 변경 함수 호출
//                showCustomToast(pet.photo)
                this@MyPageFragment.findNavController().navigate(R.id.action_myPageFragment_to_addPetFragment)
            }
        })
        binding.myPageFragmentRvPetList.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = petListRecyclerView
            adapter!!.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
        }
    }

    private fun initTabAdapter() {
        val viewPagerAdapter = MyTabPageAdapter(this)
        val tabList = listOf("내 일정", "내가 쓴 글")

        viewPagerAdapter.addFragment(MyScheduleFragment())
        viewPagerAdapter.addFragment(MyPostFragment())

        binding.myPageFragmentVp.adapter = viewPagerAdapter
        TabLayoutMediator(binding.myPageFragmentTabLayout, binding.myPageFragmentVp) { tab, position ->
            tab.text = tabList[position]
        }.attach()
    }

}