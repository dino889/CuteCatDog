package com.ssafy.ccd.src.dto

data class Board(
    val id: Int,
    val userId: Int,
    val typeId: Int,
    val author: String, // 작성자 nickname - insert 시 nickname 같이 보내기
    var title: String,
    var content: String,
    val commentList : List<Comment>,
    val count: Int,
    val time: String,
    val photoPath: String,
    val commentCnt: Int,
    val lat: Double,
    val lng: Double
) {
    constructor(userId: Int, typeId: Int, author: String, title: String, content: String, time: String, photoPath: String) : this(0, userId, typeId, author, title, content, listOf(), 0, time, photoPath, 0, 0.0, 0.0) // insert
    constructor(userId: Int, typeId: Int, author: String, title: String, content: String, time: String, photoPath: String, lat: Double, lng: Double) : this(0, userId, typeId, author, title, content, listOf(), 0, time, photoPath, 0, lat, lng) // insert - 울동네
    constructor(id: Int, typeId: Int, title: String, content: String, photoPath: String) : this(id, 0, typeId, "",  title, content, listOf(), 0, "", photoPath, 0, 0.0, 0.0)    // 게시글 수정 update
    constructor(id: Int, userId: Int) : this(id, userId, 0, "",  "", "", listOf(), 0, "", "", 0, 0.0, 0.0)    // 게시글 좋아요 클릭
}