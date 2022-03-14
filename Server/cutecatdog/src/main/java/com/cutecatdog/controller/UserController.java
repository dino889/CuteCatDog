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
        HttpStatus status = null;
        try {
            response.setSuccess(true);
            HashMap<String, Boolean> data = new HashMap<>();
            if (userService.addUser(userDto)) {
                response.setMessage("회원가입 성공");
                data.put("isSignup", true);
                response.setData(data);
                status = HttpStatus.OK;
            } else {
                response.setMessage("회원가입 실패");
                data.put("isSignup", false);
                response.setData(data);
                status = HttpStatus.NO_CONTENT;
            }
        } catch (Exception e) {
            response.setSuccess(false);
            response.setMessage(e.getMessage());
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return new ResponseEntity<>(response, status);
    }

    @ApiOperation(value = "회원 정보 조회", notes = "", response = Map.class)
    @GetMapping
    public ResponseEntity<Message> userDetail(@RequestParam(value = "id", required = true) int user_id)
            throws Exception {
        Message response = new Message();
        UserDto userDto = userService.findUser(user_id);
        HttpStatus status = null;
        try {
            response.setSuccess(true);
            HashMap<String, UserDto> data = new HashMap<>();
            if (userDto != null) {
                data.put("user", userDto);
                response.setData(data);
                response.setMessage("회원 정보 조회 성공");
                status = HttpStatus.OK;
            } else {
                data.put("user", null);
                response.setData(data);
                response.setMessage("회원 정보 조회 실패");
                status = HttpStatus.NO_CONTENT;
            }
        } catch (Exception e) {
            response.setSuccess(false);
            response.setMessage(e.getMessage());
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }

        return new ResponseEntity<>(response, status);
    }

    @ApiOperation(value = "회원 정보 수정", notes = "", response = Map.class)
    @PutMapping
    public ResponseEntity<Message> userModify(
            @RequestBody @ApiParam(value = "회원 정보", required = true) UserDto userDto) throws Exception {
        Message response = new Message();
        HttpStatus status = null;
        try {
            response.setSuccess(true);
            HashMap<String, Boolean> data = new HashMap<>();
            if (userService.modifyUser(userDto)) {
                response.setMessage("회원 정보 수정 성공");
                data.put("isModify", true);
                response.setData(data);
                status = HttpStatus.OK;
            } else {
                response.setMessage("회원 정보 수정 실패");
                data.put("isModify", false);
                response.setData(data);
                status = HttpStatus.NOT_MODIFIED;
            }
        } catch (Exception e) {
            response.setSuccess(false);
            response.setMessage(e.getMessage());
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }

        return new ResponseEntity<>(response, status);
    }

    @ApiOperation(value = "회원 탈퇴", notes = "", response = Map.class)
    @DeleteMapping("/{id}")
    public ResponseEntity<Message> userRemove(@PathVariable(name = "id") int user_id)
            throws Exception {
        Message response = new Message();
        HttpStatus status = null;
        try {
            response.setSuccess(true);
            HashMap<String, Boolean> data = new HashMap<>();
            if (userService.removeUser(user_id)) {
                response.setMessage("회원 탈퇴 성공");
                data.put("isDelete", true);
                response.setData(data);
                status = HttpStatus.OK;
            } else {
                response.setMessage("회원 탈퇴 실패");
                data.put("isDelete", false);
                response.setData(data);
                status = HttpStatus.NOT_MODIFIED;
            }
        } catch (Exception e) {
            response.setSuccess(false);
            response.setMessage(e.getMessage());
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }

        return new ResponseEntity<>(response, status);
    }

    @ApiOperation(value = "이메일 중복 검사", notes = "", response = Map.class)
    @GetMapping("/exists")
    public ResponseEntity<Message> userEmailExist(
            @RequestParam(value = "email") String val) throws Exception {
        Message response = new Message();
        HttpStatus status = null;
        try {
            response.setSuccess(true);
            HashMap<String, Boolean> data= new HashMap<>();
            if (userService.checkEmail(val)) {
                response.setMessage("이미 존재하는 이메일");
                data.put("isExisted", true);
                response.setData(data);
                status = HttpStatus.OK;
            } else {
                response.setMessage("중복된 이메일 없음");
                data.put("isExisted", false);
                response.setData(data);
                status = HttpStatus.NOT_FOUND;
            }
        } catch (Exception e) {
            response.setSuccess(false);
            response.setMessage(e.getMessage());
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }

        return new ResponseEntity<>(response, status);
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
        Message response = new Message();
        HttpStatus status = null;
        try {
            response.setSuccess(true);
            UserDto loginUser = userService.loginUser(email, password);
            HashMap<String, UserDto> data = new HashMap<>();
            if (loginUser != null) {
                data.put("user", loginUser);
                response.setMessage("로그인 성공");
                response.setData(data);
                status = HttpStatus.ACCEPTED;
            } else {
                response.setMessage("로그인 실패");
                data.put("user", null);
                response.setData(data);
                status = HttpStatus.NOT_ACCEPTABLE;
            }
        } catch (Exception e) {
            response.setSuccess(false);
            response.setMessage(e.getMessage());
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }

        return new ResponseEntity<>(response, status);
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
        HttpStatus status = null;
        try {
            response.setSuccess(true);
            HashMap<String, Boolean> data = new HashMap<>();
            if (userService.resetPassword(email)) {
                response.setMessage("비밀번호 초기화 성공");
                data.put("isReset", true);
                response.setData(data);
                status = HttpStatus.OK;
            } else {
                response.setMessage("비밀번호 초기화 실패");
                data.put("isReset", false);
                response.setData(data);
                status = HttpStatus.NOT_MODIFIED;
            }
        } catch (Exception e) {
            response.setSuccess(false);
            response.setMessage(e.getMessage());
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return new ResponseEntity<>(response, status);
    }

    @ApiOperation(value = "이메일 인증", notes = "", response = Map.class)
    @GetMapping("/verify")
    public ResponseEntity<Message> userEmailVerify(
            @RequestParam(value = "email") String email) throws Exception {
        Message response = new Message();
        HttpStatus status = null;
        try {
            response.setSuccess(true);
            String code = userService.veryfyEmail(email);
            if (code != null) {
                HashMap<String, String> data = new HashMap<>();
                response.setMessage("이메일 인증 요청 성공");
                data.put("code", code);
                response.setData(data);
                status = HttpStatus.OK;
            } else {
                HashMap<String, Boolean> data = new HashMap<>();
                response.setMessage("이메일 인증 요청 실패");
                data.put("isVerify", false);
                response.setData(data);
                status = HttpStatus.NOT_FOUND;
            }
        } catch (Exception e) {
            response.setSuccess(false);
            response.setMessage(e.getMessage());
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }

        return new ResponseEntity<>(response, status);
    }

}
