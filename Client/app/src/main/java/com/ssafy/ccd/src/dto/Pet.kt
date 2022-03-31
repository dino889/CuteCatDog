package com.ssafy.ccd.src.dto

data class Pet(
    val birth: String,
    val gender: Int,
    val id: Int,
    val isNeutered: Int,
    val kindId: Int,
    val kind: String,
    val name: String,
    val photoPath: String,
    val userId: Int
){
    constructor():this("",0,0,0,0, "", "","",0)
    constructor(birth: String, gender: Int, id: Int, isNeutered: Int, kindId: Int, name: String, photoPath: String, userId: Int):this(birth, gender, id, isNeutered, kindId, "", name, photoPath, userId)
}