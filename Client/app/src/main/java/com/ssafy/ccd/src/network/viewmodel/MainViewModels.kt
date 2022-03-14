package com.ssafy.ccd.src.network.viewmodel

import android.provider.ContactsContract
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.ccd.src.dto.Message
import com.ssafy.ccd.src.dto.Pet
import com.ssafy.ccd.src.dto.User
import com.ssafy.ccd.src.network.service.PetService
import com.ssafy.ccd.src.network.service.UserService
import kotlinx.coroutines.launch

private const val TAG = "MainViewModels_ccd"
class MainViewModels : ViewModel() {

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

//    suspend fun getUserInformation(userId: String, loginChk : Boolean) {
//        val response = UserService().getUser(userId)
//        viewModelScope.launch {
//            val res = response.body()
//            if(response.code() == 200) {
//                if(res != null) {
//                    if(loginChk == true) {    // 로그인 user이면
//                        setLoginUserInfo(res)
//                    } else {
//                        setUserInfo(res)
//                    }
//                    Log.d(TAG, "getUserInfoSuccess: ${response.message()}")
//                } else {
//                    Log.d(TAG, "getUserInfoError: ${response.message()}")
//                }
//            }
//        }
//    }

    private var _loginInfo = Message()

    val loginInfo : Message
        get() = _loginInfo

    private fun setChkLogin(message: Message) = viewModelScope.launch {
        _loginInfo = message
    }

    suspend fun login(user: User) {
        val response = UserService().loginUser(user)

        viewModelScope.launch {
            val res = response.body()
            if(response.code() == 200) {
                if(res != null) {
                    setChkLogin(res)
                    Log.d("loginBtnClickEvent", "login: $res")
                }
            }
        }
    }




    /**
     * PET VIEW MODEL
     * */
    private val _petsList = MutableLiveData<MutableList<Pet>>()
    private val _myPetsList = MutableLiveData<MutableList<Pet>>()

    val petsList : LiveData<MutableList<Pet>>
        get() = _petsList
    val myPetsList : LiveData<MutableList<Pet>>
        get() = _myPetsList

    fun setPetList(list:MutableList<Pet>) = viewModelScope.launch {
        _petsList.value = list
    }
    fun setMyPetList(list:MutableList<Pet>) = viewModelScope.launch {
        _myPetsList.value = list
    }

    suspend fun getPetsAllList(){
        val response = PetService().petsAllListService()
        viewModelScope.launch {
            val res = response.body()
            if(response.code() == 200){
                if(res != null){
                    if(res.success){
                        setPetList(res.data as MutableList<Pet>)
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
                    if(res.success){
                        setMyPetList(res.data as MutableList<Pet>)
                    }else{
                        Log.d(TAG, "getMyPetsAllList: ${res.message}")
                    }
                }else{
                    Log.d(TAG, "getMyPetsAllList: ${response.message()}")
                }
            }
        }
    }
}