package com.cutecatdog.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.cutecatdog.common.message.Message;
import com.cutecatdog.model.Calendar.ScheduleDto;
import com.cutecatdog.service.ScheduleService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/calendar")
@Api(tags = "Calendar")
public class CalendarController {
    
    @Autowired
    ScheduleService scheduleService;

    @ApiOperation(value = "사용자 등록 일정 조회", notes = "사용자가 등록한 일정을 모두 조회한다.", response = Map.class)
    @GetMapping("/user/{user_id}")
    public ResponseEntity<Message> scheduleList(@PathVariable(name = "userId") int userId) throws Exception {
        Message response = new Message();
        HttpStatus status = null;
        try {
            response.setSuccess(true);
            HashMap<String, List<ScheduleDto>> data = new HashMap<>();
            data.put("schedules", scheduleService.findSchedule(userId));
            response.setData(data);
            status = HttpStatus.OK;
            if (data.get("schedules").size() > 0) {
                response.setMessage("일정 조회 성공");
            } else {
                response.setMessage("일정이 없습니다.");
            }
        } catch (Exception e) {
            response.setSuccess(false);
            response.setMessage(e.getMessage());
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }

        return new ResponseEntity<>(response, status);
    }

    @ApiOperation(value = "반려동물 일정 조회", notes = "해당 id에 해당하는 반려동물의 일정을 모두 조회한다.", response = Map.class)
    @GetMapping("/pet/{pet_id}")
    public ResponseEntity<Message> scheduleListPet(@PathVariable(name = "pet_id") int pet_id) throws Exception {
        Message response = new Message();
        HttpStatus status = null;
        try {
            response.setSuccess(true);
            HashMap<String, List<ScheduleDto>> data = new HashMap<>();
            data.put("schedules", scheduleService.findSchedulePet(pet_id));
            response.setData(data);
            status = HttpStatus.OK;
            if (data.get("schedules").size() > 0) {
                response.setMessage("일정 조회 성공");
            } else {
                response.setMessage("일정이 없습니다.");
            }
        } catch (Exception e) {
            response.setSuccess(false);
            response.setMessage(e.getMessage());
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }

        return new ResponseEntity<>(response, status);
    }

    @ApiOperation(value = "반려동물 접종/산책 일정 조회", notes = "해당 id에 해당하는 반려동물의 1: 접종/2: 산책 일정을 모두 조회한다.", response = Map.class)
    @GetMapping("/{pet_id}/{type}")
    public ResponseEntity<Message> scheduleListIno(@PathVariable(name = "pet_id") int pet_id, @PathVariable(name = "type") int type) throws Exception {
        Message response = new Message();
        HttpStatus status = null;
        try {
            response.setSuccess(true);
            HashMap<String, List<ScheduleDto>> data = new HashMap<>();
            if (type == 1) {
                data.put("schedules", scheduleService.findScheduleIno(pet_id));
            }else if(type == 2){
                data.put("schedules", scheduleService.findScheduleWalk(pet_id));
            }
            
            response.setData(data);
            status = HttpStatus.OK;
            if (data.get("schedules").size() > 0) {
                response.setMessage("일정 조회 성공");
            } else {
                response.setMessage("일정이 없습니다.");
            }
        } catch (Exception e) {
            response.setSuccess(false);
            response.setMessage(e.getMessage());
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }

        return new ResponseEntity<>(response, status);
    }

    @ApiOperation(value = "일정 등록", notes = "", response = Map.class)
    @PostMapping
    public ResponseEntity<Message> scheduleAdd(@RequestBody(required = true) ScheduleDto scheduleDto) throws Exception {
        Message response = new Message();
        HttpStatus status = null;
        try {
            response.setSuccess(true);
            HashMap<String, Boolean> data = new HashMap<>();
            status = HttpStatus.OK;
            if (scheduleService.addSchedule(scheduleDto)) {
                response.setMessage("일정 등록 성공");
                data.put("isSuccess", true);
                response.setData(data);
            } else {
                response.setMessage("일정 등록 실패");
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

    @ApiOperation(value = "일정 수정", notes = "일정을 수정한다.", response = Map.class)
    @PutMapping
    public ResponseEntity<Message> scheduleModify(@RequestBody(required = true)ScheduleDto scheduleDto) throws Exception{
        Message response = new Message();
        HttpStatus status = null;
        try {
            response.setSuccess(true);
            HashMap<String, Boolean> data = new HashMap<>();
            status = HttpStatus.OK;
            if (scheduleService.modifySchedule(scheduleDto)) {
                response.setMessage("일정 수정 성공");
                data.put("isSuccess", true);
                response.setData(data);
            } else {
                response.setMessage("일정 수정 실패");
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

    @ApiOperation(value = "일정 삭제", notes = "일정을 삭제한다.", response = Map.class)
    @DeleteMapping("/{id}")
    public ResponseEntity<Message> scheduleRemove(@PathVariable("id") int id) throws Exception {
        Message response = new Message();
        HttpStatus status = null;
        try {
            status = HttpStatus.OK;
            response.setSuccess(true);
            if (scheduleService.removeSchedule(id)) {
                response.setMessage("일정 삭제 성공");
            } else {
                response.setMessage("일정 삭제 실패");
            }
        } catch (Exception e) {
            response.setSuccess(false);
            response.setMessage(e.getMessage());
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }

        return new ResponseEntity<>(response, status);
    }
    
}
