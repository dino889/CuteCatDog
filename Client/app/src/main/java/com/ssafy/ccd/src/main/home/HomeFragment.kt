package com.ssafy.ccd.src.main.home

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Message
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.ssafy.ccd.R
import com.ssafy.ccd.config.ApplicationClass
import com.ssafy.ccd.config.BaseFragment
import com.ssafy.ccd.databinding.FragmentHomeBinding
import com.ssafy.ccd.src.dto.ItemInfo
import com.ssafy.ccd.src.dto.Pet
import com.ssafy.ccd.src.main.MainActivity
import com.ssafy.ccd.src.main.home.Information.InformationRecyclerViewAdapter
import kotlinx.coroutines.runBlocking


class HomeFragment : BaseFragment<FragmentHomeBinding>(FragmentHomeBinding::bind, R.layout.fragment_home) {

    private lateinit var petAdapter:HomeProfilePetsAdapter
    private lateinit var mainActivity : MainActivity

    // Binding items
    private lateinit var ivKnowledge : ImageView
    private lateinit var ivHomeUserImg : ImageView
    private lateinit var rvInformation : RecyclerView
    private lateinit var vpBanner : ViewPager2

    // Information List
    private lateinit var rvAdapterinfo : InformationRecyclerViewAdapter

    // viewPager Banner
    private var list = mutableListOf(R.drawable.banner5, R.drawable.banner2, R.drawable.banner3, R.drawable.banner4)
    private var currentPosition = 0
    private var myHandler = MyHandler()
    private val intervalTime = 1500.toLong() // 몇초 간격으로 페이지를 넘길것인지 (1500 = 1.5초)

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
        initBanner()
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
        rvInformation = binding.fragmentHomeRvInformation
        vpBanner = binding.fragmentHomeVpBanner

        // Information
        rvAdapterinfo = InformationRecyclerViewAdapter(requireContext(), mutableListOf(
            // TODO 추후에 이미지 변경해야함.
            ItemInfo("강아지 훈련 교실", "defaultimg.png"),
            ItemInfo("강아지 교감 교실", "defaultimg.png"),
            ItemInfo("고양이 훈련 교실", "defaultimg.png"),
            ItemInfo("고양이 교감 교실", "defaultimg.png"),
        ))
    }

    private fun initAdapter(){
        // myPetsList Adapter
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

        // Information Adapter
        val manager = LinearLayoutManager(requireContext())
        manager.orientation = LinearLayoutManager.HORIZONTAL

        rvInformation.apply {
            layoutManager = manager
            adapter = rvAdapterinfo
        }
    }

    override fun onResume() {
        super.onResume()
        autoScrollStart(intervalTime)
    }

    override fun onPause() {
        super.onPause()
        autoScrollStop()
    }

    private inner class MyHandler : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)

            if(msg.what == 0) {
                binding.fragmentHomeVpBanner.setCurrentItem(++currentPosition % list.size, true) // 다음 페이지로 이동
                autoScrollStart(intervalTime) // 스크롤을 계속 이어서 한다.
            }
        }
    }

    private fun autoScrollStop(){
        myHandler.removeMessages(0) // 핸들러를 중지시킴
    }

    private fun initBanner() {
        binding.fragmentHomeVpBanner.adapter = BannerAdapter(list)
        binding.fragmentHomeVpBanner.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        binding.fragmentHomeVpBanner.setCurrentItem(currentPosition, true)
        binding.fragmentHomeVpBanner.apply {
            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageScrollStateChanged(state: Int) {
                    super.onPageScrollStateChanged(state)
                    when (state) {
                        // 뷰페이저에서 손 떼었을때 / 뷰페이저 멈춰있을 때
                        ViewPager2.SCROLL_STATE_IDLE -> autoScrollStart(intervalTime)
                        // 뷰페이저 움직이는 중
                        ViewPager2.SCROLL_STATE_DRAGGING -> autoScrollStop()
                    }
                }
            })
        }
        binding.wormDotsIndicator.setViewPager2(binding.fragmentHomeVpBanner)
    }

    private fun autoScrollStart(intervalTime: Long) {
        myHandler.removeMessages(0)
        myHandler.sendEmptyMessageDelayed(0, intervalTime)
    }
}