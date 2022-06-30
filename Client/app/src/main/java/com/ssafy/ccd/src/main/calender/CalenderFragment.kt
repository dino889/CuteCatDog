package com.ssafy.ccd.src.main.calender

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
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
import com.ssafy.ccd.src.main.MainActivity
import com.ssafy.ccd.src.main.mypage.PetListRecyclerviewAdapter
import com.ssafy.ccd.util.CommonUtils
import kotlinx.coroutines.runBlocking

private const val TAG = "CalenderFragment"
class CalenderFragment : BaseFragment<FragmentCalenderBinding>(FragmentCalenderBinding::bind, R.layout.fragment_calender) {
    private lateinit var petAdapter: CalendarWritePetAdapter
    private lateinit var monthListAdapter : CalenderMonthAdapter
    private lateinit var mainActivity:MainActivity
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
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
            mainViewModel.getCalendarListbyUser(ApplicationClass.sharedPreferencesUtil.getUser().id)
            Log.d(TAG, "onViewCreated: ")
        }
        setListener()

        binding.fragmentCalenderWrite.setOnClickListener {
            this@CalenderFragment.findNavController().navigate(R.id.action_calenderFragment_to_calenderWriteFragment)
        }

    }
    fun setListener(){
        initSpinner()
        initCalendar(0)
        initAdapter(0)
        binding.fragmentCalendarAllList.setOnClickListener {
            petAdapter.selectItem = -1
            petAdapter.notifyDataSetChanged()
            binding.fragmentCalendarAllCheck.visibility = View.VISIBLE
            initCalendar(0)
        }

    }
    fun initSpinner(){
        var types = arrayListOf<String>("선택안함","접종","산책","기타")
        var adapter = ArrayAdapter(requireContext(),R.layout.support_simple_spinner_dropdown_item,types)
        binding.calendarFragmentTypeSpinner.adapter = adapter
        binding.calendarFragmentTypeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                initCalendar(position)
                initAdapter(position)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

        }
    }

    fun initAdapter(typeId:Int){
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
//                    Log.d(TAG, "onClick: ${id}")
                    petAdapter.selectItem = position
                    binding.fragmentCalendarAllCheck.visibility = View.INVISIBLE
                    petAdapter.notifyDataSetChanged()
                    initPetFilter(id,typeId)
                }
            })
        })

    }
    fun initPetFilter(petId:Int,typeId:Int){
        mainViewModel.calendarList.observe(viewLifecycleOwner, {
            var date = arrayListOf<String>()
            Log.d(TAG, "initPetFilter: ${typeId}")
            for(i in 0..it.size-1){
                if(typeId > 0){

                    if(it[i].type == typeId){
                        if(it[i].petId == petId){
                            date.add(CommonUtils.makeBirthString(it[i].datetime))
                        }
                    }
                }else{
                    if(it[i].petId == petId){
                        date.add(CommonUtils.makeBirthString(it[i].datetime))
                    }
                }

            }
            Log.d(TAG, "initCalendar: ${date}")
            monthListAdapter = CalenderMonthAdapter(requireContext(),date,mainViewModel,viewLifecycleOwner,this,mainActivity)

            binding.fragmentCalenderCustomCalender.apply {
                layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL,false)
                adapter = monthListAdapter
                scrollToPosition(Int.MAX_VALUE/2)
            }
            val snap = PagerSnapHelper()
            if( binding.fragmentCalenderCustomCalender.onFlingListener == null){
                snap.attachToRecyclerView(binding.fragmentCalenderCustomCalender)
            }

        })
    }
    fun initCalendar(type:Int){
        mainViewModel.calendarList.observe(viewLifecycleOwner, {
            var date = arrayListOf<String>()
            for(i in 0..it.size-1){
                if(type > 0){
                    if(it[i].type == type){
                        date.add(CommonUtils.makeBirthString(it[i].datetime))
                    }
                }else{
                    date.add(CommonUtils.makeBirthString(it[i].datetime))
                }

            }
            Log.d(TAG, "initCalendar: ${date}")
            monthListAdapter = CalenderMonthAdapter(requireContext(),date,mainViewModel,viewLifecycleOwner,this, mainActivity)

            binding.fragmentCalenderCustomCalender.apply {
                layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL,false)
                adapter = monthListAdapter
                scrollToPosition(Int.MAX_VALUE/2)
            }
            val snap = PagerSnapHelper()
            if( binding.fragmentCalenderCustomCalender.onFlingListener == null){
                snap.attachToRecyclerView(binding.fragmentCalenderCustomCalender)
            }
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