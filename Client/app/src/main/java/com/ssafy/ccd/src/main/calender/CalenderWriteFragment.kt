package com.ssafy.ccd.src.main.calender

import android.app.DatePickerDialog
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.ccd.R
import com.ssafy.ccd.config.ApplicationClass
import com.ssafy.ccd.config.BaseFragment
import com.ssafy.ccd.databinding.FragmentCalenderWriteBinding
import com.ssafy.ccd.src.main.MainActivity
import com.ssafy.ccd.src.network.service.CalendarService
import com.ssafy.ccd.util.CommonUtils
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.lang.Exception
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

private const val TAG = "CalenderWriteFragment"
class CalenderWriteFragment : BaseFragment<FragmentCalenderWriteBinding>(FragmentCalenderWriteBinding::bind, R.layout.fragment_calender_write) {
    private lateinit var petAdapter:CalendarWritePetAdapter
    var type = 0
    var curDate = Date()
    val dataFormat: SimpleDateFormat = SimpleDateFormat("yyyy년 MM월 dd일")
    var petId = -1
    private lateinit var mainActivity : MainActivity

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
        mainActivity.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
    }


    override fun onResume() {
        super.onResume()
        mainActivity.hideBottomNavi(true)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainActivity.hideBottomNavi(true)
        binding.viewModel = mainViewModel
        runBlocking {
            mainViewModel.getMyPetsAllList(ApplicationClass.sharedPreferencesUtil.getUser().id)
        }
        binding.fragmentCalenderWriteSuccessBtn.setOnClickListener {
            var date = binding.fragmentCalendarWriteDate.text.toString()
            var title = binding.fragmentCalendarWriteTitle.text.toString()
            var content = binding.fragmentCalendarWriteMemo.text.toString()
            var place = binding.fragmentCalendarWritePlace.text.toString()
            if(type == -1 || type == 0){
                showCustomToast("다시 선택해주세요!")
            }else{
                var calendar = com.ssafy.ccd.src.dto.Calendar(
                    CommonUtils.makeBirthMilliSecond(date),
                    0,
                    content,
                    petId,
                    title,
                    type,
                    ApplicationClass.sharedPreferencesUtil.getUser().id,
                    place
                )
                insertCalendar(calendar)
            }

        }
        binding.fragmentCalendarWriteDatePicker.setOnClickListener {
            initDatePicker()
        }
        binding.fragmentCalenderWriteBack.setOnClickListener {
            this@CalenderWriteFragment.findNavController().popBackStack()
        }
        setLinstener()
    }
    fun insertCalendar(calendar:com.ssafy.ccd.src.dto.Calendar){
        GlobalScope.launch { 
            var response = CalendarService().insertCalendar(calendar)
            val res = response.body()
            if(response.code() == 200){
                if(res!=null){
                    if(res.success){
                        mainActivity.runOnUiThread(Runnable {
                            this@CalenderWriteFragment.findNavController().navigate(R.id.action_calenderWriteFragment_to_calenderFragment)
                        })
                    }
                }
            }else{
                Log.d(TAG, "insertCalendar: ${response.code()}")
            }
        }
    }
    @RequiresApi(Build.VERSION_CODES.O)
    fun setLinstener(){
        var now = LocalDateTime.now()
        binding.fragmentCalendarWriteDate.setText(now.format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일")))
        initAdapter()
        initSpinner()
    }
    fun initDatePicker(){
        val calendar:Calendar = Calendar.getInstance()
        try{
            curDate = dataFormat.parse(binding.fragmentCalendarWriteDate.text.toString())
        }catch (e:Exception){
            e.printStackTrace()
        }

        calendar.time = curDate
        val curYear = calendar.get(Calendar.YEAR)
        val curMonth = calendar.get(Calendar.MONTH)
        val curDay = calendar.get(Calendar.DAY_OF_MONTH)

        val dialog = DatePickerDialog(requireContext(), dateSetListener, curYear, curMonth, curDay)
        dialog.show()
    }
    private val dateSetListener =
        DatePickerDialog.OnDateSetListener {datePicker, i, i2, i3 ->
            val selectedCalendar = Calendar.getInstance()
            selectedCalendar[Calendar.YEAR] = i
            selectedCalendar[Calendar.MONTH] = i2
            selectedCalendar[Calendar.DAY_OF_MONTH] = i3

            val curDate = selectedCalendar.time
            setSelectedDate(curDate)
        }
    private fun setSelectedDate(curDate: Date){
        val selectedDateStr = dataFormat.format(curDate)
        binding.fragmentCalendarWriteDate.setText(selectedDateStr)
    }
    fun initSpinner(){
        var typeList = listOf("선택안함","접종","산책","기타") //0123
        var adapter = ArrayAdapter(requireContext(),R.layout.support_simple_spinner_dropdown_item,typeList)
        binding.fragmentCalendarWriteType.adapter = adapter

        binding.fragmentCalendarWriteType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                type = position
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

        }

    }
    fun initAdapter(){
        mainViewModel.myPetsList.observe(viewLifecycleOwner, {
            petAdapter = CalendarWritePetAdapter()
            petAdapter.list = it
            binding.fragmentCalendarWriteRvPet.apply{
                layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)
                adapter = petAdapter
                adapter!!.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
            }
            petAdapter.setItemClickListener(object: CalendarWritePetAdapter.ItemClickListener{
                override fun onClick(view: View, position: Int, id: Int) {
                    petId = id
                    petAdapter.selectItem = position
                    petAdapter.notifyDataSetChanged()
                }

            })
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mainActivity.hideBottomNavi(false)
    }
}