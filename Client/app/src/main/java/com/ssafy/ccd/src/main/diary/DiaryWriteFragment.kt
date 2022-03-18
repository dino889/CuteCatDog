package com.ssafy.ccd.src.main.diary

import android.app.DatePickerDialog
import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.storage.FirebaseStorage
import com.ssafy.ccd.R
import com.ssafy.ccd.config.ApplicationClass
import com.ssafy.ccd.config.BaseFragment
import com.ssafy.ccd.databinding.FragmentDiaryWriteBinding
import com.ssafy.ccd.src.dto.Photo
import com.ssafy.ccd.src.main.MainActivity
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

private const val TAG = "DiaryWriteFragment"
class DiaryWriteFragment : BaseFragment<FragmentDiaryWriteBinding>(FragmentDiaryWriteBinding::bind,R.layout.fragment_diary_write) {
    private lateinit var mainActivity : MainActivity
    private lateinit var photoAdapter:DiaryPhotoAdapter
    private val GALLERY_CODE = 20
    private val fileNames = arrayListOf<String>()
    var curDate = Date()
    val dataFormat: SimpleDateFormat = SimpleDateFormat("yyyy년 MM월 dd일")
    private lateinit var contentResolver : ContentResolver
    var timeName = ""
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
        binding.fragmentDiaryWriteDatePicker.setOnClickListener {
            setBirth()
        }
        binding.fragmentDiaryWriteAddCameraBtn.setOnClickListener{
            mainActivity.getAlbum(GALLERY_CODE)
            loadImage()
        }
        binding.fragmentDiaryWriteSuccessBtn.setOnClickListener {
            insertDiary()
        }
    }
    fun setBirth(){
        val calendar:Calendar = Calendar.getInstance()
        try{
            curDate = dataFormat.parse(binding.fragmentDiaryWriteDate.text.toString())
        }catch (e:Exception){
            e.printStackTrace()
        }

        calendar.time = curDate

        val curYear = calendar.get(Calendar.YEAR)
        val curMonth = calendar.get(Calendar.MONTH)
        val curDay = calendar.get(Calendar.DAY_OF_MONTH)

        val dialog = DatePickerDialog(requireContext(), dateSetListener, curYear, curMonth, curDay)
        dialog.show()
    }
    private val dateSetListener =
        DatePickerDialog.OnDateSetListener {datePicker, i, i2, i3 ->
            val selectedCalendar = Calendar.getInstance()
            selectedCalendar[Calendar.YEAR] = i
            selectedCalendar[Calendar.MONTH] = i2
            selectedCalendar[Calendar.DAY_OF_MONTH] = i3

            val curDate = selectedCalendar.time
            setSelectedDate(curDate)
        }
    private fun setSelectedDate(curDate: Date){
        val selectedDateStr = dataFormat.format(curDate)
        binding.fragmentDiaryWriteDate.setText(selectedDateStr)
    }
    private fun loadImage(){
        Log.d(TAG, "loadImage: this?")
        mainViewModel.photoUriList.observe(viewLifecycleOwner, {
            Log.d(TAG, "loadImage: ${it}")
            photoAdapter = DiaryPhotoAdapter()
            photoAdapter.photoList = it

            binding.fragmentDiaryWriteCameraRv.apply {
                layoutManager = LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false)
                adapter = photoAdapter
                adapter!!.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
            }
        })

        convertFileName()
    }
    fun addFireBase(){
        if(mainViewModel.photoUriList.value?.size == null){
            Log.e("ERROR", "이미지 Uri에서 문제가 발생하였습니다.")
            showCustomToast("이미지 Uri에서 문제가 발생하였습니다.")
            childFragmentManager.popBackStack()
        }
        for(i in 0..fileNames.size-1){
            val storageReferenceChild = FirebaseStorage.getInstance().getReference("${ApplicationClass.sharedPreferencesUtil.getUser().id}").child(fileNames[i])
            for(j in 0..mainViewModel.photoUriList.value!!.size-1){
                storageReferenceChild.putFile(mainViewModel.photoUriList.value!![j])
                    .addOnSuccessListener { 
                        storageReferenceChild.downloadUrl
                            .addOnSuccessListener {
                                Log.d(TAG, "addFireBase: ${it}")
                            }
                    }
            }
            

        }
    }
    private fun insertDiary(){
        var title = binding.fragmentDiaryWriteTitle.text.toString()
        var date = binding.fragmentDiaryWriteDate.text.toString()
        var content = binding.fragmentDiaryWriteContent.text.toString()
        var photos = mainViewModel.photoList.value!!

    }
    private fun convertFileName(){
        mainViewModel.photoUriList.observe(viewLifecycleOwner, {
            for(item in 0..it.size-1){
                timeName = System.currentTimeMillis().toString();
                var fileName = "${ApplicationClass.sharedPreferencesUtil.getUser().id}/${timeName}${item}."+GetFileExtension(it[item])
                var name = "${timeName}${item}."+GetFileExtension(it[item])
                fileNames.add(name)
                mainViewModel.photoList.value?.add(Photo(0,fileName))
            }
        })
    }
    private fun GetFileExtension(uri: Uri?):String? {
        val mimeTypeMap = MimeTypeMap.getSingleton()
        contentResolver = mainActivity.contentResolver
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri!!))

    }
    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            DiaryWriteFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}