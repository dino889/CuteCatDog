package com.ssafy.ccd.src.main.mypage

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.ccd.R
import com.ssafy.ccd.config.BaseFragment
import com.ssafy.ccd.databinding.FragmentMyScheduleBinding
import com.ssafy.ccd.src.dto.Pet
import com.ssafy.ccd.src.main.MainActivity

class MyScheduleFragment : BaseFragment<FragmentMyScheduleBinding>(FragmentMyScheduleBinding::bind, R.layout.fragment_my_schedule) {
    private lateinit var mainActivity : MainActivity
    private lateinit var myScheduleRecyclerviewAdapter: MyScheduleRecyclerviewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initRecyclerviewAdapter()
    }

    private fun initRecyclerviewAdapter() {
//
//        myScheduleRecyclerviewAdapter = MyScheduleRecyclerviewAdapter()
//        myScheduleRecyclerviewAdapter.setItemClickListener(object : MyScheduleRecyclerviewAdapter.ItemClickListener {
//            override fun onClick(view: View, position: Int, id: Int) {
//                showCustomToast(id.toString())
//            }
//        })
//        binding.myScheduleFragmentRv.apply {
//            layoutManager = LinearLayoutManager(requireContext())
//            adapter = myScheduleRecyclerviewAdapter
//            adapter!!.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
//        }
    }
}