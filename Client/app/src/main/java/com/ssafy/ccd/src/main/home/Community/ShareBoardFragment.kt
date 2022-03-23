package com.ssafy.ccd.src.main.home.Community

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.ssafy.ccd.R
import com.ssafy.ccd.config.BaseFragment
import com.ssafy.ccd.databinding.FragmentLocalBoardBinding
import com.ssafy.ccd.databinding.FragmentShareBoardBinding
import com.ssafy.ccd.src.main.home.BoardAdapter
import kotlinx.coroutines.runBlocking

/**
 * @author Jiwoo
 * @since 03/23/22
 * '공유해' 게시판
 */
class ShareBoardFragment : BaseFragment<FragmentShareBoardBinding>(FragmentShareBoardBinding::bind,R.layout.fragment_share_board) {
    private lateinit var shareBoardAdapter: ShareBoardAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()
        backBtnClickEvent()
        writeBtnClickEvent()
    }

    private fun initRecyclerView() {
        runBlocking {
            mainViewModel.getPostListByType(3)
        }
        mainViewModel.sharePostList.observe(viewLifecycleOwner, {
            binding.shareBoardFragmentRvPostList.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            shareBoardAdapter = ShareBoardAdapter(it, mainViewModel.allUserList.value!!, mainViewModel.likePostsByUserId.value!!, requireContext())
            binding.shareBoardFragmentRvPostList.adapter = shareBoardAdapter
        })
    }

    private fun backBtnClickEvent() {
        binding.shareFragmentIbBack.setOnClickListener {
            this@ShareBoardFragment.findNavController().popBackStack()
        }
    }

    private fun writeBtnClickEvent() {
        binding.shareFragmentIbWrite.setOnClickListener {
            this@ShareBoardFragment.findNavController().navigate(R.id.action_shareBoardFragment_to_writeBoardFragment)
        }
    }
}