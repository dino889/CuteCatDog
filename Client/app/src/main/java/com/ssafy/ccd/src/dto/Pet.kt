package com.ssafy.ccd.src.dto

data class Pet(
    val birth: String,
    val gender: Int,
    val id: Int,
    val isNeutered: Int,
    val kindId: Int,
    val name: String,
    val photoPath: String,
    val userId: Int
){
    constructor():this("",0,0,0,0,"","",0)
}