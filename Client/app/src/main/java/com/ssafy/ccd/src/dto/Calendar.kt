package com.ssafy.ccd.src.dto

data class Calendar(
    val datetime: String,
    val id: Int,
    val memo: String,
    val petId: Int,
    val title: String,
    val type: Int,
    val userId: Int,
    val place:String
)