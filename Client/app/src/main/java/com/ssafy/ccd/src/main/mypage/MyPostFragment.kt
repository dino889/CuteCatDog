package com.ssafy.ccd.src.main.mypage

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.ccd.R
import com.ssafy.ccd.config.BaseFragment
import com.ssafy.ccd.databinding.FragmentMyPostBinding
import com.ssafy.ccd.src.dto.Pet


class MyPostFragment : BaseFragment<FragmentMyPostBinding>(FragmentMyPostBinding::bind, R.layout.fragment_my_post) {

    private lateinit var myPostRecyclerviewAdapter: MyPostRecyclerviewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initRecyclerviewAdapter()
    }

    private fun initRecyclerviewAdapter() {
        val postList = mutableListOf<Pet>()
//        postList.add(Pet(1, "1"))
//        postList.add(Pet(2, "2"))
        myPostRecyclerviewAdapter = MyPostRecyclerviewAdapter(postList = postList)
        myPostRecyclerviewAdapter.setItemClickListener(object : MyPostRecyclerviewAdapter.ItemClickListener {
            override fun onClick(view: View, position: Int, id: Int) {
                showCustomToast(id.toString())
            }
        })
        binding.myPostFragmentRv.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = myPostRecyclerviewAdapter
            adapter!!.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
        }
    }
}