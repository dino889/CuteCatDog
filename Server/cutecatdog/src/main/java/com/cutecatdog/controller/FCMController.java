package com.cutecatdog.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.cutecatdog.common.message.Message;
import com.cutecatdog.model.UserDto;
import com.cutecatdog.model.fcm.FCMParamDto;
import com.cutecatdog.service.FCMService;
import com.cutecatdog.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@RequestMapping("/fcm")
@CrossOrigin("*")
@Slf4j
@Api(tags = "FCM")
public class FCMController {

    @Autowired
    FCMService fcmService;

    @Autowired
    UserService userService;

    @ApiOperation(value = "사용자 기기 토큰 등록", notes = "사용자의 기기 토큰값을 등록한다.", response = Map.class)
    @PostMapping("/token")
    public ResponseEntity<Message> registToken(String token, int userId) {
        log.info("registToken : token:{} {}", token, userId);
        Message response = new Message();
        HttpStatus status = null;
        HashMap<String, Boolean> data = new HashMap<>();
        try {
            status = HttpStatus.OK;
            response.setSuccess(true);
            // token이 업데이트 될 때 중복되는 토큰 값이 있으면 이 전 토큰 값 삭제
            UserDto user = userService.findUserByToken(token);
            if (user != null) {
                user.setDeviceToken(token);
                userService.modifyTokenByUserId(user);
            }
            fcmService.modifyToken(token, userId);
            response.setMessage("토큰 등록 성공");
            data.put("isSuccess", true);
            response.setData(data);
        } catch (Exception e) {
            e.printStackTrace();
            response.setSuccess(false);
            response.setMessage(e.getMessage());
            data.put("isSuccess", false);
            response.setData(data);
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }

        return new ResponseEntity<>(response, status);

    }

    @ApiOperation(value = "전체 알림 전송", notes = "공지사항 또는 이벤트와 같은 전체 알림을 전송한다.", response = Map.class)
    @PostMapping("/broadcast")
    public ResponseEntity<Message> broadCast(@RequestBody(required = true)FCMParamDto fcmParamDto) throws IOException {
        log.info("broadCast : title:{}, body:{}", fcmParamDto.getTitle(), fcmParamDto.getContent());
        Message response = new Message();
        HttpStatus status = null;
        HashMap<String, Boolean> data = new HashMap<>();
        try {
            status = HttpStatus.OK;
            response.setSuccess(true);
            if (fcmService.broadCastMessage(fcmParamDto)>0) {
                data.put("isSuccess", true);
                response.setData(data);
                response.setMessage("전체 알림 전송 성공");
            }else{
                data.put("isSuccess", false);
                response.setData(data);
                response.setMessage("전체 알림 전송 실패");
            }
        } catch (Exception e) {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
            e.printStackTrace();
            response.setSuccess(false);
            response.setMessage(e.getMessage());
            data.put("isSuccess", false);
            response.setData(data);
        }

        return new ResponseEntity<>(response, status);
    }

    @ApiOperation(value = "개인 일정 알림 전송", notes = "개인 일정 알림을 전송한다.", response = Map.class)
    @PostMapping("/sendMessageTo")
    public ResponseEntity<Message> sendMessageTo(@RequestBody(required = true)FCMParamDto fcmParamDto) throws IOException {
        log.info("sendMessageTo : token:{}, title:{}, body:{}", fcmParamDto.getToken(), fcmParamDto.getTitle(), fcmParamDto.getContent());
        Message response = new Message();
        HttpStatus status = null;
        HashMap<String, Boolean> data = new HashMap<>();
        try {
            status = HttpStatus.OK;
            response.setSuccess(true);
            fcmService.sendMessageTo(fcmParamDto);
            data.put("isSuccess", true);
            response.setData(data);
            response.setMessage("개인 일정 알림 전송 성공");
        } catch (Exception e) {
            e.printStackTrace();
            status = HttpStatus.INTERNAL_SERVER_ERROR;
            response.setSuccess(false);
            response.setMessage(e.getMessage());
            data.put("isSuccess", false);
            response.setData(data);
        }

        return new ResponseEntity<>(response, status);
    }

}
