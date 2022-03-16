package com.ssafy.ccd.src.main.mypage

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.tabs.TabLayoutMediator
import com.ssafy.ccd.R
import com.ssafy.ccd.config.ApplicationClass
import com.ssafy.ccd.config.BaseFragment
import com.ssafy.ccd.databinding.FragmentMyPageBinding
import com.ssafy.ccd.src.dto.Pet
import com.ssafy.ccd.src.main.MainActivity
import com.ssafy.ccd.src.network.service.PetService
import com.ssafy.ccd.src.network.viewmodel.MainViewModels
import kotlinx.coroutines.runBlocking

private const val TAG = "MyPageFragment_ccd"
class MyPageFragment : BaseFragment<FragmentMyPageBinding>(FragmentMyPageBinding::bind, R.layout.fragment_my_page) {

    private lateinit var petAdapter: PetListRecyclerviewAdapter

    private lateinit var mainActivity : MainActivity

//    private val mainViewModel:MainViewModels by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }
    
    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = mainViewModel
        runBlocking {
            mainViewModel.getMyPetsAllList(ApplicationClass.sharedPreferencesUtil.getUser().id)
            mainViewModel.myPetsList.value?.get(0)?.let { mainViewModel.getPetDetailList(it.id) }
        }
        initPetAdapter()
        initTabAdapter()

    }

    fun initPetAdapter(){
        mainViewModel.myPetsList.observe(viewLifecycleOwner, {
            Log.d(TAG, "initPetAdapter: ${it}")
            petAdapter = PetListRecyclerviewAdapter()
            petAdapter.petList = it

            binding.myPageFragmentRvPetList.apply {
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL,false)
                adapter = petAdapter
                adapter!!.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
            }

            petAdapter.setAddClickListener(object: PetListRecyclerviewAdapter.AddClickListener {
                override fun onClick(view: View, position: Int) {
                    //AddPetFragment로 넘기기
                    this@MyPageFragment.findNavController().navigate(R.id.action_myPageFragment_to_addPetFragment)
                }
            })

            petAdapter.setItemClickListener(object : PetListRecyclerviewAdapter.ItemClickListener {
                override fun onClick(view: View, position: Int, pet: Pet) {
                    //해당 반려동물 정보 불러오기
                    runBlocking{
                        mainViewModel.getPetDetailList(pet.id)
                        binding.viewModel = mainViewModel
                        Log.d(TAG, "onClick: ${pet.id}")
                    }
                }

            })
        })
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