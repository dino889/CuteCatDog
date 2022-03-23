package com.ssafy.ccd.src.network.api

import com.ssafy.ccd.src.dto.Board
import com.ssafy.ccd.src.dto.Comment
import com.ssafy.ccd.src.dto.Message
import retrofit2.Response
import retrofit2.http.*

interface BoardApi {

    // 전체 게시글 조회
    @GET("/board")
    suspend fun selectAllPostList() : Response<Message>

    // 게시글 등록
    @POST("/board")
    suspend fun insertPost(@Body boardAddRequestDto : Board) : Response<Message>

    // 게시글 수정
    @PUT("/board")
    suspend fun updatePost(@Body boardModifyRequestDto : Board) : Response<Message>

    // 게시글 상세 보기
    @GET("/board/{id}")
    suspend fun selectPostDetail(@Path("id") id : Int) : Response<Message>

    // 게시글 삭제
    @DELETE("/board/{id}")
    suspend fun deletePost(@Path("id") id: Int) : Response<Message>

    // 댓글 등록
    @POST("/board/comment")
    suspend fun insertComment(@Body commentRequestDto : Comment) : Response<Message>

    // 댓글 수정
    @PUT("/board/comment")
    suspend fun updateComment(@Body commentModifyRequestDto : Comment) : Response<Message>

    // 댓글 삭제
    @DELETE("/board/comment/{id}")
    suspend fun deleteComment(@Path("id") id: Int) : Response<Message>

    // 대댓글 등록
    @POST("/board/comment/add")
    suspend fun insertReply(@Body commentAddShowRequestDto: Comment) : Response<Message>

    // 게시글 타입별 게시글 조회
    @GET("/board/type/{typeId}")
    suspend fun selectPostListByType(@Path("typeId") typeId: Int) : Response<Message>



}