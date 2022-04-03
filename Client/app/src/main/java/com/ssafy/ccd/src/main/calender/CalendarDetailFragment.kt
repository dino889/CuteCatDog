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
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint
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
            mainViewModel.recommandWalkSapce(mainViewModel.userLoc!!.latitude, mainViewModel.userLoc!!.longitude)
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

        mapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(mainViewModel.userLoc!!.latitude, mainViewModel.userLoc!!.longitude),true)
        mapView.setZoomLevel(7,true)

//        var markerArr = arrayListOf<MapPoint>()
        var poiItem = arrayListOf<MapPOIItem>()
        mainViewModel.walk.observe(viewLifecycleOwner, {
            for(i in 0..it.size-1){
                var mapPoint = MapPoint.mapPointWithGeoCoord(it[i].lat.toDouble(),it[i].lng.toDouble())
//                markerArr.add(mapPoint)
                val marker = MapPOIItem()
                marker.itemName = it[i].place
                marker.mapPoint = mapPoint
            }
        })
        mapView.addPOIItems(poiItem.toArray(arrayOfNulls(poiItem.size)))


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