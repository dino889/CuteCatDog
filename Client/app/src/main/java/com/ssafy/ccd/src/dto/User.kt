package com.ssafy.ccd.src.dto

data class User(
    val id: Int,
    val email: String,
    val nickname: String,
    val password: String,
    val profileImage: String,
    val deviceToken: String,
    val socialType : String
) {
    constructor() : this(0, "", "", "", "", "", "")
    constructor(id: Int) : this(id, "", "", "", "", "", "")
    constructor(id: Int, deviceToken: String) : this(id, "", "", "", "", deviceToken, "")
    constructor(email: String, password: String) : this(0, email, "", password, "", "", "")
    constructor(email: String, nickname: String, password: String, profileImage: String, socialType: String) : this(0, email, nickname, password, profileImage, "", socialType)
}