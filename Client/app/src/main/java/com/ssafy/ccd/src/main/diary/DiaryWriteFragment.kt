package com.ssafy.ccd.src.main.diary

import android.annotation.SuppressLint
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
import android.view.WindowManager
import android.webkit.MimeTypeMap
import android.widget.ArrayAdapter
import android.widget.MultiAutoCompleteTextView
import android.widget.MultiAutoCompleteTextView.CommaTokenizer
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
import com.ssafy.ccd.src.dto.Message
import com.ssafy.ccd.src.dto.Photo
import com.ssafy.ccd.src.main.MainActivity
import com.ssafy.ccd.src.network.service.DiaryService
import com.ssafy.ccd.src.network.service.PetService
import com.ssafy.ccd.util.CommonUtils
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import retrofit2.Response
import java.lang.Exception
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList
import androidx.core.app.ActivityCompat.startActivityForResult

import android.provider.MediaStore

import android.content.Intent
import android.widget.Toast

import android.content.ClipData
import android.graphics.Color
import android.graphics.ImageDecoder
import android.provider.CalendarContract
import android.widget.EditText
import com.google.android.youtube.player.internal.i
import com.jakewharton.rxbinding3.widget.textChanges
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.subscribeBy
import java.io.IOException
import java.util.concurrent.TimeUnit


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
    var flag = 1;   // 1 = insert / 2 = update / 3 = 분석 결과로 일기 업로드
    var diaryId = -1
    private lateinit var editTextSubscription: Disposable
    private lateinit var beforeHashtag: List<Hashtag>

    private var dupChk = true

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
        contentResolver = mainActivity.contentResolver
    }

    override fun onResume() {
        super.onResume()
        mainActivity.hideBottomNavi(true)

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainActivity.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)

        arguments?.apply {
            diaryId = getInt("diaryId")
            if(diaryId > 0){
                flag= 2;
            }
            var check = getInt("flag")
            if(check == 3){
                flag = 3;
            }
            Log.d(TAG, "onCreate: $check")
        }

    }

    @SuppressLint("ResourceAsColor")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dupChk = true

        mainViewModel.allClearPhotoList()
        mainViewModel.allClearPhotoUriList()
        binding.viewModel = mainViewModel
        runBlocking {
            mainViewModel.getHashTags()
        }
        mainActivity.hideBottomNavi(true)
        FirebaseAuth.getInstance().signInAnonymously()
        if(flag==2 || flag == 3){
            initData()
        }
        initHashs()
        binding.fragmentDiaryWriteDatePicker.setOnClickListener {
            setBirth()
        }
        inputObservable()
        if(flag!=2){
            mainViewModel.photoUriList.observe(viewLifecycleOwner, {
                if(it.size == 10){
                    binding.fragmentDiaryWriteAddCameraBtn.setBackgroundColor(Color.parseColor("#FFAD88"))
                }
                binding.photosize.setText(CommonUtils.converPhotoSize(it.size))

            })
        }


        binding.fragmentDiaryWriteAddCameraBtn.setOnClickListener{
            if(mainViewModel.photoUriList.value!!.size < 10){
                val intent = Intent(Intent.ACTION_PICK)
                intent.type = MediaStore.Images.Media.CONTENT_TYPE
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true) // 다중 이미지를 가져올 수 있도록 세팅
                intent.data = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                startActivityForResult(intent, 8888)
            }else{
                showCustomToast("사진을 추가하실 수 없습니다.")
            }
        }



        binding.fragmentDiaryWriteSuccessBtn.setOnClickListener {
            if(dupChk) {
                dupChk = false
                var title = binding.fragmentDiaryWriteTitle.text.toString()
                var date = binding.fragmentDiaryWriteDate.text.toString()
                var content = binding.fragmentDiaryWriteContent.text.toString()
                var photos = mainViewModel.photoList.value!!

                if(flag == 1 || flag == 3){
                    //insert
                    if(inputValueChk()) {
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
                    } else {
                        showCustomToast("입력 값을 확인해 주세요.")
                        dupChk = true
                    }
                }
                if(flag == 2){
                    //update
                    if(inputValueChk()) {
                        convertFileName()
                        hashTagUpdate()
    //                    getFilterHashTag()
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
                    } else {
                        showCustomToast("입력 값을 확인해 주세요.")
                        dupChk = true
                    }
                }
            }
        }

        binding.fragmentDiaryWriteBack.setOnClickListener {
            this@DiaryWriteFragment.findNavController().popBackStack()
            dupChk = true
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun initData(){
        if(flag == 2){
            runBlocking {
                mainViewModel.getDiaryDetail(diaryId)
            }
            var diary = mainViewModel.diary.value!!
            beforeHashtag = diary.hashtag
            binding.fragmentDiaryWriteTitle.setText(diary.title)
            binding.fragmentDiaryWriteDate.setText(CommonUtils.makeBirthString(diary.datetime))
            binding.fragmentDiaryWriteContent.setText(diary.content)
            binding.photosize.text = CommonUtils.converPhotoSize(diary.photo.size)
            
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
        }else if(flag == 3){
            var curTime = LocalDateTime.now()
            val formmater = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일")
            var cur = curTime.format(formmater)

            binding.fragmentDiaryWriteDate.setText(cur)
            mainViewModel.uploadedImageUri?.let { mainViewModel.insertPhotoUriList(it) }
            loadImage()
            binding.fragmentDiaryWriteHashTag.setText("#${mainViewModel.emotions}")
        }

    }

    private fun setBirth(){
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
        Log.d(TAG, "addFireBase: ${mainViewModel.photoUriList.value?.size} / ${fileNames.size}")
        if(mainViewModel.photoUriList.value?.size == null){
            Log.e(TAG, "이미지 Uri에서 문제가 발생하였습니다.")
            showCustomToast("이미지 Uri에서 문제가 발생하였습니다.")
            childFragmentManager.popBackStack()
        }

        for(i in 0 until mainViewModel.photoUriList.value!!.size){
            val storageReferenceChild = FirebaseStorage.getInstance().getReference("${ApplicationClass.sharedPreferencesUtil.getUser().id}").child(fileNames[i])
            storageReferenceChild.putFile(mainViewModel.photoUriList.value!![i])
                .addOnSuccessListener {
                    storageReferenceChild.downloadUrl
                        .addOnSuccessListener {
                            if(i == mainViewModel.photoUriList.value!!.size - 1) {
                                this@DiaryWriteFragment.findNavController().popBackStack()
                                dupChk = true
//                                (requireActivity() as MainActivity).onBackPressed()
                            }
                            Log.d(TAG, "addFireBase: $it")
                        }
                }
        }
    }

    fun insertDiary(diary:Diary) {
        var response : Response<Message>
        runBlocking {
            response = DiaryService().insertDiaryService(diary)
        }
        val res = response.body()
        if (response.code() == 200) {
            if (res != null) {
                Log.d(TAG, "insertDiary: ${res}")
//                    addFireBase()
                if (res.success) {
                    if (!mainViewModel.photoUriList.value!!.isEmpty()) {
                        addFireBase()
                    }
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

    fun updateDiary(diary:Diary){
        var response : Response<Message>

        runBlocking {
            response = DiaryService().updateDiaryService(diary)
        }
        val res = response.body()
        if(response.code() == 200){
            if(res!=null){
                if(res.success){
                    Log.d(TAG, "updateDiary2: ${res}")
                    addFireBase()
                    if(mainViewModel.photoUriList.value?.size == 0) {
                        (requireActivity() as MainActivity).onBackPressed()
                        dupChk = true
                    }
//                    mainActivity.runOnUiThread(Runnable {
//                        this@DiaryWriteFragment.findNavController().navigate(R.id.action_diaryWriteFragment_to_diaryFragment)
//                    })
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
            if(text[i].contains("#")) {
                hashs.add(Hashtag(text[i].trim(), i))
            }
        }
        Log.d(TAG, "getFilterHashTag: $hashs")
    }

    private fun hashTagUpdate() {
        val tag = mutableSetOf<Hashtag>()

        val fullText = binding.fragmentDiaryWriteHashTag.text.toString()
        val text = fullText.split(" ")

        if(beforeHashtag.size == 0) {
            for (i in 0..text.size - 1) {
                if(text[i].contains("#")) {
                    hashs.add(Hashtag(text[i].trim(), i))
                }
            }
        } else {
            for(hashtag in beforeHashtag) {
                for(txt in text) {
                    if(hashtag.hashtag != txt && txt.contains("#")) {
                        tag.add(Hashtag(txt, 0))
                    }
                }
            }
            hashs = tag.toList() as ArrayList<Hashtag>
            Log.d(TAG, "hashTagUpdate: $tag /////// $hashs")
        }
//
//        for(i in 0..text.size - 1){
//            if(beforeHashtag.size != 0) {
//                for(hashtag in beforeHashtag) {
//                    Log.d(TAG, "hashTagUpdate2: ${text[i]} / ${hashtag.hashtag}")
//                    if(text[i].contains("#")) {
//                        if(text[i] != hashtag.hashtag) {
//                            Log.d(TAG, "해시태그: ${text[i]}")
//
//                            hashs.add(Hashtag(text[i].trim(), i))
//                        }
//                    }
//                }
//            } else {
//
//            }
//        }
        Log.d(TAG, "hashTagUpdate3333: $hashs")
    }

    private fun initHashs(){
        var hashs = mutableListOf<String>()
        for(item in 0..mainViewModel.hashList.value!!.size-1){
            var name = mainViewModel.hashList.value!![item].hashtag
            hashs.add(name)
        }
        var adapter = ArrayAdapter<String>(requireContext(),android.R.layout.simple_dropdown_item_1line,hashs)

        binding.fragmentDiaryWriteHashTag.setTokenizer(SpaceTokenizer())
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
            Log.e(TAG, "signInAnonymously: ")
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 8888) {
            if (data == null) {   // 어떤 이미지도 선택하지 않은 경우
                showCustomToast("이미지를 선택하지 않았습니다.")
            } else {   // 이미지를 하나라도 선택한 경우
                if (data.clipData == null) {     // 이미지를 하나만 선택한 경우
                    Log.e("single choice: ", data.data.toString())
                    val imageUri = data.data

                    mainViewModel.uploadImages = imageUri
                    imageUri.let { mainViewModel.insertPhotoUriList(it!!) }
                    if (mainViewModel.uploadImages == null) showCustomToast("이미지가 정상적으로 로드 되지 않았습니다.")
                    else {
                        try {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                                Log.d(TAG, "onActivityResult: here?")
                                mainViewModel.uploadedImage = ImageDecoder.decodeBitmap(ImageDecoder.createSource(contentResolver, mainViewModel.uploadImages!!))
                            } else {
                                Log.d(TAG, "onActivityResult: here2?")
                                mainViewModel.uploadedImage = MediaStore.Images.Media.getBitmap(contentResolver, mainViewModel.uploadImages)
                            }
                        } catch ( e: IOException) {
                            e.printStackTrace();
                        }
                    }

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
                } else {      // 이미지를 여러장 선택한 경우
                    val clipData = data.clipData
                    Log.e("clipData", clipData!!.itemCount.toString())
                    if (clipData.itemCount > 10 ) {   // 선택한 이미지가 11장 이상인 경우
                        showCustomToast("사진은 10장까지 선택 가능합니다.")
                    } else {   // 선택한 이미지가 1장 이상 10장 이하인 경우
                        Log.e(TAG, "multiple choice")
                        for (i in 0 until clipData.itemCount) {
                            val imageUri = clipData.getItemAt(i).uri // 선택한 이미지들의 uri를 가져온다.
                            try {
//                                uriList.add(imageUri) //uri를 list에 담는다.
                                mainViewModel.uploadImages = imageUri
                                imageUri.let {
                                    if(mainViewModel.photoUriList.value!!.size < 10){
                                        mainViewModel.insertPhotoUriList(it!!)
                                    } else{
                                        showCustomToast("더이상 선택할 수 없습니다.")
                                    }
                                }
                            } catch (e: Exception) {
                                Log.e(TAG, "File select error", e)
                            }
                        }
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
                }
            }
        }
    }

    private fun inputValueChk() : Boolean {
        val title = binding.fragmentDiaryWriteTitle.text
        val content = binding.fragmentDiaryWriteContent.text.toString()
        val date = binding.fragmentDiaryWriteDate.text.toString()
        Log.d(TAG, "inputValueChk: $title $content $date")
        return title.isNotEmpty() && content.isNotEmpty() && date.contains("년")

    }

    private fun contentLenChk(input:String):Boolean{
        binding.fragmentDiaryWriteContentLength.text = "(${input.length} / 500)"
        if(input.trim().isEmpty()){
            binding.textInputLayout3.error = "Required Field"
            binding.fragmentDiaryWriteContent.requestFocus()
            return false
        }else if(input.length < 30 || input.length > 500){
            binding.textInputLayout3.error = "작성된 내용의 길이를 확인해주세요"
            binding.fragmentDiaryWriteContent.requestFocus()
            return false
        }else{
            binding.textInputLayout3.error = null
            return true
        }
    }

    private fun inputObservable(){
        binding.fragmentDiaryWriteContent.setQueryDebounce{
            contentLenChk(it)
        }
    }

    private fun EditText.setQueryDebounce(queryFunction: (String) -> Unit): Disposable {
        val editTextChangeObservable = this.textChanges()
        editTextSubscription = editTextChangeObservable
            // 마지막 글자 입력 0.5초 후에 onNext 이벤트로 데이터 발행
            .debounce(500, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            // 구독을 통해 이벤트 응답 처리
            .subscribeBy(
                onNext = {
                    Log.d(TAG, "onNext : $it")
                    queryFunction(it.toString())
                },
                onComplete = {
                    Log.d(TAG, "onComplete")
                },
                onError = {
                    Log.i(TAG, "onError : $it")
                }
            )
        return editTextSubscription  // Disposable 반환
    }

    override fun onDestroy() {
        super.onDestroy()
        mainActivity.hideBottomNavi(false)
    }
}