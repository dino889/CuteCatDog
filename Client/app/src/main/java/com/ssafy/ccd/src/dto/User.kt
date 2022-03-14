package com.ssafy.ccd.src.dto

data class User(
    val id: Int,
    val email: String,
    val nickname: String,
    val password: String,
    val profile_image: String,
    val device_token: String
) {
    constructor() : this(0, "", "", "", "", "")
    constructor(id: Int, device_token: String) : this(id, "", "", "", "", device_token)
    constructor(id:Int, email: String, nickname: String, password: String) : this(0, email, nickname, password, "", "")
}