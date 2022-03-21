package com.ssafy.ccd.src.main.diary

import android.app.DatePickerDialog
import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import androidx.annotation.RequiresApi
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.storage.FirebaseStorage
import com.ssafy.ccd.R
import com.ssafy.ccd.config.ApplicationClass
import com.ssafy.ccd.config.BaseFragment
import com.ssafy.ccd.databinding.FragmentDiaryWriteBinding
import com.ssafy.ccd.src.dto.Diary
import com.ssafy.ccd.src.dto.Hashtag
import com.ssafy.ccd.src.dto.Photo
import com.ssafy.ccd.src.main.MainActivity
import com.ssafy.ccd.src.network.service.DiaryService
import com.ssafy.ccd.src.network.service.PetService
import com.ssafy.ccd.util.CommonUtils
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

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
    private var hashs = arrayListOf<Hashtag>()
    var flag = 1;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }
    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainViewModel.allClearPhotoList()
        mainViewModel.allClearPhotoUriList()
        binding.fragmentDiaryWriteDatePicker.setOnClickListener {
            setBirth()
        }
        binding.fragmentDiaryWriteAddCameraBtn.setOnClickListener{
            mainActivity.getAlbum(GALLERY_CODE)
            loadImage()
        }
        binding.fragmentDiaryWriteSuccessBtn.setOnClickListener {
            var title = binding.fragmentDiaryWriteTitle.text.toString()
            var date = binding.fragmentDiaryWriteDate.text.toString()
            var content = binding.fragmentDiaryWriteContent.text.toString()
            var photos = mainViewModel.photoList.value!!

            if(flag == 1){
                //insert
                convertFileName()
                getFilterHashTag()
                Log.d(TAG, "onViewCreated: ${hashs}")
                var diary = Diary(
                    content = content,
                    datetime = CommonUtils.makeBirthMilliSecond(date),
                    hashtag = hashs,
                    id = 0,
                    photo = photos,
                    title = title,
                    userId = ApplicationClass.sharedPreferencesUtil.getUser().id
                )
                insertDiary(diary)
            }


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
    }
    fun addFireBase(){
        if(mainViewModel.photoUriList.value?.size == null){
            Log.e("ERROR", "이미지 Uri에서 문제가 발생하였습니다.")
            showCustomToast("이미지 Uri에서 문제가 발생하였습니다.")
            childFragmentManager.popBackStack()
        }

        for(i in 0 until fileNames.size){
            val storageReferenceChild = FirebaseStorage.getInstance().getReference("${ApplicationClass.sharedPreferencesUtil.getUser().id}").child(fileNames[i])
//            for(j in 0 until mainViewModel.photoUriList.value!!.size){
//                storageReferenceChild.putFile(mainViewModel.photoUriList.value!![j])
//                    .addOnSuccessListener {
//                        storageReferenceChild.downloadUrl
//                            .addOnSuccessListener {
//                                Log.d(TAG, "addFireBase: ${it}")
//                            }
//                    }
//                }
            storageReferenceChild.putFile(mainViewModel.photoUriList.value!![i])
                .addOnSuccessListener {
                    storageReferenceChild.downloadUrl
                        .addOnSuccessListener {
                            Log.d(TAG, "addFireBase: $it")
                        }
                }

        }
    }
    fun insertDiary(diary:Diary) {
        GlobalScope.launch {
            var response = DiaryService().insertDiaryService(diary)
            val res = response.body()
            if (response.code() == 200) {
                if (res != null) {
                    Log.d(TAG, "insertDiary: ${res}")
//                    addFireBase()
                    if (res.success) {
                        if (!mainViewModel.photoUriList.value!!.isEmpty()) {

                            addFireBase()
                        }
                        mainActivity.runOnUiThread(Runnable {
                            this@DiaryWriteFragment.findNavController()
                                .navigate(R.id.action_diaryWriteFragment_to_diaryFragment)
                        })
                    }
                } else {
                    if (res != null) {
                        Log.d(TAG, "insertDiary: ${res.message}")
                    }
                }
            } else {
                Log.d(TAG, "insertDiary: ${response.code()}")
            }
        }
    }

    private fun convertFileName(){
        for(item in 0 until mainViewModel.photoUriList.value!!.size) {
            timeName = System.currentTimeMillis().toString();
            var fileName = "${ApplicationClass.sharedPreferencesUtil.getUser().id}/${timeName}." + GetFileExtension(
                    mainViewModel.photoUriList.value!![item]
                )
            var name = "${timeName}." + GetFileExtension(mainViewModel.photoUriList.value!![item])
            fileNames.add(name)
            mainViewModel.insertPhotoList(Photo(0, fileName))
//            mainViewModel.photoList.value?.add(Photo(0, fileName))
        }

//        mainViewModel.photoUriList.observe(viewLifecycleOwner, {
//            timeName = System.currentTimeMillis().toString()
//            val cnt = mainViewModel.photoUriList.value!!.size
//            var fileName = "${ApplicationClass.sharedPreferencesUtil.getUser().id}/${timeName}."+GetFileExtension(
//                mainViewModel.photoUriList.value!![cnt])
//            var name = "${timeName}."+GetFileExtension(it[item])
//            fileNames.add(name)
//            mainViewModel.insertPhotoList(Photo(0,fileName))
//
////            fileNames.clear()
////            for(item in 0..it.size-1){
////                timeName = System.currentTimeMillis().toString();
////                var fileName = "${ApplicationClass.sharedPreferencesUtil.getUser().id}/${timeName}${item}."+GetFileExtension(it[item])
////                var name = "${timeName}${item}."+GetFileExtension(it[item])
////                fileNames.add(name)
////                mainViewModel.insertPhotoList(Photo(0,fileName))
//////                mainViewModel.photoList.value?.add(Photo(0,fileName))
////            }
//        })
    }
    private fun GetFileExtension(uri: Uri?):String? {
        val mimeTypeMap = MimeTypeMap.getSingleton()
        contentResolver = mainActivity.contentResolver
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri!!))

    }

    private fun getFilterHashTag(){
        var fullText = binding.fragmentDiaryWriteHashTag.text.toString()
        var text = fullText.split(" ")
        for(i in 0..text.size-1){
            hashs.add(Hashtag(text[i],i))
        }
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