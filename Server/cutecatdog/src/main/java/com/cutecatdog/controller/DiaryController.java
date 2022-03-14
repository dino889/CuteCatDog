package com.cutecatdog.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.cutecatdog.common.message.Message;
import com.cutecatdog.model.DiaryDto;
import com.cutecatdog.service.DiaryService;
import com.cutecatdog.service.HashtagService;
import com.cutecatdog.service.PhotoService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.v3.oas.annotations.parameters.RequestBody;

@RestController
@RequestMapping("/diary")
@Api(tags = "Diary")
public class DiaryController {
    
    @Autowired
    private DiaryService diaryService;

    @Autowired
    private HashtagService hashtagService;

    @Autowired
    private PhotoService photoService;

    @ApiOperation(value = "일기 등록", notes = "", response = Map.class)
    @PostMapping
    public ResponseEntity<Message> diaryAdd(
            @RequestBody @ApiParam(value = "일기(작성자, 제목, 내용, 작성 시간, 해시태그, 사진)", required = true) DiaryDto diaryDto)
            throws Exception {
        Message response = new Message();
        HttpStatus status = null;
        try {
            response.setSuccess(true);
            HashMap<String, Boolean> data = new HashMap<>();
            if (diaryService.addDiary(diaryDto)) {
                response.setMessage("일기 등록 완료");
                data.put("isAdd", true);
                response.setData(data);
                status = HttpStatus.OK;
            } else {
                response.setMessage("일기 등록 실패");
                data.put("isAdd", false);
                response.setData(data);
                status = HttpStatus.OK;
            }
        } catch (Exception e) {
            response.setSuccess(false);
            response.setMessage(e.getMessage());
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return new ResponseEntity<>(response, status);
    }

    @ApiOperation(value = "전체 일기 조회-최신순", notes = "", response = Map.class)
    @GetMapping
    public ResponseEntity<Message> diaryListDesc(@RequestParam(value = "user_id", required = true) int user_id)
            throws Exception {
        Message response = new Message();
        HttpStatus status = null;
        try {
            response.setSuccess(true);
            List<DiaryDto> diarys = diaryService.findDiaryDesc(user_id);
            HashMap<String, List<DiaryDto>> data = new HashMap<>();
            if (diarys != null) {
                for (DiaryDto diaryDto : diarys) {
                    diaryDto.setHashtag(hashtagService.findHashtag(diaryDto.getId()));
                    diaryDto.setPhoto(photoService.findPhoto(diaryDto.getId()));
                }
                data.put("diarys", diarys);
                response.setData(data);
                response.setMessage("일기 목록 조회 성공");
                status = HttpStatus.OK;
            } else {
                data.put("diarys", diarys);
                response.setData(data);
                response.setMessage("일기 목록 조회 실패");
                status = HttpStatus.OK;
            }
        } catch (Exception e) {
            response.setSuccess(false);
            response.setMessage(e.getMessage());
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }

        return new ResponseEntity<>(response, status);
    }

    @ApiOperation(value = "일기 삭제", notes = "", response = Map.class)
    @DeleteMapping("/{id}")
    public ResponseEntity<Message> diaryRemove(@PathVariable(name = "id") int id)
            throws Exception {
        Message response = new Message();
        HttpStatus status = null;
        try {
            response.setSuccess(true);
            HashMap<String, Boolean> data = new HashMap<>();
            if (diaryService.removeDiary(id)) {
                response.setMessage("일기 삭제 성공");
                data.put("isDelete", true);
                response.setData(data);
                status = HttpStatus.OK;
            } else {
                response.setMessage("일기 삭제 실패");
                data.put("isDelete", false);
                response.setData(data);
                status = HttpStatus.OK;
            }
        } catch (Exception e) {
            response.setSuccess(false);
            response.setMessage(e.getMessage());
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }

        return new ResponseEntity<>(response, status);
    }

    @ApiOperation(value = "일기 상세 조회", notes = "", response = Map.class)
    @GetMapping("/{id}")
    public ResponseEntity<Message> diaryDetail(@PathVariable(name = "id") int id)
            throws Exception {
        Message response = new Message();
        HttpStatus status = null;
        try {
            response.setSuccess(true);
            HashMap<String, DiaryDto> data = new HashMap<>();
            DiaryDto diaryDto = diaryService.findDiaryDetail(id);
            if (diaryService.removeDiary(id)) {
                response.setMessage("일기 조회 성공");
                data.put("diary", diaryDto);
                response.setData(data);
                status = HttpStatus.OK;
            } else {
                response.setMessage("일기 조회 실패");
                data.put("diary", null);
                response.setData(data);
                status = HttpStatus.OK;
            }
        } catch (Exception e) {
            response.setSuccess(false);
            response.setMessage(e.getMessage());
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }

        return new ResponseEntity<>(response, status);
    }

    @ApiOperation(value = "일기 수정", notes = "", response = Map.class)
    @PutMapping
    public ResponseEntity<Message> diaryModify(
            @RequestBody @ApiParam(value = "일기 정보", required = true) DiaryDto diaryDto) throws Exception {
        Message response = new Message();
        HttpStatus status = null;
        try {
            response.setSuccess(true);
            HashMap<String, Boolean> data = new HashMap<>();
            if (diaryService.modifyDiary(diaryDto)) {
                response.setMessage("일기 수정 성공");
                data.put("isModify", true);
                response.setData(data);
                status = HttpStatus.OK;
            } else {
                response.setMessage("일기 수정 실패");
                data.put("isModify", false);
                response.setData(data);
                status = HttpStatus.OK;
            }
        } catch (Exception e) {
            response.setSuccess(false);
            response.setMessage(e.getMessage());
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }

        return new ResponseEntity<>(response, status);
    }
    
}
