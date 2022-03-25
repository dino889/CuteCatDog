package com.ssafy.ccd.src.main.home.Community

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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

    // Binding
    private lateinit var rvQna : RecyclerView
    private lateinit var qnaBoardAdapter: QnABoardAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setInstance()
        setListener()
        initRecyclerView()
    }

    /**
     * @author Jiwoo
     * 리스너 설정
     */
    private fun setListener() {
        binding.fragmentQnaBack.setOnClickListener {
            this@QnABoardFragment.findNavController().popBackStack()
        }

        binding.fragmentQnaWrite.setOnClickListener {
            mainViewModel.writeType = 0
            this@QnABoardFragment.findNavController().navigate(R.id.writeQnaFragment)
        }
    }

    /**
     * @author Jiwoo
     * 객체 생성
     */
    private fun setInstance() {
        rvQna = binding.fragmentQnaRv
        qnaBoardAdapter = QnABoardAdapter(mutableListOf(), mainViewModel.allUserList.value!!, mainViewModel.likePostsByUserId.value!!, requireContext())
    }


    /**
     * @author Jiwoo
     * RecyclerView 셋팅
     */
    private fun initRecyclerView() {
        runBlocking {
            mainViewModel.getPostListByType(2)
        }

        rvQna.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            binding.fragmentQnaRv.adapter = qnaBoardAdapter
        }

        mainViewModel.qnaPostList.observe(viewLifecycleOwner) {
            qnaBoardAdapter.updateData(it)
        }

        qnaBoardAdapter.setItemClickListener(object : QnABoardAdapter.ItemClickListener{
            override fun onClick(view: View, position: Int) {
                mainViewModel.boardQna = qnaBoardAdapter.postList[position]
                this@QnABoardFragment.findNavController().navigate(R.id.qnABoardDetailFragment)
            }
        })
    }
}