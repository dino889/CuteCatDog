package com.ssafy.ccd.src.dto

data class CommentRequestDto (
    val boardId : Int,
    val comment : String,
    val userId : Int
)