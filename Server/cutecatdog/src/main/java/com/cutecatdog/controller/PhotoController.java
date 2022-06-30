package com.cutecatdog.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.cutecatdog.common.message.Message;
import com.cutecatdog.model.diary.PhotoDto;
import com.cutecatdog.model.diary.PhotoParamDto;
import com.cutecatdog.service.PhotoService;

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
@RequestMapping("/photos")
@Api(tags = "Photo")
public class PhotoController {

    @Autowired
    private PhotoService photoService;

    @ApiOperation(value = "다이어리 사진 조회", notes = "다이어리에 첨부된 사진을 조회한다.", response = Map.class)
    @GetMapping("/{diary_id}")
    public ResponseEntity<Message> photoList(@PathVariable(name = "diary_id") int diary_id) throws Exception {
        Message response = new Message();
        HttpStatus status = null;
        try {
            response.setSuccess(true);
            HashMap<String, List<PhotoDto>> data = new HashMap<>();
            data.put("photos", photoService.findPhoto(diary_id));
            response.setData(data);
            status = HttpStatus.OK;
            if (data.get("photos").size() > 0) {
                response.setMessage("사진 조회 성공");
            } else {
                response.setMessage("사진이 없습니다.");
            }
        } catch (Exception e) {
            response.setSuccess(false);
            response.setMessage(e.getMessage());
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }

        return new ResponseEntity<>(response, status);
    }

    @ApiOperation(value = "사진 등록", notes = "", response = Map.class)
    @PostMapping
    public ResponseEntity<Message> photoAdd(@RequestBody(required = true) PhotoParamDto paramDto) throws Exception {
        Message response = new Message();
        HttpStatus status = null;
        try {
            response.setSuccess(true);
            HashMap<String, Boolean> data = new HashMap<>();
            status = HttpStatus.OK;
            if (photoService.addPhoto(paramDto)) {
                response.setMessage("사진 등록 성공");
                data.put("isSuccess", true);
                response.setData(data);
            } else {
                response.setMessage("사진 등록 실패");
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

    @ApiOperation(value = "사진 삭제", notes = "사진을 삭제한다.", response = Map.class)
    @DeleteMapping("/{id}")
    public ResponseEntity<Message> photoRemove(@PathVariable("id") int id) throws Exception {
        Message response = new Message();
        HttpStatus status = null;
        try {
            status = HttpStatus.OK;
            response.setSuccess(true);
            if (photoService.removePhoto(id)) {
                response.setMessage("해시태그 삭제 성공");
            } else {
                response.setMessage("해시태그 삭제 실패");
            }
        } catch (Exception e) {
            response.setSuccess(false);
            response.setMessage(e.getMessage());
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }

        return new ResponseEntity<>(response, status);
    }

}
