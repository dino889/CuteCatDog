package com.ssafy.ccd.src.dto

data class Diary(
    val content: String,
    val datetime: String,
    val hashtag: List<Hashtag>,
    val id: Int,
    val photo: List<Photo>,
    val title: String,
    val userId: Int
)