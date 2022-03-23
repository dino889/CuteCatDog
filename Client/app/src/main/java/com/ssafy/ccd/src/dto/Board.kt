package com.ssafy.ccd.src.dto

data class Board(
    val id: Int,
    val userId: Int,
    val typeId: Int,
    val author: String, // 작성자 nickname - insert 시 nickname 같이 보내기
    val title: String,
    val content: String,
    val commentList : List<Comment>
) {
    constructor(userId: Int, typeId: Int, author: String, title: String, content: String) : this(0, userId, typeId, author, title, content, listOf()) // insert
    constructor(id: Int, typeId: Int, title: String, content: String) : this(id, 0, typeId, "",  title, content, listOf())    // 게시글 수정 update

}