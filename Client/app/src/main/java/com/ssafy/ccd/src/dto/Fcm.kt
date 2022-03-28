package com.ssafy.ccd.src.dto

data class Fcm (
    val token: String,
    val type: Int,
    val datetime: String,
    val title: String,
    val content: String
)