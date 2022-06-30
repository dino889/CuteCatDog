package com.ssafy.ccd.src.main.diary

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.PopupMenu
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.ccd.R
import com.ssafy.ccd.config.BaseFragment
import com.ssafy.ccd.databinding.FragmentDiaryDetailBinding
import com.ssafy.ccd.src.dto.Hashtag
import com.ssafy.ccd.src.dto.Photo
import com.ssafy.ccd.src.main.MainActivity
import com.ssafy.ccd.src.network.service.DiaryService
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

private const val TAG = "DiaryDetailFragment"
class DiaryDetailFragment : BaseFragment<FragmentDiaryDetailBinding>(FragmentDiaryDetailBinding::bind,R.layout.fragment_diary_detail) {
    private lateinit var mainActivity : MainActivity
    private var diaryId = -1
    private lateinit var hashAdapter: DiaryHashAdapter
    private lateinit var photoAdapter :DiaryPhotoRvAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.apply {
            diaryId = getInt("diaryId")
        }
    }
    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = mainViewModel
//        setFragmentResultListener("diaryId") {key,bundle ->
//            diaryId = bundle.getInt("diaryId")
//        }
        Log.d(TAG, "onCreate: ${diaryId}")
        runBlocking {
            mainViewModel.getDiaryDetail(diaryId)
        }

        initAdatper()
        binding.fragmentDiaryDetailMore.setOnClickListener {
            var popupMenu = PopupMenu(requireContext(), binding.fragmentDiaryDetailMore)
            MenuInflater(requireContext()).inflate(R.menu.popup_menu, popupMenu.menu)
            var listener = PopupMenuListener()
            popupMenu.setOnMenuItemClickListener(listener)
            popupMenu.show()
        }

        backBtnClickEvent()
    }
    inner class PopupMenuListener: PopupMenu.OnMenuItemClickListener {
        override fun onMenuItemClick(item: MenuItem?): Boolean {
            when(item?.itemId){
                R.id.modify->{
                    this@DiaryDetailFragment.findNavController().navigate(R.id.action_diaryDetailFragment_to_diaryWriteFragment,
                        bundleOf("diaryId" to diaryId))
                }
                R.id.delete -> {
                    DeleteDiary(diaryId)
                }
            }
            return false
        }
    }
    fun initAdatper(){
        mainViewModel.diary.observe(viewLifecycleOwner, {
            hashAdapter = DiaryHashAdapter()
            photoAdapter = DiaryPhotoRvAdapter()
            hashAdapter.list = it.hashtag as MutableList<Hashtag>
            photoAdapter.list = it.photo as MutableList<Photo>

            binding.fragmentDiaryDetailHashRv.apply {
                layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)
                adapter = hashAdapter
                adapter!!.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
            }

            binding.fragmentDiaryDetailImgRv.apply {
                layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL,false)
                adapter = photoAdapter
                adapter!!.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
            }
        })
    }
    fun DeleteDiary(diaryId:Int){
        GlobalScope.launch { 
            var response = DiaryService().deleteDiaryService(diaryId)
            val res = response.body()
            if(response.code() == 200){
                if(res!=null){
                    if(res.success){
                        //diayFragment 이동
                        mainActivity.runOnUiThread(Runnable {
                            this@DiaryDetailFragment.findNavController().navigate(R.id.action_diaryDetailFragment_to_diaryFragment)
                        })
                    }
                }else{
                    Log.d(TAG, "DeleteDiary: ")
                }
            }else{
                Log.d(TAG, "DeleteDiary: ")
            }
        }
    }

    private fun backBtnClickEvent() {
        binding.fragmentDiaryDetailBack.setOnClickListener {
            this@DiaryDetailFragment.findNavController().popBackStack()
        }
    }
}