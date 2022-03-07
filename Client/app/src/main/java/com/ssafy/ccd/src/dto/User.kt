package com.ssafy.ccd.src.dto

data class User(
    val id: String,
    val password: String,
    val nickname: String,
    val phone: String,
    val email: String,
    val birth: String,
    val gender: String?,
    val type: String,
    val token: String,
    val img: String
) {
    constructor() : this("", "", "", "", "", "", "", "", "", "")
}
