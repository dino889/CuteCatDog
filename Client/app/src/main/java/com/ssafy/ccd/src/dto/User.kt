package com.ssafy.ccd.src.dto

data class User(
    val id: Int,
    val email: String,
    val nickname: String,
    val password: String,
    val profileImage: String,
    val deviceToken: String
) {
    constructor() : this(0, "", "", "", "", "")
    constructor(id: Int) : this(id, "", "", "", "", "")
    constructor(id: Int, deviceToken: String) : this(id, "", "", "", "", deviceToken)
    constructor(email: String, password: String) : this(0, email, "", password, "", "")
    constructor(email: String, nickname: String, password: String, profileImage: String) : this(0, email, nickname, password, profileImage, "")
    constructor(id:Int, email: String, nickname: String, password: String, profileImage: String) : this(0, email, nickname, password, profileImage, "")
}