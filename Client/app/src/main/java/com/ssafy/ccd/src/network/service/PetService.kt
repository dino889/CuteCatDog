package com.ssafy.ccd.src.network.service

import com.ssafy.ccd.src.dto.Message
import com.ssafy.ccd.src.dto.Pet
import com.ssafy.ccd.util.RetrofitUtil
import okhttp3.MultipartBody
import retrofit2.Response

class PetService {
    //반려동물 등록
    suspend fun petsCreateService(pet: Pet): Response<Message>{
        return RetrofitUtil.petService.petsCreate(pet)
    }

    //반려동물 전체리스트 조회
    suspend fun petsAllListService():Response<Message>{
        return RetrofitUtil.petService.petsAllList()
    }

    //반려동물 상세정보 조회
    suspend fun petsDetailListService(id:Int):Response<Message>{
        return RetrofitUtil.petService.petsDetailList(id)
    }

    //내 반려동물 전체리스트 조회
    suspend fun myPetsAllListService(userId:Int):Response<Message>{
        return RetrofitUtil.petService.myPetsAllList(userId)
    }

    //반려동물 정보 수정
    suspend fun petsUpdateService(pet:Pet):Response<Message>{
        return RetrofitUtil.petService.petsUpdate(pet)
    }

    //반려동물 삭제
    suspend fun petsDeleteService(id:Int):Response<Message>{
        return RetrofitUtil.petService.petsDelete(id)
    }

    //반려동물 품종 전체조회
    suspend fun kindsAllListService():Response<Message>{
        return RetrofitUtil.petService.kindsAllList()
    }

    suspend fun kindsById(kindId:Int):Response<Message> {
        return RetrofitUtil.petService.kindsbyId(kindId)
    }

    suspend fun getAipetType(file: MultipartBody.Part):Response<Message>{
        return RetrofitUtil.petService.getAipetType(file)
    }
}