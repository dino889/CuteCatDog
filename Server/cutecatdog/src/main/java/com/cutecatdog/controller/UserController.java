package com.cutecatdog.controller;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

import java.util.Map;

import com.cutecatdog.model.ApiResponse;
import com.cutecatdog.model.UserDto;
import com.cutecatdog.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@RestController
@RequestMapping("/users")
@Api(tags = "User")
public class UserController {

    static String S = "Success";
    static String F = "Fail";
    
    @Autowired
    private UserService userService;

    @ApiOperation(value="회원가입", notes="", response=Map.class)
    @PostMapping
    public ResponseEntity<ApiResponse> signupUser(@RequestBody @ApiParam(value="회원정보(이메일, 비밀번호, 닉네임, 프로필 사진)", required = true) UserDto userDto) throws Exception{
        ApiResponse response = new ApiResponse();
        if (userService.signupUser(userDto)) {
            response.setSuccess(true);
            response.setMessage(S);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        
        response.setSuccess(false);
        response.setMessage(F);
        return new ResponseEntity<>(response, HttpStatus.NO_CONTENT);
    }

    @ApiOperation(value = "회원 정보 조회", notes = "", response = Map.class)
    @GetMapping
    public ResponseEntity<ApiResponse> findUser(@RequestParam(value = "id", required = true) int user_id) throws Exception{
        ApiResponse response = new ApiResponse();
        UserDto userDto = userService.findUser(user_id);
        if (userDto != null) {
            response.setData(userDto);
            response.setSuccess(true);
            response.setMessage(S);
        }else{
            response.setSuccess(false);
            response.setMessage(F);
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @ApiOperation(value = "회원 정보 수정", notes = "", response = Map.class)
    @PutMapping
    public ResponseEntity<ApiResponse> modifyUser(@RequestBody @ApiParam(value = "회원 정보", required = true)UserDto userDto) throws Exception{
        ApiResponse response = new ApiResponse();
        if (userService.modifyUser(userDto)) {
            response.setSuccess(true);
            response.setMessage(S);
        }else{
            response.setSuccess(false);
            response.setMessage(F);
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @ApiOperation(value = "회원 탈퇴", notes = "", response = Map.class)
    @DeleteMapping("/{user_id}")
    public ResponseEntity<ApiResponse> removeUser(@RequestParam(value = "ID", required = true) int user_id) throws Exception{
        ApiResponse response = new ApiResponse();
        if (userService.removeUser(user_id)) {
            response.setSuccess(true);
            response.setMessage(S);
        }else{
            response.setSuccess(false);
            response.setMessage(F);
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @ApiOperation(value = "이메일/닉네임 중복 확인", notes = "", response = Map.class)
    @GetMapping("/exists/{type}?{value}")
    public ResponseEntity<ApiResponse> checkEmail(@RequestParam(value = "email/nickname", required = true) String type, @RequestParam(value = "value")String val) throws Exception{
        ApiResponse response = new ApiResponse();
        if (type == "email") {
            if (userService.checkEmail(val)) {
                response.setSuccess(true);
                response.setMessage(S);
            }else{
                response.setSuccess(false);
                response.setMessage(F);
            }
        }else if (type == "nickname") {
            if (userService.checkNickname(val)) {
                response.setSuccess(true);
                response.setMessage(S);
            }else{
                response.setSuccess(false);
                response.setMessage(F);
            }
        }else{
            response.setSuccess(false);
            response.setMessage("email 또는 nickname을 입력해주세요.");
        }
        
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // @ApiOperation(value = "닉네임 중복 확인", notes = "", response = Map.class)
    // @GetMapping("/exists/nickname?")
    // public ResponseEntity<ApiResponse> checkNickname(@RequestParam(value = "nickname", required = true) String nickname) throws Exception{
    //     ApiResponse response = new ApiResponse();
    //     if (userService.checkNickname(nickname)) {
    //         response.setSuccess(true);
    //         response.setMessage(S);
    //     }else{
    //         response.setSuccess(false);
    //         response.setMessage(F);
    //     }

    //     return new ResponseEntity<>(response, HttpStatus.OK);
    // }

    @ApiOperation(value="로그인", notes="", response=Map.class)
    @PostMapping("/login")
    public ResponseEntity<ApiResponse> login(@ApiParam(value="이메일", required=true)String email, @ApiParam(value="비밀번호", required=true)String password){
        ApiResponse result = new ApiResponse();
        HttpStatus status = null;
        try{
            UserDto loginUser = userService.loginUser(email, password);
            if(loginUser != null){
                result.setSuccess(true);
                result.setMessage("Success");
                result.setData(loginUser);
                status = HttpStatus.ACCEPTED;
            }else{
                result.setSuccess(false);;
                result.setMessage("Fail");
                status = HttpStatus.ACCEPTED;
            }
        } catch(Exception e){
            result.setSuccess(false);
            result.setMessage(e.getMessage());
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }

        return new ResponseEntity<>(result, status);
    }

    @ApiOperation(value = "로그아웃", notes = "", response = Map.class)
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse> logout(@ApiParam(value = "ID", required=true)int user_id){
        
        return null;
    }

    @ApiOperation(value = "비밀번호 초기화", notes = "", response = Map.class)
    @GetMapping("/reset-password?")
    public ResponseEntity<ApiResponse> resetPassword(@RequestParam(value = "email", required=true)String email) throws Exception{
        ApiResponse response = new ApiResponse();
        if (userService.resetPassword(email)) {
            response.setSuccess(true);
            response.setMessage("비밀번호 초기화 성공");
        }else{
            response.setSuccess(false);
            response.setMessage("비밀번호 초기화 실패");
        }
        return null;
    }
}
