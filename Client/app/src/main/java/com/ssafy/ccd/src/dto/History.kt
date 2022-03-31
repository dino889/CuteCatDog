package com.ssafy.ccd.src.dto

data class History(
    val datetime: String,
    val emotion: String,
    val id: Int,
    val photoPath: String,
    val userId: Int
) {
    constructor(userId: Int, emotion: String, photoPath: String, datetime: String) : this(id = 0, userId = userId, emotion = emotion, photoPath = photoPath, datetime = datetime) // history insert

}