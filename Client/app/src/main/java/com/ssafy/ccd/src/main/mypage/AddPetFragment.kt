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
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import android.widget.AdapterView
import android.widget.ArrayAdapter
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*
import android.widget.DatePicker
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.ssafy.ccd.config.ApplicationClass
import com.ssafy.ccd.src.dto.Pet
import com.ssafy.ccd.src.main.MainActivity
import com.ssafy.ccd.src.network.service.PetService
import com.ssafy.ccd.src.network.viewmodel.MainViewModels
import kotlinx.coroutines.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.io.InputStream

private const val TAG = "AddPetFragment_ccd"
class AddPetFragment : BaseFragment<FragmentAddPetBinding>(FragmentAddPetBinding::bind, R.layout.fragment_add_pet) {
    var curDate = Date() // 현재
    private lateinit var mainActivity : MainActivity
    private val mainViewModel: MainViewModels by activityViewModels()
    val dataFormat: SimpleDateFormat = SimpleDateFormat("yyyy년 MM월 dd일")
    var kindId = 0
    var gender = -1
    var isNeutered = -1
    private lateinit var imgUri: Uri
    private var fileExtension : String? = ""
    // SimpleDateFormat 으로 포맷 결정
    var result: String = dataFormat.format(curDate)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }
    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        runBlocking {
            mainViewModel.getPetKindsAllList()
        }
        initListener()

        binding.addPetFragmentIbSelectImg.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,"image/*")
            filterActivityLauncher.launch(intent)
        }
        binding.fragmentAddPetSuccessBtn.setOnClickListener {
            val file = File(imgUri.path!!)

            var inputStream: InputStream? = null
            try {
                inputStream = requireActivity().contentResolver.openInputStream(imgUri)
            } catch (e: IOException) {
                e.printStackTrace()
            }
            val bitmap = BitmapFactory.decodeStream(inputStream)
            val byteArrayOutputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 20, byteArrayOutputStream)  // 압축해서 저장
            val requestBody = RequestBody.create(MediaType.parse("image/*"), byteArrayOutputStream.toByteArray())
            val uploadFile = MultipartBody.Part.createFormData("img", "${file.name}.${fileExtension?.substring(6)}", requestBody)
//            val gson : Gson = Gson()
//            val json = gson.toJson(user)
//            val requestBody_user = RequestBody.create(MediaType.parse("text/plain"), json)

            var pet = Pet(
                birth = binding.addPetFragmentTietBirth.text.toString(),
                gender = gender,
                id=0,
                isNeutered = isNeutered,
                kindId = kindId,
                name = binding.addPetFragmentTietName.text.toString(),
                photoPath = "",
                userId = ApplicationClass.sharedPreferencesUtil.getUser().id
            )
            insertPet(pet)
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
        for(item in 0..mainViewModel.kinds.value!!.size){
            var name = mainViewModel.kinds.value!![item].name
            kinds.add(name)
        }
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
                        runBlocking {
                            mainViewModel.getMyPetsAllList(ApplicationClass.sharedPreferencesUtil.getUser().id)
                        }
                    }else{
                        Log.d(TAG, "insertPet: ${res.message}")
                    }
                }else{
                    Log.d(TAG, "insertPet: ")
                }
            }
        }

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
    private val filterActivityLauncher : ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            if(it.resultCode == RESULT_OK && it.data != null){
                var currentImageUri = it.data?.data
                try{
                    currentImageUri?.let {
                        if(Build.VERSION.SDK_INT < 28){
                            Glide.with(requireContext())
                                .load(currentImageUri)
                                .into(binding.addPEtFragmentIvPetImage)

                            imgUri = currentImageUri
                            fileExtension = requireActivity().contentResolver.getType(currentImageUri)
                        }else{
                            Glide.with(requireContext())
                                .load(currentImageUri)
                                .into(binding.addPEtFragmentIvPetImage)

                            imgUri = currentImageUri
                            fileExtension = requireActivity().contentResolver.getType(currentImageUri)
                        }
                    }
                }catch (e:Exception){
                    e.printStackTrace()
                }
            }else if(it.resultCode == RESULT_CANCELED){
                showCustomToast("사진 선택 취소")
                imgUri = Uri.EMPTY
            }else{
                Log.d(TAG, "something Wrong")
            }
        }

}