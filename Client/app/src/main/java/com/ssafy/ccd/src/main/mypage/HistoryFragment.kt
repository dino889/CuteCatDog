package com.ssafy.ccd.src.main.mypage

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.ccd.R
import com.ssafy.ccd.config.ApplicationClass
import com.ssafy.ccd.config.BaseFragment
import com.ssafy.ccd.databinding.FragmentHistoryBinding
import com.ssafy.ccd.src.dto.Message
import com.ssafy.ccd.src.main.MainActivity
import com.ssafy.ccd.src.network.service.HistoryService
import kotlinx.coroutines.runBlocking
import retrofit2.Response


class HistoryFragment : BaseFragment<FragmentHistoryBinding>(FragmentHistoryBinding::bind, R.layout.fragment_history) {
    private val TAG = "HistoryFragment_ccd"

    private lateinit var mainActivity : MainActivity
    private lateinit var myHistoryRecyclerviewAdapter: MyHistoryRecyclerviewAdapter

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initAdapter()
    }

    private fun initAdapter() {
        runBlocking {
            mainViewModel.getHistoryList(ApplicationClass.sharedPreferencesUtil.getUser().id)
        }

        mainViewModel.historyList.observe(viewLifecycleOwner, {
            myHistoryRecyclerviewAdapter = MyHistoryRecyclerviewAdapter()
            myHistoryRecyclerviewAdapter.historyList = it
            myHistoryRecyclerviewAdapter.setItemClickListener(object : MyHistoryRecyclerviewAdapter.ItemClickListener {
                override fun onClick(view: View, position: Int) {
                    deleteHistory(it[position].id)
                }

            })
            binding.myHistoryFragmentRv.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = myHistoryRecyclerviewAdapter
                adapter!!.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
            }
        })
    }

    private fun deleteHistory(id: Int) {
        var response : Response<Message>
        runBlocking {
            response = HistoryService().deleteHistory(id)
        }
        if(response.code() == 200 || response.code() == 500) {
            val res = response.body()
            if(res != null) {
                if(res.success && res.data["isSuccess"] == true) {
                    showCustomToast("삭제되었습니다.")
                    runBlocking {
                        mainViewModel.getHistoryList(ApplicationClass.sharedPreferencesUtil.getUser().id)
                    }
                    myHistoryRecyclerviewAdapter.notifyDataSetChanged()
                } else if(res.data["isSuccess"] == false) {
                    showCustomToast("삭제 실패")
                    Log.e(TAG, "deleteHistory: ${res.message}", )
                } else {
                    showCustomToast("서버 통신 실패")
                }
            }
        }
    }
}