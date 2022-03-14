package com.ssafy.ccd.src.main.mypage

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ssafy.ccd.R
import com.ssafy.ccd.config.BaseFragment
import com.ssafy.ccd.databinding.FragmentAddPetBinding
import com.ssafy.ccd.databinding.FragmentMyPageBinding
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*
import android.widget.DatePicker





class AddPetFragment : BaseFragment<FragmentAddPetBinding>(FragmentAddPetBinding::bind, R.layout.fragment_add_pet) {
    var curDate = Date() // 현재

    val dataFormat: SimpleDateFormat = SimpleDateFormat("yyyy년 MM월 dd일")

    // SimpleDateFormat 으로 포맷 결정
    var result: String = dataFormat.format(curDate)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.addPetFragmentTietBirth.setText(result)

        binding.addPetFragmentTietBirth.setOnClickListener {
            setBirth()
        }

    }


    private fun setBirth() {

        val calendar: Calendar = Calendar.getInstance()
        try {
            curDate = dataFormat.parse(binding.addPetFragmentTietBirth.getText().toString())
            // 문자열로 된 생년월일을 Date로 파싱
        } catch (ex: Exception) {
            ex.printStackTrace()
        }

        calendar.setTime(curDate)

        val curYear: Int = calendar.get(Calendar.YEAR)
        val curMonth: Int = calendar.get(Calendar.MONTH)
        val curDay: Int = calendar.get(Calendar.DAY_OF_MONTH)
        // 년,월,일 넘겨줄 변수

        // 년,월,일 넘겨줄 변수
        val dialog = DatePickerDialog(requireContext(), birthDateSetListener, curYear, curMonth, curDay)
        dialog.show()
    }

    private val birthDateSetListener =
        OnDateSetListener { datePicker, year, month, day ->
            val selectedCalendar = Calendar.getInstance()
            selectedCalendar[Calendar.YEAR] = year
            selectedCalendar[Calendar.MONTH] = month
            selectedCalendar[Calendar.DAY_OF_MONTH] = day
            // 달력의 년월일을 버튼에서 넘겨받은 년월일로 설정
            val curDate = selectedCalendar.time // 현재를 넘겨줌
            setSelectedDate(curDate)
        }

    private fun setSelectedDate(curDate: Date) {
        val selectedDateStr = dataFormat.format(curDate)
        binding.addPetFragmentTietBirth.setText(selectedDateStr) // 버튼의 텍스트 수정
    }
}