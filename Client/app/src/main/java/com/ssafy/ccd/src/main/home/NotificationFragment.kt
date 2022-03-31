package com.ssafy.ccd.src.main.home

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.ccd.R
import com.ssafy.ccd.config.ApplicationClass
import com.ssafy.ccd.config.BaseFragment
import com.ssafy.ccd.databinding.FragmentNotificationBinding
import com.ssafy.ccd.src.main.MainActivity
import kotlinx.coroutines.runBlocking

class NotificationFragment : BaseFragment<FragmentNotificationBinding>(FragmentNotificationBinding::bind,R.layout.fragment_notification) {
    private lateinit var mainActivity : MainActivity
    private lateinit var notiAdapter:NotificationAdapter
    var userId = ApplicationClass.sharedPreferencesUtil.getUser().id
    var typeId = -1

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainActivity.hideBottomNavi(true)

        binding.viewModel = mainViewModel
        runBlocking {
            mainViewModel.getNotificationAll(userId)
        }
        initSpinner()
        initAdapter()

        binding.fragmentNotiBack.setOnClickListener {
            this@NotificationFragment.findNavController().popBackStack()
        }
    }
    fun initSpinner(){
        var filtering = arrayListOf<String>("전체","공지사항","이벤트","개인")
        var adapter = ArrayAdapter(requireContext(),android.R.layout.simple_dropdown_item_1line,filtering)
        binding.fragmentNotiSpinner.adapter = adapter

        binding.fragmentNotiSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if(position == 0){
                    runBlocking {
                        mainViewModel.getNotificationAll(userId)
                    }
                }else if(position == 1){
                    runBlocking {
                        mainViewModel.getNotificationNotice(userId)
                    }
                }else if(position == 2){
                    runBlocking {
                        mainViewModel.getNotificationEvent(userId)
                    }
                }else if(position == 3){
                    runBlocking {
                        mainViewModel.getNotificationSchedule(userId)
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

        }
    }
    fun initAdapter(){
        mainViewModel.notificationList.observe(viewLifecycleOwner, {
            notiAdapter = NotificationAdapter()
            notiAdapter.list = it
            binding.fragmentNotiRv.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = notiAdapter
                adapter!!.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
            }
        })

    }

    override fun onDestroy() {
        super.onDestroy()
        mainActivity.hideBottomNavi(false)
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            NotificationFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }
}