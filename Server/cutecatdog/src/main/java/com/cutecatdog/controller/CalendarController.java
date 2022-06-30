package com.cutecatdog.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.cutecatdog.common.message.Message;
import com.cutecatdog.model.Calendar.ScheduleDto;
import com.cutecatdog.service.PetService;
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

    @Autowired
    PetService petService;

    @ApiOperation(value = "사용자 등록 일정 조회", notes = "사용자가 등록한 일정을 모두 조회한다.", response = Map.class)
    @GetMapping("/user/{user_id}")
    public ResponseEntity<Message> scheduleList(@PathVariable(name = "user_id") int userId) throws Exception {
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

    @ApiOperation(value = "사용자 등록 일정 날짜별 조회", notes = "사용자가 등록한 일정을 모두 조회한다.", response = Map.class)
    @GetMapping("/{user_id}/{datetime}")
    public ResponseEntity<Message> scheduleDate(@PathVariable(name = "user_id", required = true)int userId, @PathVariable(name = "datetime", required = true)String datetime) throws Exception {
        Message response = new Message();
        HttpStatus status = null;
        try {
            response.setSuccess(true);
            ScheduleDto sch = new ScheduleDto();
            sch.setDatetime(datetime);
            sch.setUserId(userId);
            HashMap<String, List<HashMap<String, ?>>> data = new HashMap<>();
            data.put("schedules", new ArrayList<>());
            List<ScheduleDto> list = scheduleService.findScheduleDate(sch);
            for (ScheduleDto scheduleDto : list) {
                HashMap<String, Object> hash = new HashMap<>();
                hash.put("schedule", scheduleDto);
                hash.put("pet", petService.findPetDetail(scheduleDto.getPetId()));
                data.get("schedules").add(hash);
            }
            response.setData(data);
            status = HttpStatus.OK;
            if (data.get("schedules").size() > 0) {
                response.setMessage(datetime+" 일정 조회 성공");
            } else {
                response.setMessage("해당 날짜의 일정이 없습니다.");
            }
        } catch (Exception e) {
            response.setSuccess(false);
            response.setMessage(e.getMessage());
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }

        return new ResponseEntity<>(response, status);
    }

    @ApiOperation(value = "일정 상세 조회", notes = "일정의 상세 정보와 해당 일정의 반려 동물 정보를 조회한다.", response = Map.class)
    @GetMapping("detail/{id}")
    public ResponseEntity<Message> scheduleDetail(@PathVariable("id") int id) throws Exception {
        Message response = new Message();
        HttpStatus status = null;
        try {
            response.setSuccess(true);
            HashMap<String, HashMap<String, ?>> data = new HashMap<>();
            HashMap<String, Object> hash = new HashMap<>();
            ScheduleDto scheduleDto  = scheduleService.findScheduleDetail(id);
            if (scheduleDto != null) {
                hash.put("schedule", scheduleDto);
                hash.put("pet", petService.findPetDetail(scheduleDto.getPetId()));
                data.put("schedules", hash);
                response.setMessage("일정 상세정보 조회 성공");
            }else{
                data.put("scheduleResponse", null);
                response.setMessage("해당 id의 일정이 없습니다.");
            }
            response.setData(data);
            
            status = HttpStatus.OK;
        } catch (Exception e) {
            response.setSuccess(false);
            response.setMessage(e.getMessage());
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }

        return new ResponseEntity<>(response, status);
    }

    @ApiOperation(value = "최근 일주일 사용자 일정 조회", notes = "최근 일주일간 사용자의 일정을 모두 조회한다.", response = Map.class)
    @GetMapping("/{user_id}")
    public ResponseEntity<Message> scheduleWeek(@PathVariable(name = "user_id", required = true)int userId) throws Exception {
        Message response = new Message();
        HttpStatus status = null;
        try {
            response.setSuccess(true);
            HashMap<String, List<HashMap<String, ?>>> data = new HashMap<>();
            data.put("schedules", new ArrayList<>());
            List<ScheduleDto> list = scheduleService.findScheduleWeek(userId);
            for (ScheduleDto scheduleDto : list) {
                HashMap<String, Object> hash = new HashMap<>();
                hash.put("schedule", scheduleDto);
                hash.put("pet", petService.findPetDetail(scheduleDto.getPetId()));
                data.get("schedules").add(hash);
            }
            response.setData(data);
            status = HttpStatus.OK;
            if (data.get("schedules").size() > 0) {
                response.setMessage("일주일 일정 조회 성공");
            } else {
                response.setMessage("최근 1주일간 일정이 없습니다.");
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

    @ApiOperation(value = "반려동물 접종 일정 조회", notes = "해당 id에 해당하는 반려동물의 접종 일정을 모두 조회한다.", response = Map.class)
    @GetMapping("/inoculation/{pet_id}")
    public ResponseEntity<Message> scheduleListIno(@PathVariable(name = "pet_id") int pet_id) throws Exception {
        Message response = new Message();
        HttpStatus status = null;
        try {
            response.setSuccess(true);
            HashMap<String, List<ScheduleDto>> data = new HashMap<>();
            data.put("schedules", scheduleService.findScheduleIno(pet_id));
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

    @ApiOperation(value = "반려동물 산책 일정 조회", notes = "해당 id에 해당하는 반려동물의 산책 일정을 모두 조회한다.", response = Map.class)
    @GetMapping("/walk/{pet_id}")
    public ResponseEntity<Message> scheduleListWalk(@PathVariable(name = "pet_id") int pet_id) throws Exception {
        Message response = new Message();
        HttpStatus status = null;
        try {
            response.setSuccess(true);
            HashMap<String, List<ScheduleDto>> data = new HashMap<>();
            data.put("schedules", scheduleService.findScheduleWalk(pet_id));
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
