package com.ssafy.ccd.src.network.api

import com.ssafy.ccd.src.dto.Board
import com.ssafy.ccd.src.dto.Comment
import com.ssafy.ccd.src.dto.LikeRequestDto
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

    // 게시글에 해당하는 댓글 리스트만 조회
    @GET("/board/{id}/comment")
    suspend fun selectCommentList(@Path("id") id: Int) : Response<Message>

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

    // 게시글 1개에 대해 로그인한 유저가 좋아요 눌렀는 지 확인
    @GET("/board/isLike")
    suspend fun selectPostIsLike(@Query("boardId") boardId: Int, @Query("userId") userId: Int) : Response<Message>

    // 게시글 좋아요 등록 및 삭제
    @POST("/board/likes")
    suspend fun insertOrDeletePostLike(@Body likeRequestDto: LikeRequestDto) : Response<Message>

    // 게시글 타입별 게시글 조회
    @GET("/board/type/{typeId}")
    suspend fun selectPostListByType(@Path("typeId") typeId: Int) : Response<Message>

    // 로그인한 유저가 좋아요 누른 게시글 리스트 조회
    @GET("/board/userid/{userId}")
    suspend fun selectLikePostsByUserId(@Path("userId") userId: Int) : Response<Message>

    //유저가 쓴 글 전체보기
    @GET("/board/user/{userId}")
    suspend fun selectBoardByUserId(@Path("userId")userId:Int) : Response<Message>

    // 좋아요 가능한지 확인
    @GET("/board/isLike")
    suspend fun checkLikeByBoardIdAndUserId(@Path("boardId")boardId:Int, @Path("userId")userId:Int) : Response<Message>

    // 울동네 게시글 리스트 조회
    @GET("/board/type/my")
    suspend fun selectLocalPostList(@Query("lat") lat: Double, @Query("lng") lng: Double) : Response<Message>
}