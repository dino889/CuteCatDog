package com.ssafy.ccd.src.main.calender

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.ccd.R
import com.ssafy.ccd.config.ApplicationClass
import com.ssafy.ccd.config.BaseFragment
import com.ssafy.ccd.databinding.FragmentCalenderBinding
import com.ssafy.ccd.src.dto.Pet
import com.ssafy.ccd.src.main.mypage.PetListRecyclerviewAdapter
import com.ssafy.ccd.util.CommonUtils
import kotlinx.coroutines.runBlocking

private const val TAG = "CalenderFragment"
class CalenderFragment : BaseFragment<FragmentCalenderBinding>(FragmentCalenderBinding::bind, R.layout.fragment_calender) {
    private lateinit var petAdapter: CalendarWritePetAdapter
    private lateinit var monthListAdapter : CalenderMonthAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = mainViewModel
        runBlocking {
            mainViewModel.getMyPetsAllList(ApplicationClass.sharedPreferencesUtil.getUser().id)
            mainViewModel.getCalendarListbyUser(ApplicationClass.sharedPreferencesUtil.getUser().id)
            Log.d(TAG, "onViewCreated: ")
        }
        setListener()

        binding.fragmentCalenderWrite.setOnClickListener {
            this@CalenderFragment.findNavController().navigate(R.id.action_calenderFragment_to_calenderWriteFragment)
        }

    }
    fun setListener(){
        initCalendar()
        initAdapter()
    }
    fun initAdapter(){
        mainViewModel.myPetsList.observe(viewLifecycleOwner, {
            petAdapter = CalendarWritePetAdapter()
            petAdapter.list = it
            binding.fragmentCalenderPetsRv.apply{
                layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)
                adapter = petAdapter
                adapter!!.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
            }
            petAdapter.setItemClickListener(object: CalendarWritePetAdapter.ItemClickListener{
                override fun onClick(view: View, position: Int, id: Int) {
                    //필터링하기...
                    Log.d(TAG, "onClick: ${id}")
                    initPetFilter(id)
                }

            })
        })
    }
    fun initPetFilter(petId:Int){
        mainViewModel.calendarList.observe(viewLifecycleOwner, {
            val monthListManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL,false)
            var date = arrayListOf<String>()
            for(i in 0..it.size-1){
                if(it[i].petId == petId){
                    date.add(CommonUtils.makeBirthString(it[i].datetime))
                }
            }
            Log.d(TAG, "initCalendar: ${date}")
            monthListAdapter = CalenderMonthAdapter(requireContext(),date,mainViewModel,viewLifecycleOwner)

            binding.fragmentCalenderCustomCalender.apply {
                layoutManager = monthListManager
                adapter = monthListAdapter
                scrollToPosition(Int.MAX_VALUE/2)
            }
            val snap = PagerSnapHelper()
            if( binding.fragmentCalenderCustomCalender.onFlingListener == null){
                snap.attachToRecyclerView(binding.fragmentCalenderCustomCalender)
            }

        })
    }
    fun initCalendar(){
        mainViewModel.calendarList.observe(viewLifecycleOwner, {
            val monthListManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL,false)

            var date = arrayListOf<String>()
//            if(petId == -1){
//
//            }else{
//                for(i in 0..it.size-1){
//                    if(petId == it[i].petId) {
//                        Log.d(TAG, "initCalendar: $petId")
//                        date.add(CommonUtils.makeBirthString(it[i].datetime))
//                    }
//                }
//            }
            for(i in 0..it.size-1){
                date.add(CommonUtils.makeBirthString(it[i].datetime))
            }
            Log.d(TAG, "initCalendar: ${date}")
            monthListAdapter = CalenderMonthAdapter(requireContext(),date,mainViewModel,viewLifecycleOwner)

            binding.fragmentCalenderCustomCalender.apply {
                layoutManager = monthListManager
                adapter = monthListAdapter
                scrollToPosition(Int.MAX_VALUE/2)
            }
            val snap = PagerSnapHelper()
            snap.attachToRecyclerView(binding.fragmentCalenderCustomCalender)
        })
    }

    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            CalenderFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}