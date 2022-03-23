package com.ssafy.ccd.src.main.home.Community

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.ssafy.ccd.R
import com.ssafy.ccd.config.BaseFragment
import com.ssafy.ccd.databinding.FragmentLocalBoardBinding
import com.ssafy.ccd.src.main.home.BoardAdapter
import kotlinx.coroutines.runBlocking

class LocalBoardFragment : BaseFragment<FragmentLocalBoardBinding>(FragmentLocalBoardBinding::bind,R.layout.fragment_local_board) {
    private lateinit var localBoardAdapter: LocalBoardAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()
    }

    private fun initRecyclerView() {
        runBlocking {
            mainViewModel.getPostListByType(1)
        }
        mainViewModel.locPostList.observe(viewLifecycleOwner, {
            binding.localBoardFragmentRvPostList.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            localBoardAdapter = LocalBoardAdapter(it, mainViewModel.allUserList.value!!, mainViewModel.likePostsByUserId.value!!, requireContext())
            binding.localBoardFragmentRvPostList.adapter = localBoardAdapter
        })
    }


}