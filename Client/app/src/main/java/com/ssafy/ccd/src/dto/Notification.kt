package com.ssafy.ccd.src.dto

data class Notification(
    val content: String,
    val datetime: String,
    val id: Int,
    val title: String,
    val type: Int,
    val userId: Int
)