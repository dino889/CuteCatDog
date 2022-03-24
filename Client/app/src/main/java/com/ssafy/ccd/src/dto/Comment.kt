package com.ssafy.ccd.src.dto

data class Comment(
    val id: Int,
    val boardId: Int,
    val userId: Int,
    val parent: Int?,
    val comment: String,
    val dept: Int,
    val seq: Int,
    val nickname: String
) {
    constructor(boardId: Int, comment: String, userId: Int) : this(0, boardId, userId, 0, comment, 0, 0, "")    // comment insert
    constructor(id: Int, comment: String) : this(id, 0, 0, 0, comment, 0, 0, "")    // update Comment
}