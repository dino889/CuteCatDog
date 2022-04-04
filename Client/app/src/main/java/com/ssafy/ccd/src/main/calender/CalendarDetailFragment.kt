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
        if(mapView.parent!=null){
            (mapView.parent as ViewGroup).removeView(mapView)
        }

        var mapViewContainer = binding.kakaoMapView as ViewGroup
        mapViewContainer.addView(mapView)
        val mapPoint = MapPoint.mapPointWithGeoCoord(mainViewModel.userLoc!!.latitude, mainViewModel.userLoc!!.longitude)
        mapView.setMapCenterPoint(mapPoint,true)
        mapView.setZoomLevel(4,true)
        var marker = MapPOIItem()
        marker.itemName = "Current MyLocation"
        marker.mapPoint = mapPoint
        marker.markerType = MapPOIItem.MarkerType.RedPin
        mapView.addPOIItem(marker)
        var poiItem = arrayListOf<MapPOIItem>()
        binding.fragmentCalendarDetailRecommandWalk.visibility = View.GONE
        if(mainViewModel.walk.value!!.isEmpty()){
            binding.fragmentCalendarDetailRecommandWalk.visibility = View.VISIBLE
            binding.fragmentCalendarDetailRecommandWalk.text = " 주변에 추천할 산책장소가 없습니다 :( "
        }else{
            binding.fragmentCalendarDetailRecommandWalk.visibility = View.GONE
            mainViewModel.walk.observe(viewLifecycleOwner, {
                Log.d(TAG, "initKaKaoMap: ${it}")
                for(i in 0..it.size-1){
                    var mapPoint = MapPoint.mapPointWithGeoCoord(it[i].lat.toDouble(),it[i].lng.toDouble())
                    val marker = MapPOIItem()
                    marker.itemName = it[i].place
                    marker.mapPoint = mapPoint
                    marker.markerType = MapPOIItem.MarkerType.BluePin
                }
            })
            mapView.addPOIItems(poiItem.toArray(arrayOfNulls(poiItem.size)))
        }

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