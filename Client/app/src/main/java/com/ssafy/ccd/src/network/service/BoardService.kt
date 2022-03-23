package com.ssafy.ccd.src.network.service

import com.ssafy.ccd.src.dto.Board
import com.ssafy.ccd.src.dto.Comment
import com.ssafy.ccd.src.dto.Message
import com.ssafy.ccd.src.network.api.BoardApi
import com.ssafy.ccd.util.RetrofitUtil
import retrofit2.Response

class BoardService {

    suspend fun selectAllPostList() : Response<Message> = RetrofitUtil.boardService.selectAllPostList()

    suspend fun insertPost(boardAddRequestDto: Board) : Response<Message> = RetrofitUtil.boardService.insertPost(boardAddRequestDto)

    suspend fun updatePost(boardModifyRequestDto: Board) : Response<Message> = RetrofitUtil.boardService.updatePost(boardModifyRequestDto)

    suspend fun selectPostDetail(id: Int) : Response<Message> = RetrofitUtil.boardService.selectPostDetail(id)

    suspend fun deletePost(id: Int) : Response<Message> = RetrofitUtil.boardService.deletePost(id)

    suspend fun selectCommentList(postId: Int) = RetrofitUtil.boardService.selectCommentList(postId)

    suspend fun insertComment(commentRequestDto : Comment) = RetrofitUtil.boardService.insertComment(commentRequestDto)

    suspend fun updateComment(commentModifyRequestDto : Comment) = RetrofitUtil.boardService.updateComment(commentModifyRequestDto)

    suspend fun deleteComment(id: Int) = RetrofitUtil.boardService.deleteComment(id)

    suspend fun insertReply(commentAddShowRequestDto: Comment) = RetrofitUtil.boardService.insertReply(commentAddShowRequestDto)

    suspend fun selectPostIsLike(postId: Int, userId: Int) = RetrofitUtil.boardService.selectPostIsLike(postId, userId)

    suspend fun insertOrDeletePostLike(likeRequestDto: Board) = RetrofitUtil.boardService.insertOrDeletePostLike(likeRequestDto)
    
    suspend fun selectPostListByType(typeId: Int) = RetrofitUtil.boardService.selectPostListByType(typeId)

    suspend fun selectLikePostsByUserId(userId: Int) = RetrofitUtil.boardService.selectLikePostsByUserId(userId)
}