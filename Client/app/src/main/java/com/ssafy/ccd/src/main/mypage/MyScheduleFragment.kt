package com.ssafy.ccd.src.main.mypage

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.ccd.R
import com.ssafy.ccd.config.BaseFragment
import com.ssafy.ccd.databinding.FragmentMyScheduleBinding
import com.ssafy.ccd.src.dto.Pet

class MyScheduleFragment : BaseFragment<FragmentMyScheduleBinding>(FragmentMyScheduleBinding::bind, R.layout.fragment_my_schedule) {

    private lateinit var myScheduleRecyclerviewAdapter: MyScheduleRecyclerviewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initRecyclerviewAdapter()
    }

    private fun initRecyclerviewAdapter() {
        val scheduleList = mutableListOf<Pet>()
        scheduleList.add(Pet(1, "1"))
        scheduleList.add(Pet(2, "2"))
        myScheduleRecyclerviewAdapter = MyScheduleRecyclerviewAdapter(scheduleList)
        myScheduleRecyclerviewAdapter.setItemClickListener(object : MyScheduleRecyclerviewAdapter.ItemClickListener {
            override fun onClick(view: View, position: Int, id: Int) {
                showCustomToast(id.toString())
            }
        })
        binding.myScheduleFragmentRv.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = myScheduleRecyclerviewAdapter
            adapter!!.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
        }
    }
}