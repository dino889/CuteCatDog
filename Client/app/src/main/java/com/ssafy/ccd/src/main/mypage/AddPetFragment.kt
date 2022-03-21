package com.ssafy.ccd.src.main.mypage

import android.app.Activity.RESULT_CANCELED
import android.app.Activity.RESULT_OK
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ssafy.ccd.R
import com.ssafy.ccd.config.BaseFragment
import com.ssafy.ccd.databinding.FragmentAddPetBinding
import com.ssafy.ccd.databinding.FragmentMyPageBinding
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import android.webkit.MimeTypeMap
import android.widget.AdapterView
import android.widget.ArrayAdapter
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*
import android.widget.DatePicker
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.net.toUri
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.google.gson.Gson
import com.ssafy.ccd.config.ApplicationClass
import com.ssafy.ccd.src.dto.Pet
import com.ssafy.ccd.src.main.MainActivity
import com.ssafy.ccd.src.network.service.PetService
import com.ssafy.ccd.src.network.viewmodel.MainViewModels
import com.ssafy.ccd.util.CommonUtils
import kotlinx.coroutines.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.time.LocalDate

private const val TAG = "AddPetFragment_ccd"
class AddPetFragment : BaseFragment<FragmentAddPetBinding>(FragmentAddPetBinding::bind, R.layout.fragment_add_pet) {
    var curDate = Date() // 현재
    private lateinit var mainActivity : MainActivity
    private var petId = -1
//    private val mainViewModel: MainViewModels by activityViewModels()

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }
    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        runBlocking {
            mainViewModel.getPetKindsAllList()
        }

        initListener()
        Log.d(TAG, "onViewCreated: ${mainViewModel.petId}")
        if(mainViewModel.petId!! > 0){
            runBlocking {
                mainViewModel.getPetDetailList(mainViewModel.petId)
            }
            flag = 2
            initData()
        }

        binding.addPetFragmentIbSelectImg.setOnClickListener {
            mainActivity.getAlbum(GALLERY_CODE)
            loadImage()
        }
        binding.fragmentAddPetSuccessBtn.setOnClickListener {
            if(flag==1){
                if(mainViewModel.uploadedImageUri == null || mainViewModel.uploadedImageUri.toString() == ""){
                    fileName = ""
                }else{
                    timeName = System.currentTimeMillis().toString();
                    fileName = "${ApplicationClass.sharedPreferencesUtil.getUser().id}/${timeName}."+GetFileExtension(mainViewModel.uploadedImageUri)
                }
                var pet = Pet(
                    birth = CommonUtils.makeBirthMilliSecond(binding.addPetFragmentTietBirth.text.toString()),
                    gender = gender,
                    id=0,
                    isNeutered = isNeutered,
                    kindId = kindId,
                    name = binding.addPetFragmentTietName.text.toString(),
                    photoPath = fileName,
                    userId = ApplicationClass.sharedPreferencesUtil.getUser().id
                )
                Log.d(TAG, "onViewCreated: INSERT")
                insertPet(pet)
            }else{
                if(mainViewModel.uploadedImageUri == null || mainViewModel.uploadedImageUri.toString() == "" ){
                    fileName = ""
                }else{
                    timeName = System.currentTimeMillis().toString();
                    fileName = "${ApplicationClass.sharedPreferencesUtil.getUser().id}/${timeName}."+GetFileExtension(mainViewModel.uploadedImageUri)
                }
                var pet = Pet(
                    birth = CommonUtils.makeBirthMilliSecond(binding.addPetFragmentTietBirth.text.toString()),
                    gender = gender,
                    id=mainViewModel.petId,
                    isNeutered = isNeutered,
                    kindId = kindId,
                    name = binding.addPetFragmentTietName.text.toString(),
                    photoPath = fileName,
                    userId = ApplicationClass.sharedPreferencesUtil.getUser().id
                )
                Log.d(TAG, "onViewCreated: UPDATE")
                updatePets(pet)
            }
        }

        binding.fragmentAddPetBack.setOnClickListener {
            this@AddPetFragment.findNavController().popBackStack()
        }
    }
    fun initData(){
        binding.fragmentAddPetSuccessBtn.setText("수정")

        mainViewModel.pet.observe(viewLifecycleOwner, {
            binding.addPetFragmentTietName.setText(it.name.toString())
            binding.addPetFragmentTietBirth.setText(it.birth.toString())
            binding.addPetFragmentAutoKind.setText(it.kindId.toString())
            mainViewModel.uploadedImageUri = it.photoPath.toUri()
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
            if(it.photoPath == null || it.photoPath==""){
                binding.addPEtFragmentIvPetImage.setImageResource(R.drawable.logo)
                mainViewModel.uploadedImageUri = null
            }else{
                var storage = FirebaseStorage.getInstance("gs://cutecatdog-32527.appspot.com/")
                var storageRef = storage.reference
                storageRef.child("${it.photoPath}").downloadUrl.addOnSuccessListener(object : OnSuccessListener<Uri> {
                    override fun onSuccess(p0: Uri?) {
                        binding.addPEtFragmentIvPetImage.setImageURI(p0)
                    }

                }).addOnFailureListener(object: OnFailureListener {
                    override fun onFailure(p0: Exception) {
                    }
                })
            }
        })
    }


    fun loadImage(){
        Log.d(TAG, "loadImage: ${mainViewModel.uploadedImageUri}")
        binding.addPEtFragmentIvPetImage.setImageURI(mainViewModel.uploadedImageUri)

    }
    fun addFireBase(){
        if(mainViewModel.uploadedImageUri == null){
            Log.e("ERROR", "이미지 Uri에서 문제가 발생하였습니다.")
            showCustomToast("이미지 Uri에서 문제가 발생하였습니다.")
            childFragmentManager.popBackStack()
        }

        val storageReferenceChild = FirebaseStorage.getInstance().getReference("${ApplicationClass.sharedPreferencesUtil.getUser().id}").child(timeName+"."+GetFileExtension(mainViewModel.uploadedImageUri))

        storageReferenceChild.putFile(mainViewModel.uploadedImageUri!!)
            .addOnSuccessListener{
                storageReferenceChild.downloadUrl
                    .addOnSuccessListener {
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
    private fun initKinds(){
        var kinds = mutableListOf<String>()
        for(item in 0..mainViewModel.kinds.value!!.size-1){
            var name = mainViewModel.kinds.value!![item].name
            kinds.add(name)
        }
        Log.d(TAG, "initKinds: ${kinds}")
        var adapter = ArrayAdapter<String>(requireContext(),android.R.layout.simple_dropdown_item_1line,kinds)
        binding.addPetFragmentAutoKind.setAdapter(adapter)

        binding.addPetFragmentAutoKind.setOnItemClickListener { parent, view, position, id ->
            kindId = mainViewModel.kinds.value!![position].id
        }
    }
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
        GlobalScope.launch {
            var response = PetService().petsCreateService(pet)
            val res = response.body()
            if(response.code() == 200){
                if(res!=null){
                    if(res.success){
                        if(mainViewModel.uploadedImageUri != null){
                            addFireBase()
                        }
                        mainActivity.runOnUiThread(Runnable {
                            this@AddPetFragment.findNavController().navigate(R.id.action_addPetFragment_to_myPageFragment)
                        })
                    }else{
                        Log.d(TAG, "insertPet: ${res.message}")
                    }
                }else{
                    Log.d(TAG, "insertPet: ")
                }
            }
        }

    }
    fun updatePets(pet:Pet){
        GlobalScope.launch {
            var response = PetService().petsUpdateService(pet)
            Log.d(TAG, "updatePets: ${response.code()}")
            val res = response.body()
            if(response.code() == 200){
                if(res!=null){
                    if(res.success){
                        if(mainViewModel.uploadedImageUri != null){
                            addFireBase()
                        }
                        mainActivity.runOnUiThread(Runnable {
                            this@AddPetFragment.findNavController().navigate(R.id.action_addPetFragment_to_myPageFragment)
                        })
                    }else{
                        Log.d(TAG, "updatePet: ${res.message}")
                    }
                }else{
                    Log.d(TAG, "updatePet: ")
                }
            }
        }
    }
    fun GetFileExtension(uri: Uri?): String? {
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

}