package com.ssafy.ccd.src.network.viewmodel

import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.reflect.TypeToken
import com.ssafy.ccd.src.dto.*
import com.ssafy.ccd.src.network.service.DiaryService
import com.ssafy.ccd.src.network.service.PetService
import com.ssafy.ccd.src.network.service.UserService
import com.ssafy.ccd.util.CommonUtils
import kotlinx.coroutines.launch
import java.lang.reflect.Type
import java.net.URI

private const val TAG = "MainViewModels_ccd"
class MainViewModels : ViewModel() {

    // ---------------------------------------------------------------------------------------------
    // ---------------------------------------------------------------------------------------------
    /**
     * USER ViewModel
     */
    private val _loginUserInfo = MutableLiveData<User>()

    val loginUserInfo : LiveData<User>
        get() = _loginUserInfo

    private fun setLoginUserInfo(user: User) = viewModelScope.launch {
        _loginUserInfo.value = user
    }

    private val _userInfo = MutableLiveData<User>()

    val userInformation : LiveData<User>
        get() = _userInfo

    private fun setUserInfo(user: User) = viewModelScope.launch {
        _userInfo.value = user
    }

    suspend fun getUserInfo(userId: Int, loginChk : Boolean) : Int {
        var returnRes = -1
        val response = UserService().readUserInfo(userId)
        viewModelScope.launch {
            val res = response.body()
            if(response.code() == 200 || response.code() == 500) {
                if(res != null) {
                    if(res.success == true) {
                        if(res.data["user"] != null && res.message == "회원 정보 조회 성공") {
                            val type: Type = object : TypeToken<User>() {}.type
                            val user = CommonUtils.parseDto<User>(res.data["user"]!!, type)
                            if(loginChk == true) {  // login user
                                setLoginUserInfo(user)
                            } else {
                                setUserInfo(user)
                            }
                            returnRes = 1
                        } else if(res.data["user"] == null) {
                            returnRes = 2   // 탈퇴한 회원 정보 조회 또는 에러
                        }
                    } else {
                        returnRes = -1  // 서버 통신 오류
                        Log.e(TAG, "getUserInfo: ${res.message}")
                    }

                } else {
                    Log.d(TAG, "getUserInfoError: ${response.message()}")
                }
            }
        }
        return returnRes
    }

    suspend fun join(user: User) : Message {
        var result = Message()
        val response = UserService().createUser(user)

        viewModelScope.launch {
            val res = response.body()
            if(response.code() == 200 || response.code() == 500) {
                if(res != null) {
                    result = res
                }
            }
        }
        return result
    }

    suspend fun login(user: User) : Message {
        var result = Message()
        val response = UserService().loginUser(user)

        viewModelScope.launch {
            val res = response.body()
            if(response.code() == 200 || response.code() == 500) {
                if(res != null) {
                    result = res
                }
            }
        }
        return result
    }

    suspend fun existsChkUserEmail(email: String) : Message {
        var result = Message()
        val response = UserService().existsUserEmail(email)

        viewModelScope.launch {
            val res = response.body()
            if(response.code() == 200 || response.code() == 500) {
                if(res != null) {
                    result = res
                }
            }
        }
        return result
    }

    suspend fun sendCodeToEmail(email: String) : Message {
        var result = Message()
        val response = UserService().verifyUserEmail(email)

        viewModelScope.launch {
            val res = response.body()
            if(response.code() == 200 || response.code() == 500) {
                if(res != null) {
                    result = res
                }
            }
        }
        return result
    }

    // ---------------------------------------------------------------------------------------------
    // ---------------------------------------------------------------------------------------------
    /**
     * PET VIEW MODEL
     * */
    private val _petsList = MutableLiveData<MutableList<Pet>>()
    private val _myPetsList = MutableLiveData<MutableList<Pet>>()
    private val _pet = MutableLiveData<Pet>()
    private val _kinds = MutableLiveData<MutableList<PetKind>>()
    private val _kind = MutableLiveData<PetKind>()
    var petId:Int = -1

    val petsList : LiveData<MutableList<Pet>>
        get() = _petsList
    val myPetsList : LiveData<MutableList<Pet>>
        get() = _myPetsList
    val pet : LiveData<Pet>
        get() = _pet
    val kinds : LiveData<MutableList<PetKind>>
        get() = _kinds
    val kind : LiveData<PetKind>
        get() = _kind

    fun setPetList(list:MutableList<Pet>) = viewModelScope.launch {
        _petsList.value = list
    }
    fun setMyPetList(list:MutableList<Pet>) = viewModelScope.launch {
        _myPetsList.value = list
    }
    fun setPet(pet:Pet) = viewModelScope.launch {
        _pet.value = pet
    }
    fun setKinds(list:MutableList<PetKind>) = viewModelScope.launch {
        _kinds.value = list
    }
    fun setKind(kind:PetKind) = viewModelScope.launch {
        _kind.value = kind
    }

    suspend fun getPetsAllList(){
        val response = PetService().petsAllListService()
        viewModelScope.launch {
            val res = response.body()
            if(response.code() == 200){
                if(res != null){
                    if(res.success){
                        Log.d(TAG, "getPetsAllList: ${res.data}")
                        var type = object : TypeToken<MutableList<Pet?>?>() {}.type
                        var pet:MutableList<Pet> = CommonUtils.parseDto(res.data.get("pets")!!,type)
                        setPetList(pet)
                    }else{
                        Log.d(TAG, "getPetsAllList: ${res.message}")
                    }
                }else{
                    Log.d(TAG, "getPetsAllList: ${response.message()}")
                }
            }
        }
    }

    suspend fun getMyPetsAllList(userId:Int){
        val response = PetService().myPetsAllListService(userId)
        viewModelScope.launch {
            val res = response.body()
            if(response.code() == 200){
                if(res!=null){
                    Log.d(TAG, "getMyPetsAllList: ${res}")
                    if(res.success){
                        if(res.data.get("pets")!=null){
                            var type = object : TypeToken<MutableList<Pet?>?>() {}.type
                            var pet:MutableList<Pet> = CommonUtils.parseDto<MutableList<Pet>>(res.data.get("pets")!!, type)
                            setMyPetList(pet)
                            Log.d(TAG, "getMyPetsAllList: ${pet}")
                            Log.d(TAG, "getMyPetsAllList: ${pet.size}")
                        }
                    }else{
                        Log.d(TAG, "getMyPetsAllList: ${res.message}")
                    }
                }else{
                    Log.d(TAG, "getMyPetsAllList: ${response.message()}")
                }
            }
        }
    }

    suspend fun getPetDetailList(petId:Int){
        val response = PetService().petsDetailListService(petId)
        viewModelScope.launch {
            val res = response.body()
            if(response.code() == 200){
                if(res!=null){
                    if(res.success){
                        Log.d(TAG, "getPetDetailList: ${res.data}")
                        var type = object:TypeToken<Pet>() {}.type
                        var pet = CommonUtils.parseDto<Pet>(res.data.get("pet")!!,type)
                        setPet(pet)
                    }else{
                        Log.d(TAG, "getMyPetsAllList: ${res.message}")
                    }
                }else{
                    Log.d(TAG, "getMyPetsAllList: ${response.message()}")
                }
            }
        }
    }

    suspend fun getPetKindsAllList(){
        val response = PetService().kindsAllListService()
        viewModelScope.launch {
            val res = response.body()
            if(response.code() == 200){
                if(res!=null){
                    if(res.success){
                        var type = object :TypeToken<MutableList<PetKind>?>() {}.type
                        var kinds = CommonUtils.parseDto<MutableList<PetKind>>(res.data.get("kinds")!!,type)
                        setKinds(kinds)
                    }else{
                        Log.d(TAG, "getPetKindsAllList: ${res.message}")
                    }
                }else{
                    Log.d(TAG, "getPetKindsAllList: ${response.message()}")
                }
            }
        }
    }

    suspend fun getKindbyId(kindId:Int){
        val response = PetService().kindsById(kindId)
        viewModelScope.launch {
            val res = response.body()
            if(response.code() == 200){
                if(res!=null){
                    if(res.success){
                        var type = object:TypeToken<PetKind?>() {}.type
                        var kind = CommonUtils.parseDto<PetKind>(res.data.get("kinds")!!,type)
                        setKind(kind)
                    }else{
                        Log.d(TAG, "getKindbyId: ${res.message}")
                    }
                }else{
                    Log.d(TAG, "getKindbyId: ${response.message()}")
                }
            }
        }
    }
    /**
     * Diary View Model
     * @author BOYEON
     * @Date 2022-03-18
     * */

    val _photoUriList = arrayListOf<Uri>()
    private val _photoList = arrayListOf<Photo>()
    var uploadImages : Uri? = null
    private val _diaryList = MutableLiveData<MutableList<Diary>>()
    private val _diaryPhotoList = MutableLiveData<MutableList<Photo>>()
    private val _diary = MutableLiveData<Diary>()
    private val _hashList = MutableLiveData<MutableList<Hashtag>>()

    val diaryList : LiveData<MutableList<Diary>>
        get() = _diaryList
    val diaryPhotoList : LiveData<MutableList<Photo>>
        get() = _diaryPhotoList
    val diary : LiveData<Diary>
        get() = _diary
    val hashList : LiveData<MutableList<Hashtag>>
        get() = _hashList
    val photoUriList = MutableLiveData<ArrayList<Uri>>().apply{
        value = _photoUriList
    }
    var photoList = MutableLiveData<ArrayList<Photo>>().apply {
        value = _photoList
    }
    fun setHashtagList(list:MutableList<Hashtag>) = viewModelScope.launch {
        _hashList.value = list
    }
    fun setDiaryList(list:MutableList<Diary>) = viewModelScope.launch {
        _diaryList.value = list
    }
    fun setDiary(diary:Diary) = viewModelScope.launch {
        _diary.value = diary
    }
    fun setDiaryPhotoList(list:MutableList<Photo>) = viewModelScope.launch {
        _diaryPhotoList.value = list
    }
    fun insertPhotoUriList(uri:Uri){
        _photoUriList.add(uri)
        photoUriList.value = _photoUriList
    }
    fun insertPhotoList(photo:Photo){
        _photoList.add(photo)
        photoList.value = _photoList
    }
    fun allClearPhotoUriList(){
        _photoUriList.clear()
    }
    fun allClearPhotoList(){
        _photoList.clear()
    }
    suspend fun getDiaryList(userId:Int){
        val response = DiaryService().diaryListbyDescService(userId)
        viewModelScope.launch {
            val res = response.body()
            if(response.code() == 200){
                if(res!=null){
                    Log.d(TAG, "getDiaryList: ${res}")
                    if(res.success){
                        var type = object:TypeToken<MutableList<Diary?>?>() {}.type
                        var type2 = object:TypeToken<MutableList<Photo?>?>() {}.type
                        var diary = CommonUtils.parseDto<MutableList<Diary>>(res.data.get("diarys")!!,type)
                        setDiaryList(diary)

                    }else{
                        Log.d(TAG, "getDiaryList: ${res.message}")
                    }
                }else{
                    Log.d(TAG, "getDiaryList: ${response.message()}")
                }
            }
        }
    }
    suspend fun getDiaryDetail(id:Int){
        val response = DiaryService().diaryDetailService(id)
        viewModelScope.launch {
            val res = response.body()
            if(response.code() == 200){
                if(res!=null){
                    if(res.success){
                        Log.d(TAG, "getDiaryDetail: ${res}")
                        var type = object:TypeToken<Diary?>() {}.type
                        var diary = CommonUtils.parseDto<Diary>(res.data.get("diary")!!,type)
                        setDiary(diary)
                    }
                }
            }else{
                Log.d(TAG, "getDiaryDetail: ${response.code()}")
            }
        }
    }
    suspend fun getHashTags(){
        val response = DiaryService().getHashTagService()
        viewModelScope.launch {
            val res = response.body()
            if(response.code() == 200){
                if(res!=null){
                    if(res.success){
                        var type = object:TypeToken<MutableList<Hashtag>?>() {}.type
                        var hashs = CommonUtils.parseDto<MutableList<Hashtag>>(res.data.get("hashtags")!!,type)
                        setHashtagList(hashs)
                    }
                }
            }else{
                Log.d(TAG, "getHashTags: ${response.code()}")
            }
        }
    }
    /**
     * AI View Model
     * @Author Jueun
     * @Date 2022-03-14 13:12
     */
    lateinit var uploadedImage : Bitmap
    var uploadedImageUri : Uri? = null
    var aiType = 0
    var emotions:String = ""

}