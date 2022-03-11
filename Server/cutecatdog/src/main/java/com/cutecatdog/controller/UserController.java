package com.cutecatdog.controller;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

import java.util.HashMap;
import java.util.Map;

import com.cutecatdog.common.message.Message;
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

    @Autowired
    private UserService userService;

    @ApiOperation(value = "회원가입", notes = "", response = Map.class)
    @PostMapping
    public ResponseEntity<Message> userAdd(
            @RequestBody @ApiParam(value = "회원정보(이메일, 비밀번호, 닉네임, 프로필 사진)", required = true) UserDto userDto)
            throws Exception {
        Message response = new Message();
        if (userService.addUser(userDto)) {
            response.setSuccess(true);
            response.setMessage("회원가입 성공");
            return new ResponseEntity<>(response, HttpStatus.OK);
        }

        response.setSuccess(false);
        response.setMessage("회원가입 실패");
        return new ResponseEntity<>(response, HttpStatus.NO_CONTENT);
    }

    @ApiOperation(value = "회원 정보 조회", notes = "", response = Map.class)
    @GetMapping
    public ResponseEntity<Message> userDetail(@RequestParam(value = "id", required = true) int user_id)
            throws Exception {
        Message response = new Message();
        UserDto userDto = userService.findUser(user_id);
        if (userDto != null) {
            response.setData(userDto);
            response.setSuccess(true);
            response.setMessage("회원 정보 조회 성공");
        } else {
            response.setSuccess(false);
            response.setMessage("회원 정보 조회 실패");
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @ApiOperation(value = "회원 정보 수정", notes = "", response = Map.class)
    @PutMapping
    public ResponseEntity<Message> userModify(
            @RequestBody @ApiParam(value = "회원 정보", required = true) UserDto userDto) throws Exception {
        Message response = new Message();
        if (userService.modifyUser(userDto)) {
            response.setSuccess(true);
            response.setMessage("회원 정보 수정 성공");
        } else {
            response.setSuccess(false);
            response.setMessage("회원 정보 수정 실패");
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @ApiOperation(value = "회원 탈퇴", notes = "", response = Map.class)
    @DeleteMapping("/{id}")
    public ResponseEntity<Message> userRemove(@PathVariable(name = "id") int user_id)
            throws Exception {
        Message response = new Message();
        if (userService.removeUser(user_id)) {
            response.setSuccess(true);
            response.setMessage("회원 탈퇴 성공");
        } else {
            response.setSuccess(false);
            response.setMessage("회원 탈퇴 실패");
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @ApiOperation(value = "이메일 중복 검사", notes = "", response = Map.class)
    @GetMapping("/exists")
    public ResponseEntity<Message> userEmailExist(
            @RequestParam(value = "email") String val) throws Exception {
        Message response = new Message();
        HashMap<String, Boolean> result = new HashMap<>();
        if (userService.checkEmail(val)) {
            response.setSuccess(true);
            response.setMessage("이미 존재하는 이메일");
            result.put("isExisted", true);
        } else {
            response.setSuccess(true);
            response.setMessage("중복된 이메일 없음");
            result.put("isExisted", false);
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // @ApiOperation(value = "닉네임 중복 확인", notes = "", response = Map.class)
    // @GetMapping("/exists/nickname?")
    // public ResponseEntity<Message> checkNickname(@RequestParam(value =
    // "nickname", required = true) String nickname) throws Exception{
    // Message response = new Message();
    // if (userService.checkNickname(nickname)) {
    // response.setSuccess(true);
    // response.setMessage(S);
    // }else{
    // response.setSuccess(false);
    // response.setMessage(F);
    // }

    // return new ResponseEntity<>(response, HttpStatus.OK);
    // }

    @ApiOperation(value = "로그인", notes = "", response = Map.class)
    @PostMapping("/login")
    public ResponseEntity<Message> login(@RequestParam(value = "이메일", required = true) String email,
            @RequestParam(value = "비밀번호", required = true) String password) {
        Message result = new Message();
        HttpStatus status = null;
        try {
            UserDto loginUser = userService.loginUser(email, password);
            if (loginUser != null) {
                result.setSuccess(true);
                result.setMessage("로그인 성공");
                result.setData(loginUser);
                status = HttpStatus.ACCEPTED;
            } else {
                result.setSuccess(false);
                ;
                result.setMessage("로그인 실패");
                status = HttpStatus.ACCEPTED;
            }
        } catch (Exception e) {
            result.setSuccess(false);
            result.setMessage(e.getMessage());
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }

        return new ResponseEntity<>(result, status);
    }

    // @ApiOperation(value = "로그아웃", notes = "", response = Map.class)
    // @PostMapping("/logout")
    // public ResponseEntity<Message> logout(@ApiParam(value = "ID", required =
    // true) int user_id) {

    // return null;
    // }

    @ApiOperation(value = "비밀번호 초기화", notes = "", response = Map.class)
    @GetMapping("/reset-password")
    public ResponseEntity<Message> resetPassword(@RequestParam(value = "email", required = true) String email)
            throws Exception {
        Message response = new Message();
        if (userService.resetPassword(email)) {
            response.setSuccess(true);
            response.setMessage("비밀번호 초기화 성공");
        } else {
            response.setSuccess(false);
            response.setMessage("비밀번호 초기화 실패");
        }
        return null;
    }
}
