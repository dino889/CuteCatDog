package com.ssafy.ccd.src.main.diary

import android.app.DatePickerDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener
import com.ssafy.ccd.R
import com.ssafy.ccd.config.ApplicationClass
import com.ssafy.ccd.config.BaseFragment
import com.ssafy.ccd.databinding.FragmentDiaryBinding
import com.ssafy.ccd.src.main.MainActivity
import kotlinx.coroutines.runBlocking
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.*
import androidx.core.util.Pair
import com.ssafy.ccd.util.CommonUtils

private const val TAG = "DiaryFragment"
class DiaryFragment : BaseFragment<FragmentDiaryBinding>(FragmentDiaryBinding::bind, R.layout.fragment_diary) {
    private lateinit var diaryAdapter:DiaryAdapter
    private lateinit var mainActivity : MainActivity
    private lateinit var selectDay:String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }
    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = mainViewModel
        runBlocking {
            mainViewModel.getDiaryList(ApplicationClass.sharedPreferencesUtil.getUser().id)
        }
        initAdapter()
        binding.fragmentDiarySearch.setOnClickListener {
            showSortDialog()
        }

        binding.fragmentDiaryWrite.setOnClickListener {
            this@DiaryFragment.findNavController().navigate(R.id.action_diaryFragment_to_diaryWriteFragment)
        }
    }
    fun initAdapter(){
        mainViewModel.diaryList.observe(viewLifecycleOwner) {
            Log.d(TAG, "initAdapter: $it")
            diaryAdapter = DiaryAdapter(requireContext(), mainViewModel, viewLifecycleOwner)
            diaryAdapter.list = it

            binding.fragmentDiaryRv.apply {
                layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                adapter = diaryAdapter
                adapter!!.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
            }
            diaryAdapter.setItemClickListener(object : DiaryAdapter.ItemClickListener {
                override fun onClick(view: View, position: Int, id: Int) {
                    mainActivity.runOnUiThread {
                        Log.d(TAG, "onClick: ${id}")
//                        setFragmentResult("diaryId", bundleOf("diaryId" to id))
                        val diaryId = bundleOf("diaryId" to id)
                        this@DiaryFragment.findNavController().navigate(R.id.action_diaryFragment_to_diaryDetailFragment, diaryId)
                    }
                }

            })
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun showSortDialog(){
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.fragment_diary_sortbottom_sheet,null)
        val dialog = BottomSheetDialog(requireContext())
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val param = WindowManager.LayoutParams()
        param.width = WindowManager.LayoutParams.MATCH_PARENT
        param.height = WindowManager.LayoutParams.WRAP_CONTENT
        val window = dialog.window
        //날짜별 검색하기
        dialogView.findViewById<TextView>(R.id.fragment_diary_dialog_Day).setOnClickListener {
            showDatePicker()
            initAdapter()
            dialog.dismiss()
        }
        //기간별 검색하기
        dialogView.findViewById<TextView>(R.id.fragment_diary_dialog_Range).setOnClickListener {
            showDateRangePicker()
            initAdapter()
            dialog.dismiss()
        }
        //오름차순 검색하기
        dialogView.findViewById<TextView>(R.id.fragment_diary_dialog_Asc).setOnClickListener {
            runBlocking {
                mainViewModel.getDiaryListAsc(ApplicationClass.sharedPreferencesUtil.getUser().id)
            }
            initAdapter()
            dialog.dismiss()
        }
        //내림차순 검색하기
        dialogView.findViewById<TextView>(R.id.fragment_diary_dialog_Desc).setOnClickListener {
            runBlocking {
                mainViewModel.getDiaryList(ApplicationClass.sharedPreferencesUtil.getUser().id)
            }
            initAdapter()
            dialog.dismiss()
        }
        dialog.setContentView(dialogView)
        dialog.show()
    }
    @RequiresApi(Build.VERSION_CODES.O)
    private val dateSetListener =
        DatePickerDialog.OnDateSetListener {datePicker, i, i2, i3 ->
            val selectedCalendar = Calendar.getInstance()
            selectedCalendar[Calendar.YEAR] = i
            selectedCalendar[Calendar.MONTH] = i2
            selectedCalendar[Calendar.DAY_OF_MONTH] = i3

            val curDate = selectedCalendar.time
            var tmpDate = SimpleDateFormat("yyyy년 MM월 dd일")

            selectDay = CommonUtils.makeBirthMilliSecond(tmpDate.format(curDate))
            Log.d(TAG, "${selectDay}: ")
            runBlocking {
                mainViewModel.getDiaryListDate("",selectDay,ApplicationClass.sharedPreferencesUtil.getUser().id)
            }
        }
    @RequiresApi(Build.VERSION_CODES.O)
    fun showDatePicker(){
        var calendar = Calendar.getInstance()

        val curYear = calendar.get(Calendar.YEAR)
        val curMonth = calendar.get(Calendar.MONTH)
        val curDay = calendar.get(Calendar.DAY_OF_MONTH)

        val dialog = DatePickerDialog(requireContext(), dateSetListener, curYear, curMonth, curDay)
        dialog.show()
    }
    fun showDateRangePicker(){

        var dateRangePicker = MaterialDatePicker.Builder.dateRangePicker()
            .setTitleText("검색기간을 선택하세요")
//            .setSelection(
//                Pair(
//                    MaterialDatePicker.thisMonthInUtcMilliseconds(),
//                    MaterialDatePicker.todayInUtcMilliseconds()
//                )
//            )
            .build()
        dateRangePicker.show(childFragmentManager, "date_picker")
        dateRangePicker.addOnPositiveButtonClickListener(object :
            MaterialPickerOnPositiveButtonClickListener<Pair<Long,Long>> {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onPositiveButtonClick(selection: Pair<Long, Long>?) {
                val calendar = Calendar.getInstance()
                calendar.timeInMillis = selection?.first ?: 0
                var startDate = CommonUtils.makeBirthMilliSecond(SimpleDateFormat("yyyy년 MM월 dd일").format(calendar.time).toString())
                calendar.timeInMillis = selection?.second ?: 0
                var endDate = CommonUtils.makeBirthMilliSecond(SimpleDateFormat("yyyy년 MM월 dd일").format(calendar.time).toString())

                runBlocking {
                    mainViewModel.getDiaryListDate(endDate,startDate,ApplicationClass.sharedPreferencesUtil.getUser().id)
                }
            }

        })
    }
    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            DiaryFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}