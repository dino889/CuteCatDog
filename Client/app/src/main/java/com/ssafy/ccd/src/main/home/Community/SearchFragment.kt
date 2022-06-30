package com.ssafy.ccd.src.main.home.Community

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.SearchView
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.ccd.R
import com.ssafy.ccd.config.BaseFragment
import com.ssafy.ccd.databinding.FragmentSearchBinding
import com.ssafy.ccd.src.main.MainActivity
import kotlinx.coroutines.runBlocking

class SearchFragment : BaseFragment<FragmentSearchBinding>(FragmentSearchBinding::bind,R.layout.fragment_search) {
    private lateinit var searchAdapter:SearchAdapter
    private lateinit var mainActivity : MainActivity
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }
    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = mainViewModel
        runBlocking {
            mainViewModel.getAllPostList()
        }
        binding.searchFragmentIbBack.setOnClickListener {
            this@SearchFragment.findNavController().popBackStack()
        }
        initSearch()
        initAdapter()
    }
    fun initSearch(){
        binding.searchFragmentSv.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                searchAdapter.filter.filter(newText)
                return false
            }
        })
    }
    fun initAdapter(){

        mainViewModel.postAllList.observe(viewLifecycleOwner, {
            searchAdapter = SearchAdapter(it)
            searchAdapter.userList = mainViewModel.allUserList.value!!
            binding.searchFragmentRv.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = searchAdapter
                searchAdapter.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
            }
            searchAdapter.setOnItemClickListener(object: SearchAdapter.ItemClickListener {
                override fun onClick(view: View, position: Int, typeId: Int, boardId: Int, userId:Int) {
                    Log.d("TAG", "onClick: $boardId")
                    mainViewModel.boardId = boardId
                    mainViewModel.userId = userId
                    if(typeId == 1){
                        //울동네
                        this@SearchFragment.findNavController().navigate(R.id.action_searchFragment_to_localBoardFragment)
                    }else if( typeId == 2){
                        //궁금해
                        this@SearchFragment.findNavController().navigate(R.id.action_searchFragment_to_qnaBoardFragment)
                    }else if(typeId == 3){
                        //공유해
                        this@SearchFragment.findNavController().navigate(R.id.action_searchFragment_to_shareBoardFragment)
                    }
                }

            })
        })

    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SearchFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}