package com.ssafy.ccd.src.main.calender

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.ssafy.ccd.R
import com.ssafy.ccd.config.BaseFragment
import com.ssafy.ccd.databinding.FragmentCalendarDetailBinding
import com.ssafy.ccd.src.main.MainActivity
import kotlinx.coroutines.runBlocking
import net.daum.mf.map.api.MapView

private const val TAG = "CalendarDetailFragment"
class CalendarDetailFragment : BaseFragment<FragmentCalendarDetailBinding>(FragmentCalendarDetailBinding::bind,R.layout.fragment_calendar_detail) {
    private lateinit var mainActivity:MainActivity
    private var calendarId = -1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.apply {
            calendarId = getInt("calendarId")
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "onViewCreated: ${calendarId}")
        binding.viewModel = mainViewModel
        runBlocking {
            mainViewModel.getCalendarDetail(calendarId)
        }
        setListener()
    }
    fun setListener(){
        initButtonClick()

        checkType()
    }
    fun checkType(){
        var type = mainViewModel.scheduleDetail.value!!.schedule.type
        if(type == 2){
            initKaKaoMap()
        }else{
            binding.kakaoMapView.visibility = View.GONE
        }
    }
    fun initKaKaoMap(){
        var mapView = MapView(requireContext())
        var mapViewContainer = binding.kakaoMapView as ViewGroup
        mapViewContainer.addView(mapView)
    }
    fun initButtonClick(){

        binding.fragmentCalenderDetailBack.setOnClickListener {
            this@CalendarDetailFragment.findNavController().popBackStack()
        }
    }
    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            CalendarDetailFragment().apply {
            }
    }

}