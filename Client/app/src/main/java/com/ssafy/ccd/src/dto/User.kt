package com.ssafy.ccd.src.dto

data class User(
    val email: String,
    val id: Int,
    val nickname: String,
    val password: String,
    val profile_image: String
){
    constructor() : this("",0,"","","")
    constructor(id:Int) : this("",id,"","","")
    constructor(email: String, password: String):this(email, 0, "", password, "")
}