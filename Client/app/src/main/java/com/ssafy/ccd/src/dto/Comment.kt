package com.ssafy.ccd.src.dto

data class Comment(
    val id: Int,
    val boardId: Int,
    val userId: Int,
    val parent: Int?,
    var comment: String,
    val dept: Int,
    val seq: Int,
    val nickname: String
) {
    constructor(boardId: Int, comment: String, userId: Int, nickname: String) : this(0, boardId, userId, 0, comment, 0, 0, nickname)    // comment insert
    constructor(boardId: Int, comment: String, userId: Int) : this(0, boardId, userId, 0, comment, 0, 0, "")    // comment insert
    constructor(id: Int, comment: String) : this(id, 0, 0, 0, comment = comment, 0, 0, "")    // update Comment
    constructor(boardId: Int, comment: String, parent: Int?, userId: Int) : this(0, boardId, userId, parent, comment, 0, 0, "")    // comment insert

}