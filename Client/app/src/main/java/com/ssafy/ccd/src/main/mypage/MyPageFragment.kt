package com.ssafy.ccd.src.main.mypage

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.tabs.TabLayoutMediator
import com.ssafy.ccd.R
import com.ssafy.ccd.config.ApplicationClass
import com.ssafy.ccd.config.BaseFragment
import com.ssafy.ccd.databinding.ActivityLoginBinding.inflate
import com.ssafy.ccd.databinding.FragmentMyPageBinding
import com.ssafy.ccd.src.dto.Pet
import com.ssafy.ccd.src.main.MainActivity
import com.ssafy.ccd.src.network.service.PetService
import com.ssafy.ccd.src.network.viewmodel.MainViewModels
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

private const val TAG = "MyPageFragment_ccd"
class MyPageFragment : BaseFragment<FragmentMyPageBinding>(FragmentMyPageBinding::bind, R.layout.fragment_my_page) {

    private lateinit var petAdapter: PetListRecyclerviewAdapter
    private lateinit var mainActivity : MainActivity
    private var petId = -1
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
//            mainViewModel.myPetsList.value?.get(0)?.let { mainViewModel.getPetDetailList(it.id) }
        }
        val myPetsList = mainViewModel.myPetsList.value
        if(myPetsList?.size!! > 0){
            petId = myPetsList[0].id
            runBlocking {
                mainViewModel.myPetsList.value?.get(0)?.let { mainViewModel.getPetDetailList(it.id) }
            }
            binding.myPageFragmentCvPetImage.visibility = View.VISIBLE
            binding.myPageFragmentCvPetEmpty.visibility = View.INVISIBLE
        }else{
            binding.myPageFragmentCvPetImage.visibility = View.INVISIBLE
            binding.myPageFragmentCvPetEmpty.visibility = View.VISIBLE
        }


        initPetAdapter()
        initTabAdapter()


        binding.myPageFragmetBtnMore.setOnClickListener {
            var popupMenu = PopupMenu(requireContext(), binding.myPageFragmetBtnMore)
            MenuInflater(requireContext()).inflate(R.menu.popup_menu, popupMenu.menu)
            var listener = PopupMenuListener()
            popupMenu.setOnMenuItemClickListener(listener)
            popupMenu.show()
        }
    }
    inner class PopupMenuListener:PopupMenu.OnMenuItemClickListener {
        override fun onMenuItemClick(item: MenuItem?): Boolean {
            when(item?.itemId){
                R.id.modify->{
                    this@MyPageFragment.findNavController().navigate(R.id.action_myPageFragment_to_addPetFragment)
                    mainViewModel.petId = petId
                }
                R.id.delete -> {
                    DeletePets(petId)
                }
            }
            return false
        }

    }
    fun DeletePets(petId:Int){
        GlobalScope.launch {
            var response = PetService().petsDeleteService(petId)
            val res = response.body()
            Log.d(TAG, "DeletePets: ${res}")
            if(response.code()==200){
                if(res!=null){
                    if(res.success){
                        runBlocking {
                            mainViewModel.getMyPetsAllList(ApplicationClass.sharedPreferencesUtil.getUser().id)
                        }
                        val myPetsList = mainViewModel.myPetsList.value
                        if(myPetsList?.size!! > 0){

                            runBlocking {
                                mainViewModel.getPetDetailList(petId)
                            }

                            binding.myPageFragmentCvPetImage.visibility = View.VISIBLE
                            binding.myPageFragmentCvPetEmpty.visibility = View.INVISIBLE
                        }else{
                            binding.myPageFragmentCvPetImage.visibility = View.INVISIBLE
                            binding.myPageFragmentCvPetEmpty.visibility = View.VISIBLE
                        }
                    }else{
                        Log.d(TAG, "DeletePets: ")
                    }
                }
            }else{
                Log.d(TAG, "DeletePets: ")
            }
        }
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
                        petId = pet.id
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