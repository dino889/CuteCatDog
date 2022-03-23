package com.ssafy.ccd.src.main.home.Community

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.ssafy.ccd.R
import com.ssafy.ccd.config.BaseFragment
import com.ssafy.ccd.databinding.FragmentQnaBoardBinding
import kotlinx.coroutines.runBlocking

/**
 * @author Jiwoo
 * @since 03/23/22
 * '궁금해' 게시판
 */
class QnABoardFragment : BaseFragment<FragmentQnaBoardBinding>(FragmentQnaBoardBinding::bind,R.layout.fragment_qna_board) {
    private lateinit var qnaBoardAdapter: QnABoardAdapter

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
            mainViewModel.getPostListByType(2)
        }
        mainViewModel.qnaPostList.observe(viewLifecycleOwner, {
            binding.fragmentQnaRv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            qnaBoardAdapter = QnABoardAdapter(it, mainViewModel.allUserList.value!!, mainViewModel.likePostsByUserId.value!!, requireContext())
            binding.fragmentQnaRv.adapter = qnaBoardAdapter
        })
    }

    private fun backBtnClickEvent() {
        binding.fragmentQnaBack.setOnClickListener {
            this@QnABoardFragment.findNavController().popBackStack()
        }
    }

    private fun writeBtnClickEvent() {
        binding.fragmentQnaWrite.setOnClickListener {
            this@QnABoardFragment.findNavController().navigate(R.id.action_qnaBoardFragment_to_writeBoardFragment)
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            QnABoardFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }
}