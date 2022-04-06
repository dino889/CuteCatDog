package com.ssafy.ccd.src.main.mypage

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.ccd.R
import com.ssafy.ccd.config.ApplicationClass
import com.ssafy.ccd.config.BaseFragment
import com.ssafy.ccd.databinding.FragmentMyPostBinding
import com.ssafy.ccd.src.dto.Pet
import kotlinx.coroutines.runBlocking

private const val TAG = "MyPostFragment"
class MyPostFragment : BaseFragment<FragmentMyPostBinding>(FragmentMyPostBinding::bind, R.layout.fragment_my_post) {

    private lateinit var myPostRecyclerviewAdapter: MyPostRecyclerviewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = mainViewModel
        runBlocking {
            mainViewModel.getBoardListByUser(ApplicationClass.sharedPreferencesUtil.getUser().id)
        }
        initRecyclerviewAdapter()
    }

    private fun initRecyclerviewAdapter() {
        mainViewModel.boardListByUser.observe(viewLifecycleOwner, {
            myPostRecyclerviewAdapter = MyPostRecyclerviewAdapter()
            myPostRecyclerviewAdapter.list = it
            Log.d(TAG, "initRecyclerviewAdapter: ${it}")
//            myPostRecyclerviewAdapter.setItemClickListener(object : MyPostRecyclerviewAdapter.ItemClickListener {
//                override fun onClick(view: View, position: Int, id: Int) {
//                    showCustomToast(id.toString())
//                }
//            })
            binding.myPostFragmentRv.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = myPostRecyclerviewAdapter
                adapter!!.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
            }
        })

    }
}