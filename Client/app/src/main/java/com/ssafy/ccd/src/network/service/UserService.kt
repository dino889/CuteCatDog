package com.ssafy.ccd.src.network.service

import com.ssafy.ccd.src.dto.Message
import com.ssafy.ccd.src.dto.User
import com.ssafy.ccd.util.RetrofitUtil
import retrofit2.Response

class UserService {

    /**
     * 회원 정보 조회
     * @param id
     * @return Response<Message>
     */
    suspend fun readUserInfo(id: Int) : Response<Message> {
        return RetrofitUtil.userService.readUserInfo(id)
    }

    /**
     * 회원가입
     * @param userDto
     * @return Response<Message>
     */
    suspend fun createUser(userDto: User) : Response<Message> {
        return RetrofitUtil.userService.createUser(userDto)
    }

    /**
     * 회원 정보 수정
     * @param userDto
     * @return Response<Message>
     */
    suspend fun updateUser(userDto: User) : Response<Message> {
        return RetrofitUtil.userService.updateUser(userDto)
    }

    /**
     * 회원 탈퇴
     * @param id
     * @return Response<Message>
     */
    suspend fun deleteUser(id: Int) : Response<Message> {
        return RetrofitUtil.userService.deleteUser(id)
    }

    /**
     * 이메일 중복 검사
     * @param email
     * @return Response<Message>
     */
    suspend fun existsUserEmail(email: String) : Response<Message> {
        return RetrofitUtil.userService.existsUserEmail(email)
    }

    /**
     * 모든 회원의 id, nickname, 프로필 이미지 조회
     * @return Response<Message>
     */
    suspend fun selectAllUsers() = RetrofitUtil.userService.selectAllUserList()
    
    /**
     * 로그인
     * @param account
     */
    suspend fun loginUser(account : User) : Response<Message> {
        return RetrofitUtil.userService.loginUser(account)
    }

    /**
     * 비밀번호 초기화
     * @param account
     * @param email
     * @return Response<Message>
     */
    suspend fun resetUserPw(account: User, email : String) : Response<Message> {
        return RetrofitUtil.userService.resetUserPw(account, email)
    }

    /**
     * 이메일 인증
     * @param email
     * @return Response<Message>
     */
    suspend fun verifyUserEmail(email: String) : Response<Message> {
        return RetrofitUtil.userService.verifyUserEmail(email)
    }
}