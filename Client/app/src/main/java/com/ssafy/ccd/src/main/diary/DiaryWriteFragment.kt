package com.ssafy.ccd.src.main.diary

import android.app.DatePickerDialog
import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextUtils
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import android.widget.ArrayAdapter
import android.widget.MultiAutoCompleteTextView
import androidx.annotation.RequiresApi
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.reflect.TypeToken
import com.google.firebase.ktx.Firebase
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
import kotlinx.coroutines.runBlocking
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
    private var mauth = FirebaseAuth.getInstance()
    var curDate = Date()
    val dataFormat: SimpleDateFormat = SimpleDateFormat("yyyy년 MM월 dd일")
    private lateinit var contentResolver : ContentResolver
    var timeName = ""
    private var hashs = arrayListOf<Hashtag>()
    var flag = 1;
    var diaryId = -1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.apply {
            diaryId = getInt("diaryId")
            if(diaryId > 0){
                flag= 2;
            }
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
        runBlocking {
            mainViewModel.getHashTags()
        }

        FirebaseAuth.getInstance().signInAnonymously()
        if(flag==2){
            initData()
        }
        initHashs()
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
            if(flag == 2){
                //update
                convertFileName()
                getFilterHashTag()
                Log.d(TAG, "onViewCreated: ${hashs}")
                var diary = Diary(
                    content = content,
                    datetime = CommonUtils.makeBirthMilliSecond(date),
                    hashtag = hashs,
                    id = diaryId,
                    photo = photos,
                    title = title,
                    userId = ApplicationClass.sharedPreferencesUtil.getUser().id
                )
                updateDiary(diary)
            }

        }
    }
    fun initData(){
        runBlocking {
            mainViewModel.getDiaryDetail(diaryId)
        }
        var diary = mainViewModel.diary.value!!
        binding.fragmentDiaryWriteTitle.setText(diary.title)
        binding.fragmentDiaryWriteDate.setText(CommonUtils.makeBirthString(diary.datetime))
        binding.fragmentDiaryWriteContent.setText(diary.content)
        var hashs = ""
        for(hash in 0..diary.hashtag.size-1){
            hashs += diary.hashtag[hash].hashtag+" "
        }
        var photoUpdateAdapter = DiaryPhotoUpdateAdapter()
        photoUpdateAdapter.list = diary.photo as MutableList<Photo>
        mainViewModel.allClearPhotoList()

        for(i in 0..diary.photo.size-1){
            mainViewModel.insertPhotoList(diary.photo[i])
        }

        binding.fragmentDiaryWriteCameraRv.apply {
            layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)
            adapter = photoUpdateAdapter
            adapter!!.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
        }
        binding.fragmentDiaryWriteHashTag.setText(hashs)
        binding.fragmentDiaryWriteSuccessBtn.setText("수정")
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
    fun updateDiary(diary:Diary){
        GlobalScope.launch {
            var response = DiaryService().updateDiaryService(diary)
            val res = response.body()
            if(response.code() == 200){
                Log.d(TAG, "updateDiary1: ${res}")
                if(res!=null){
                    if(res.success){
                        Log.d(TAG, "updateDiary2: ${res}")
                        addFireBase()
                        mainActivity.runOnUiThread(Runnable {
                            this@DiaryWriteFragment.findNavController().navigate(R.id.action_diaryWriteFragment_to_diaryFragment)
                        })
                    }else{
                        Log.d(TAG, "updateDiary3: ")
                    }
                }else{
                    Log.d(TAG, "updateDiary4: ")
                }
            }else{
                Log.d(TAG, "updateDiary: ${response.code()} ${res?.message}")
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
    private fun initHashs(){
        var hashs = mutableListOf<String>()
        for(item in 0..mainViewModel.hashList.value!!.size-1){
            var name = mainViewModel.hashList.value!![item].hashtag
            hashs.add(name)
        }
        Log.d(TAG, "initHashs: ${hashs}")
        var adapter = ArrayAdapter<String>(requireContext(),android.R.layout.simple_dropdown_item_1line,hashs)

        binding.fragmentDiaryWriteHashTag.setTokenizer(MultiAutoCompleteTextView.CommaTokenizer())
        binding.fragmentDiaryWriteHashTag.setAdapter(adapter)

    }
    inner class SpaceTokenizer : MultiAutoCompleteTextView.Tokenizer {
        override fun findTokenStart(text: CharSequence?, cursor: Int): Int {
            var i = cursor

            while (i > 0 && text!![i - 1] != ' ') {
                i--
            }
            while (i < cursor && text!![i] == ' ') {
                i++
            }

            return i
        }

        override fun findTokenEnd(text: CharSequence?, cursor: Int): Int {
            var i = cursor
            val len = text!!.length

            while (i < len) {
                if (text[i] == ' ') {
                    return i
                } else {
                    i++
                }
            }

            return len
        }

        override fun terminateToken(text: CharSequence?): CharSequence? {
            var i = text!!.length

            while (i > 0 && text[i - 1] == ' ') {
                i--
            }

            return if (i > 0 && text[i - 1] == ' ') {
                text
            } else {
                if (text is Spanned) {
                    val sp = SpannableString("$text ")
                    TextUtils.copySpansFrom(
                        text, 0, text.length,
                        Any::class.java, sp, 0
                    )
                    sp
                } else {
                    "$text "
                }
            }
        }

    }

    override fun onStart() {
        super.onStart()
        var user = mauth.currentUser
        if(user!=null){

        }else{
            signInAnonymously()
        }
    }
    private fun signInAnonymously(){
        mauth.signInAnonymously().addOnSuccessListener(requireActivity(), OnSuccessListener<AuthResult>() {
            
        }).addOnFailureListener(mainActivity) {
            Log.e(TAG, "signInAnonymously: ",)
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