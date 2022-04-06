package com.ssafy.ccd.src.main.mypage

import android.Manifest
import android.os.Bundle
import android.view.View
import com.ssafy.ccd.R
import com.ssafy.ccd.config.BaseFragment
import com.ssafy.ccd.databinding.FragmentAddPetBinding
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import android.view.WindowManager
import android.webkit.MimeTypeMap
import android.widget.ArrayAdapter
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.ssafy.ccd.config.ApplicationClass
import com.ssafy.ccd.src.dto.Board
import com.ssafy.ccd.src.dto.Message
import com.ssafy.ccd.src.dto.Pet
import com.ssafy.ccd.src.main.MainActivity
import com.ssafy.ccd.src.network.service.PetService
import com.ssafy.ccd.util.CommonUtils
import kotlinx.coroutines.*
import retrofit2.Response

private const val TAG = "AddPetFragment_ccd"
class AddPetFragment : BaseFragment<FragmentAddPetBinding>(FragmentAddPetBinding::bind, R.layout.fragment_add_pet) {
    var curDate = Date() // 현재
    private lateinit var mainActivity : MainActivity
    private var petId = -1

    private val STORAGE_CODE = 99
    private val STORAGE = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)

    val dataFormat: SimpleDateFormat = SimpleDateFormat("yyyy년 MM월 dd일")

    var kindId = 0
    var gender = -1
    var isNeutered = -1
    var fileName = ""
    var timeName = ""
    var flag = 1


    //firebase
    private var storageReference: StorageReference? = null
    private lateinit var contentResolver : ContentResolver
    private val GALLERY_CODE = 10

    // SimpleDateFormat 으로 포맷 결정
    var result: String = dataFormat.format(curDate)

    // 사진 선택
    private lateinit var imgUri: Uri    // 파일 uri
    private var fileExtension : String? = ""    // 파일 확장자
    private var imgSelectedChk = false

    private lateinit var beforePet: Pet

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.apply {
            petId = getInt("petId")
        }
        Log.d(TAG, "onCreate: $petId")
    }

    override fun onResume() {
        super.onResume()
        mainActivity.hideBottomNavi(true)
        mainActivity.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)

    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainActivity.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)

        mainActivity.hideBottomNavi(true)

        runBlocking {
            mainViewModel.getPetKindsAllList()
        }

        // 이미지 선택 버튼 클릭 이벤트
        selectImgBtnEvent()
        initListener()

        if(petId > 0){
            runBlocking {
                mainViewModel.getPetDetailList(petId)
            }
            flag = 2
            initData()
            modifyBtnClickEvent()
        } else {
//            setBirth()
            confirmBtnClickEvent()
        }

//        binding.addPetFragmentIbSelectImg.setOnClickListener {
//            mainActivity.getAlbum(GALLERY_CODE)
//            loadImage()
//        }

//        // 등록 or 수정 버튼 클릭 이벤트
//        binding.fragmentAddPetSuccessBtn.setOnClickListener {
//            if(flag == 1){
//                if(imgUri == null || imgUri.toString() == ""){
//                    fileName = ""
//                }else{
//                    timeName = System.currentTimeMillis().toString();
//                    fileName = "${ApplicationClass.sharedPreferencesUtil.getUser().id}/${timeName}."+getFileExtension(imgUri)
//                }
//                var pet = Pet(
//                    birth = CommonUtils.makeBirthMilliSecond(binding.addPetFragmentTietBirth.text.toString()),
//                    gender = gender,
//                    id=0,
//                    isNeutered = isNeutered,
//                    kindId = kindId,
//                    name = binding.addPetFragmentTietName.text.toString(),
//                    photoPath = fileName,
//                    userId = ApplicationClass.sharedPreferencesUtil.getUser().id
//                )
//                Log.d(TAG, "onViewCreated: INSERT")
//                insertPet(pet)
//            }else{
//
//                if(imgUri == null || imgUri.toString() == "" || imgUri == Uri.EMPTY){
//                    fileName = ""
//                }else{
//                    timeName = System.currentTimeMillis().toString();
//                    fileName = "${ApplicationClass.sharedPreferencesUtil.getUser().id}/${timeName}."+ getFileExtension(imgUri)
//                }
//
//                Log.d(TAG, "onViewCreated: $fileName")
//
//                val beforeFileName = beforePet.photoPath.substring(beforePet.photoPath.lastIndexOf("/") + 1, beforePet.photoPath.length)
//                if(fileName == "" || fileName == beforeFileName) {
//                    fileName = beforePet.photoPath
//                }
//                Log.d(TAG, "onViewCreated: $fileName")
//
//
//                var pet = Pet(
//                    birth = CommonUtils.makeBirthMilliSecond(binding.addPetFragmentTietBirth.text.toString()),
//                    gender = gender,
//                    id=mainViewModel.petId,
//                    isNeutered = isNeutered,
//                    kindId = kindId,
//                    name = binding.addPetFragmentTietName.text.toString(),
//                    photoPath = fileName,
//                    userId = ApplicationClass.sharedPreferencesUtil.getUser().id
//                )
//                Log.d(TAG, "onViewCreated: UPDATE")
//
//                updatePets(pet)
//            }
//        }

        // back btn click
        binding.fragmentAddPetBack.setOnClickListener {
            this@AddPetFragment.findNavController().popBackStack()
        }

    }


    fun initData(){
        binding.fragmentAddPetTitle.text = "반려동물 수정"
        binding.fragmentAddPetSuccessBtn.setText("수정")

        mainViewModel.pet.observe(viewLifecycleOwner, {
            binding.pet = it
            beforePet = it
            binding.addPetFragmentTietName.setText(it.name.toString())
            binding.addPetFragmentTietBirth.setText(it.birth.toString())
            Log.d(TAG, "initData: ${it.kind}")

            binding.addPetFragmentAutoKind.setText(it.kind.toString()) // 품종 아이디 세팅 하는 부분
//            imgUri = it.photoPath.toUri()

            if(it.isNeutered == 0){
                binding.addPetFragmentRbNeuteringX.isChecked = true
                binding.addPetFragmentRbNeuteringO.isChecked = false
            }else{
                binding.addPetFragmentRbNeuteringX.isChecked = false
                binding.addPetFragmentRbNeuteringO.isChecked = true
            }

            if(it.gender == 0){
                binding.addPetFragmentRbGendeMan.isChecked = true
                binding.addPetFragmentRbGenderWoman.isChecked = false
            }else{
                binding.addPetFragmentRbGendeMan.isChecked = false
                binding.addPetFragmentRbGenderWoman.isChecked = true
            }
            if(it.birth!=null){
                var birth = CommonUtils.makeBirthString(it.birth)
                binding.addPetFragmentTietBirth.setText(birth)
            }



            if(it.photoPath == null || it.photoPath == ""){
                Glide.with(this)
                    .load(R.drawable.logo)
                    .into(binding.addPEtFragmentIvPetImage)
            } else {
                val storage = FirebaseStorage.getInstance("gs://cutecatdog-32527.appspot.com/")
                val storageRef = storage.reference
                storageRef.child(it.photoPath).downloadUrl.addOnSuccessListener { p0 ->
                    Glide.with(this)
                        .load(p0)
                        .into(binding.addPEtFragmentIvPetImage)
                }.addOnFailureListener { }
            }
        })
    }


    /**
     * 완료 버튼 클릭 이벤트 - insert
     */
    @RequiresApi(Build.VERSION_CODES.O)
    private fun confirmBtnClickEvent() {    // 게시글 insert
        binding.fragmentAddPetSuccessBtn.setOnClickListener {
            for (item in mainViewModel.kinds.value!!) {
                val kind = binding.addPetFragmentAutoKind.text.toString()
                if(kind == item.name) {
                    kindId = item.id
                }
            }

            if(kindId < 0) {
                showCustomToast("품종 선택이 잘못되었습니다. \n다시 선택해 주세요.")
            }

            val userId = ApplicationClass.sharedPreferencesUtil.getUser().id
            val fileName = if(imgUri == null || imgUri.toString() == "" || imgUri == Uri.EMPTY) {
                ""
            } else{
                timeName = System.currentTimeMillis().toString()
                "${userId}/${timeName}."+ fileExtension
            }



            if(fileName == "") {
                showCustomToast("사진을 선택해 주세요")
            } else {
                var pet = Pet(
                            birth = CommonUtils.makeBirthMilliSecond(binding.addPetFragmentTietBirth.text.toString()),
                            gender = gender,
                            id=0,
                            isNeutered = isNeutered,
                            kindId = kindId,
                            name = binding.addPetFragmentTietName.text.toString(),
                            photoPath = fileName,
                            userId = ApplicationClass.sharedPreferencesUtil.getUser().id)
                insertPet(pet)

            }
        }
    }


    /**
     * 수정 버튼 클릭 이벤트 - update
     */
    @RequiresApi(Build.VERSION_CODES.O)
    private fun modifyBtnClickEvent() {    // 게시글 insert Or Update
        binding.fragmentAddPetSuccessBtn.setOnClickListener {

            for (item in mainViewModel.kinds.value!!) {
                val kind = binding.addPetFragmentAutoKind.text.toString()
                if(kind == item.name) {
                    kindId = item.id
                }
            }

            if(kindId < 0) {
                showCustomToast("품종 선택이 잘못되었습니다. \n다시 선택해 주세요.")
            }

            val userId = ApplicationClass.sharedPreferencesUtil.getUser().id
            var fileName = if(imgUri == null || imgUri.toString() == "" || imgUri == Uri.EMPTY) {
                ""
            } else{
                timeName = System.currentTimeMillis().toString()
                "${userId}/${timeName}."+ fileExtension
            }

            val beforeFileName = beforePet.photoPath.substring(beforePet.photoPath.lastIndexOf("/") + 1, beforePet.photoPath.length)
            if(fileName == "" || fileName == beforeFileName) {
                fileName = beforePet.photoPath
            }

            var pet = Pet(
                birth = CommonUtils.makeBirthMilliSecond(binding.addPetFragmentTietBirth.text.toString()),
                gender = gender,
                id= petId,
                isNeutered = isNeutered,
                kindId = kindId,
                name = binding.addPetFragmentTietName.text.toString(),
                photoPath = fileName,
                userId = ApplicationClass.sharedPreferencesUtil.getUser().id
            )

            updatePets(pet)

        }
    }


    /**
     * 사진 선택 버튼 클릭 이벤트
     */
    private fun selectImgBtnEvent() {

        if (::imgUri.isInitialized) {
            Log.d(TAG, "selectImgBtnEvent: $imgUri")
        } else {
            imgUri = Uri.EMPTY
            Log.d(TAG, "fileUri 초기화  $imgUri")
        }

        binding.addPetFragmentIbSelectImg.setOnClickListener {
            if (mainActivity.checkPermission(STORAGE, STORAGE_CODE)) {
                val intent = Intent(Intent.ACTION_PICK)
                intent.type = MediaStore.Images.Media.CONTENT_TYPE
                filterActivityLauncher.launch(intent)
            }
        }
    }

//    fun loadImage(){
//        Log.d(TAG, "loadImage: ${mainViewModel.uploadedImageUri}")
//        binding.addPEtFragmentIvPetImage.setImageURI(mainViewModel.uploadedImageUri)
//    }

    fun addFireBase(){
        if(imgUri == null || imgUri == Uri.EMPTY){
            Log.e("ERROR", "이미지 Uri에서 문제가 발생하였습니다.")
            showCustomToast("이미지 Uri에서 문제가 발생하였습니다.")
            childFragmentManager.popBackStack()
        }

        val storageReferenceChild = FirebaseStorage.getInstance().getReference("${ApplicationClass.sharedPreferencesUtil.getUser().id}").child(timeName+"."+fileExtension)

        storageReferenceChild.putFile(imgUri!!)
            .addOnSuccessListener{
                storageReferenceChild.downloadUrl
                    .addOnSuccessListener {
                        (requireActivity() as MainActivity).onBackPressed()
                        Log.d(TAG, "addFireBase: ${it}")
                    }
            }
    }

    fun initListener(){
        initKinds()
        selectedGender()
        selectedNeutered()

        binding.addPetFragmentTietBirth.setText(result)
        binding.addPetFragmentTietBirth.setOnClickListener {
            setBirth()
        }
    }

    /**
     * 품종 자동완성 초기화
     */
    private fun initKinds(){
        var kinds = mutableListOf<String>()
        for(item in 0..mainViewModel.kinds.value!!.size-1){
            var name = mainViewModel.kinds.value!![item].name
            kinds.add(name)
        }
//        Log.d(TAG, "initKinds: ${kinds}")
        var adapter = ArrayAdapter<String>(requireContext(),android.R.layout.simple_dropdown_item_1line,kinds)
        binding.addPetFragmentAutoKind.setAdapter(adapter)

        binding.addPetFragmentAutoKind.setOnItemClickListener { parent, view, position, id ->
            for (item in mainViewModel.kinds.value!!) {
                val kind = binding.addPetFragmentAutoKind.text.toString()
                if(kind == item.name) {
                    kindId = item.id
                }
            }
//            kindId = mainViewModel.kinds.value!![position].id
            Log.d(TAG, "initKinds: id = $id / $position / $kindId")
        }
    }

    /**
     * 성별 선택 클릭 리스너
     */
    private fun selectedGender() {
        binding.addPetFragmentRgGender.setOnCheckedChangeListener { group, checkedId ->
            when(checkedId){
                R.id.addPetFragment_rbGenderWoman -> {
                    gender = 1
                }
                R.id.addPetFragment_rbGendeMan -> {
                    gender = 0
                }
            }
        }
    }

    /**
     * 중성화 여부 선택 클릭 리스너
     */
    private fun selectedNeutered(){
        binding.addPetFragmentRgNeutering.setOnCheckedChangeListener{ group, checkedId ->
            when(checkedId){
                R.id.addPetFragment_rbNeuteringO ->
                    isNeutered = 1
                R.id.addPetFragment_rbNeuteringX ->
                    isNeutered = 0
            }
        }
    }


    fun insertPet(pet:Pet){
        var response : Response<Message>
        runBlocking {
            response = PetService().petsCreateService(pet)
            addFireBase()

        }
        val res = response.body()
        if(response.code() == 200){
            if(res!=null){
                if(res.success && res.data["isSuccess"] == true){
                    showCustomToast("등록이 완료되었습니다.")
//                    if(imgUri != null){
//                        addFireBase()
//                    }
//                        mainActivity.runOnUiThread(Runnable {
//                            this@AddPetFragment.findNavController().navigate(R.id.action_addPetFragment_to_myPageFragment)
//                        })
                }else{
                    Log.d(TAG, "insertPet: ${res.message}")
                }
            }else{
                Log.d(TAG, "insertPet: ")
            }
        }
    }

    fun updatePets(pet:Pet){
        var pageBack = false
        var response : Response<Message>
        runBlocking {
            response = PetService().petsUpdateService(pet)
            Log.d(TAG, "updatePets: ${response.code()}")
            if(pet.photoPath != beforePet.photoPath) {
                addFireBase()
                pageBack = false
            } else {
                pageBack = true
            }
        }
        val res = response.body()
        if(response.code() == 200){
            if(res!=null){
                if(res.success && res.data["isSuccess"] == true){
                    showCustomToast("수정이 완료되었습니다")
                    if(pageBack) {
                        (requireActivity() as MainActivity).onBackPressed()
                    }
//                    if(mainViewModel.uploadedImageUri != null){
//                        addFireBase()
//                    }
//                    mainActivity.runOnUiThread(Runnable {
//                        this@AddPetFragment.findNavController().navigate(R.id.action_addPetFragment_to_myPageFragment)
//                    })
                }else{
                    Log.d(TAG, "updatePet: ${res.message}")
                }
            }else{
                Log.d(TAG, "updatePet: ")
            }
        }

    }

    private fun getFileExtension(uri: Uri?): String? {
        val mimeTypeMap = MimeTypeMap.getSingleton()
        contentResolver = mainActivity.contentResolver
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri!!))
    }

    private fun setBirth() {

        val calendar: Calendar = Calendar.getInstance()
        try {
            curDate = dataFormat.parse(binding.addPetFragmentTietBirth.getText().toString())
            // 문자열로 된 생년월일을 Date로 파싱
        } catch (ex: Exception) {
            ex.printStackTrace()
        }

        calendar.setTime(curDate)

        val curYear: Int = calendar.get(Calendar.YEAR)
        val curMonth: Int = calendar.get(Calendar.MONTH)
        val curDay: Int = calendar.get(Calendar.DAY_OF_MONTH)
        // 년,월,일 넘겨줄 변수

        // 년,월,일 넘겨줄 변수
        val dialog = DatePickerDialog(requireContext(), birthDateSetListener, curYear, curMonth, curDay)
        dialog.show()
    }

    private val birthDateSetListener =
        OnDateSetListener { datePicker, year, month, day ->
            val selectedCalendar = Calendar.getInstance()
            selectedCalendar[Calendar.YEAR] = year
            selectedCalendar[Calendar.MONTH] = month
            selectedCalendar[Calendar.DAY_OF_MONTH] = day
            // 달력의 년월일을 버튼에서 넘겨받은 년월일로 설정
            val curDate = selectedCalendar.time // 현재를 넘겨줌
            setSelectedDate(curDate)
        }

    private fun setSelectedDate(curDate: Date) {
        val selectedDateStr = dataFormat.format(curDate)
        binding.addPetFragmentTietBirth.setText(selectedDateStr) // 버튼의 텍스트 수정
    }

    /**
     * 갤러리 사진 선택 result
     */
    private val filterActivityLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if(it.resultCode == AppCompatActivity.RESULT_OK && it.data != null) {
                imgSelectedChk = false
                val currentImageUri = it.data?.data

                try {
                    currentImageUri?.let {
                        imgUri = currentImageUri
//                        mainViewModel.uploadedImageUri = currentImageUri
                        fileExtension = requireActivity().contentResolver.getType(currentImageUri)
                        // 사진 set
                        Glide.with(this)
                            .load(currentImageUri)
                            .into(binding.addPEtFragmentIvPetImage)

                        fileExtension = fileExtension!!.substring(fileExtension!!.lastIndexOf("/") + 1, fileExtension!!.length)

                    }
                } catch(e:Exception) {
                    e.printStackTrace()
                }
            } else if(it.resultCode == AppCompatActivity.RESULT_CANCELED){
                showCustomToast("사진 선택 취소")
            } else {
                Log.d(TAG,"filterActivityLauncher 실패")
            }
        }

    override fun onDestroy() {
        super.onDestroy()
        mainActivity.hideBottomNavi(false)
    }

}