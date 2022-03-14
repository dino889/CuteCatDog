package com.ssafy.ccd.src.network.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.ccd.src.dto.Pet
import com.ssafy.ccd.src.network.service.PetService
import kotlinx.coroutines.launch

private const val TAG = "MainViewModels_ccd"
class MainViewModels : ViewModel() {
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
}