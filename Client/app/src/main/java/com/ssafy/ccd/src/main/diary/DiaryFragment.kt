package com.ssafy.ccd.src.main.diary

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.ssafy.ccd.R
import com.ssafy.ccd.config.ApplicationClass
import com.ssafy.ccd.config.BaseFragment
import com.ssafy.ccd.databinding.FragmentDiaryBinding
import com.ssafy.ccd.src.main.MainActivity
import kotlinx.coroutines.runBlocking

private const val TAG = "DiaryFragment"
class DiaryFragment : BaseFragment<FragmentDiaryBinding>(FragmentDiaryBinding::bind, R.layout.fragment_diary) {
    private lateinit var diaryAdapter:DiaryAdapter
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
            mainViewModel.getDiaryList(ApplicationClass.sharedPreferencesUtil.getUser().id)
        }
        initAdapter()
        binding.fragmentDiarySearch.setOnClickListener {
            showSortDialog()
        }

        binding.fragmentDiaryWrite.setOnClickListener {
            this@DiaryFragment.findNavController().navigate(R.id.action_diaryFragment_to_diaryWriteFragment)
        }
    }
    fun initAdapter(){
        mainViewModel.diaryList.observe(viewLifecycleOwner, {
            Log.d(TAG, "initAdapter: ${it}")
            diaryAdapter = DiaryAdapter(requireContext())
            diaryAdapter.list = it
            binding.fragmentDiaryRv.apply {
                layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL,false)
                adapter = diaryAdapter
                adapter!!.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
            }
        })
    }

    fun showSortDialog(){
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.fragment_diary_sortbottom_sheet,null)
        val dialog = BottomSheetDialog(requireContext())
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val param = WindowManager.LayoutParams()
        param.width = WindowManager.LayoutParams.MATCH_PARENT
        param.height = WindowManager.LayoutParams.WRAP_CONTENT
        val window = dialog.window
        dialog.setContentView(dialogView)
        dialog.show()
    }
    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            DiaryFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}