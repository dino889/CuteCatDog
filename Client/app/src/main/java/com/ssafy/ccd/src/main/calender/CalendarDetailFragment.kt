package com.ssafy.ccd.src.main.calender

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.google.android.youtube.player.internal.o
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
//            mainViewModel.recommandWalkSapce(35.931075, 128.573721)
        }
        setListener()

    }
    fun setListener(){
        initButtonClick()
        checkType()
        modifyBtnClickEvent()
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
        if(mapView.parent!=null){
            (mapView.parent as ViewGroup).removeView(mapView)
        }

        var mapViewContainer = binding.kakaoMapView as ViewGroup
        mapViewContainer.addView(mapView)
        var mapPoint = MapPoint.mapPointWithGeoCoord(mainViewModel.userLoc!!.latitude, mainViewModel.userLoc!!.longitude)
//        mapPoint = MapPoint.mapPointWithGeoCoord(35.931075, 128.573721)
        mapView.setMapCenterPoint(mapPoint,true)
        mapView.setZoomLevel(4,true)
        var marker = MapPOIItem()
        marker.itemName = "Current MyLocation"
        marker.mapPoint = mapPoint
        marker.markerType = MapPOIItem.MarkerType.RedPin
        mapView.addPOIItem(marker)
        var poiItem = arrayListOf<MapPOIItem>()
        var markerArr = arrayListOf<MapPoint>()

        binding.fragmentCalendarDetailRecommandWalk.visibility = View.GONE
        if(mainViewModel.walk.value!!.isEmpty()){
            binding.fragmentCalendarDetailRecommandWalk.visibility = View.VISIBLE
            binding.fragmentCalendarDetailRecommandWalk.text = " 주변에 추천할 산책장소가 없습니다 :( "
        }else{
            binding.fragmentCalendarDetailRecommandWalk.visibility = View.GONE

            for (item in mainViewModel.walk.value!!) {
                val lat = item.lat
                val lng = item.lng
                var mapPoint = MapPoint.mapPointWithGeoCoord(lat,lng)
                markerArr.add(mapPoint)
            }

            for(i in 0..markerArr.size-1){
                val marker = MapPOIItem()
                marker.itemName = mainViewModel.walk.value!![i].place
                marker.mapPoint = markerArr[i]
                marker.markerType = MapPOIItem.MarkerType.BluePin
                poiItem.add(marker)
            }
            mapView.addPOIItems(poiItem.toArray(arrayOfNulls(poiItem.size)))
        }

    }

    fun initButtonClick(){
        binding.fragmentCalenderDetailBack.setOnClickListener {
            this@CalendarDetailFragment.findNavController().popBackStack()
        }
    }

    private fun modifyBtnClickEvent() {
        binding.calendarDetailFragmentTvModify.setOnClickListener {
            this@CalendarDetailFragment.findNavController().navigate(R.id.action_calendarDetailFragment_to_calendarWriteFragment, bundleOf("scheduleId" to calendarId))
            Log.d(TAG, "modifyBtnClickEvent: $calendarId")
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            CalendarDetailFragment().apply {
            }
    }
//
//    override fun onResume() {
//        super.onResume()
//        var mapView = MapView(requireContext())
//        binding.kakaoMapView.addView(mapView)
//    }
//
//    override fun onPause() {
//        super.onPause()
//        var mapView = MapView(requireContext())
//        binding.kakaoMapView.removeView(mapView)
//    }
}