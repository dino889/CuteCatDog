package com.cutecatdog.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.cutecatdog.common.message.Message;
import com.cutecatdog.model.Notification.NotificationDto;
import com.cutecatdog.service.NotificationService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@Api(tags = "Notification")
@RequestMapping("/notification")
public class NotificationController {
    
    @Autowired
    NotificationService notificationService;

    @ApiOperation(value = "사용자 알림 조회", notes = "사용자에게 온 알림을 모두 조회한다.", response = Map.class)
    @GetMapping("/{user_id}")
    public ResponseEntity<Message> notificationList(@PathVariable(name = "user_id") int userId) throws Exception {
        Message response = new Message();
        HttpStatus status = null;
        try {
            response.setSuccess(true);
            HashMap<String, List<NotificationDto>> data = new HashMap<>();
            data.put("notifications", notificationService.findNotificationList(userId));
            response.setData(data);
            status = HttpStatus.OK;
            if (data.get("notifications").size() > 0) {
                response.setMessage("알림 조회 성공");
            } else {
                response.setMessage("알림이 없습니다.");
            }
        } catch (Exception e) {
            response.setSuccess(false);
            response.setMessage(e.getMessage());
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }

        return new ResponseEntity<>(response, status);
    }

    @ApiOperation(value = "공지사항 알림 조회", notes = "공지사항 알림을 모두 조회한다.", response = Map.class)
    @GetMapping("/notice/{user_id}")
    public ResponseEntity<Message> notificationN(@PathVariable(name = "user_id") int userId) throws Exception {
        Message response = new Message();
        HttpStatus status = null;
        try {
            response.setSuccess(true);
            HashMap<String, List<NotificationDto>> data = new HashMap<>();
            data.put("notifications", notificationService.findNotificationN(userId));
            response.setData(data);
            status = HttpStatus.OK;
            if (data.get("notifications").size() > 0) {
                response.setMessage("알림 조회 성공");
            } else {
                response.setMessage("알림이 없습니다.");
            }
        } catch (Exception e) {
            response.setSuccess(false);
            response.setMessage(e.getMessage());
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }

        return new ResponseEntity<>(response, status);
    }

    @ApiOperation(value = "이벤트 알림 조회", notes = "이벤트 알림을 모두 조회한다.", response = Map.class)
    @GetMapping("/event/{user_id}")
    public ResponseEntity<Message> notificationE(@PathVariable(name = "user_id") int userId) throws Exception {
        Message response = new Message();
        HttpStatus status = null;
        try {
            response.setSuccess(true);
            HashMap<String, List<NotificationDto>> data = new HashMap<>();
            data.put("notifications", notificationService.findNotificationE(userId));
            response.setData(data);
            status = HttpStatus.OK;
            if (data.get("notifications").size() > 0) {
                response.setMessage("알림 조회 성공");
            } else {
                response.setMessage("알림이 없습니다.");
            }
        } catch (Exception e) {
            response.setSuccess(false);
            response.setMessage(e.getMessage());
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }

        return new ResponseEntity<>(response, status);
    }

    @ApiOperation(value = "개인 일정 알림 조회", notes = "사용자 일정 알림을 모두 조회한다.", response = Map.class)
    @GetMapping("/schedule/{user_id}")
    public ResponseEntity<Message> notificationP(@PathVariable(name = "user_id") int userId) throws Exception {
        Message response = new Message();
        HttpStatus status = null;
        try {
            response.setSuccess(true);
            HashMap<String, List<NotificationDto>> data = new HashMap<>();
            data.put("notifications", notificationService.findNotificationP(userId));
            response.setData(data);
            status = HttpStatus.OK;
            if (data.get("notifications").size() > 0) {
                response.setMessage("알림 조회 성공");
            } else {
                response.setMessage("알림이 없습니다.");
            }
        } catch (Exception e) {
            response.setSuccess(false);
            response.setMessage(e.getMessage());
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }

        return new ResponseEntity<>(response, status);
    }

    @ApiOperation(value = "알림 등록", notes = "", response = Map.class)
    @PostMapping
    public ResponseEntity<Message> notificationAdd(@RequestBody(required = true) NotificationDto notificationDto) throws Exception {
        Message response = new Message();
        HttpStatus status = null;
        try {
            response.setSuccess(true);
            HashMap<String, Boolean> data = new HashMap<>();
            status = HttpStatus.OK;
            if (notificationService.addNotification(notificationDto)) {
                response.setMessage("알림 등록 성공");
                data.put("isSuccess", true);
                response.setData(data);
            } else {
                response.setMessage("알림 등록 실패");
                data.put("isSuccess", false);
                response.setData(data);
            }
        } catch (Exception e) {
            response.setSuccess(false);
            response.setMessage(e.getMessage());
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }

        return new ResponseEntity<>(response, status);
    }

    @ApiOperation(value = "알림 삭제", notes = "알림을 삭제한다.", response = Map.class)
    @DeleteMapping("/{id}")
    public ResponseEntity<Message> notificationRemove(@PathVariable("id") int id) throws Exception {
        Message response = new Message();
        HttpStatus status = null;
        try {
            status = HttpStatus.OK;
            response.setSuccess(true);
            if (notificationService.removeNotification(id)) {
                response.setMessage("알림 삭제 성공");
            } else {
                response.setMessage("알림 삭제 실패");
            }
        } catch (Exception e) {
            response.setSuccess(false);
            response.setMessage(e.getMessage());
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }

        return new ResponseEntity<>(response, status);
    }
}
