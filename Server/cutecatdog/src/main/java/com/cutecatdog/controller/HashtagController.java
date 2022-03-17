package com.cutecatdog.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.cutecatdog.common.message.Message;
import com.cutecatdog.model.diary.HashtagDto;
import com.cutecatdog.model.diary.HashtagParamDto;
import com.cutecatdog.service.HashtagService;

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
@RequestMapping("/hashtags")
@Api(tags = "Hashtag")
public class HashtagController {

    @Autowired
    private HashtagService hashtagService;

    @ApiOperation(value = "해시태그 조회", notes = "모든 해시태그를 조회한다.", response = Map.class)
    @GetMapping
    public ResponseEntity<Message> hashtagList() throws Exception {
        Message response = new Message();
        HttpStatus status = null;
        try {
            response.setSuccess(true);
            HashMap<String, List<HashtagDto>> data = new HashMap<>();
            data.put("hashtags", hashtagService.findHashtagList());
            response.setData(data);
            status = HttpStatus.OK;
            if (data.get("hashtags").size() > 0) {
                response.setMessage("해시태그 조회 성공");
            } else {
                response.setMessage("해시태그가 없습니다.");
            }
        } catch (Exception e) {
            response.setSuccess(false);
            response.setMessage(e.getMessage());
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }

        return new ResponseEntity<>(response, status);
    }

    @ApiOperation(value = "다이어리 해시태그 조회", notes = "다이어리에 작성된 해시태그를 조회한다.", response = Map.class)
    @GetMapping("/{diary_id}")
    public ResponseEntity<Message> hashtagDiary(@PathVariable(name = "diary_id") int diary_id) throws Exception {
        Message response = new Message();
        HttpStatus status = null;
        try {
            response.setSuccess(true);
            HashMap<String, List<HashtagDto>> data = new HashMap<>();
            data.put("hashtags", hashtagService.findHashtag(diary_id));
            response.setData(data);
            status = HttpStatus.OK;
            if (data.get("hashtags").size() > 0) {
                response.setMessage("해시태그 조회 성공");
            } else {
                response.setMessage("해시태그가 없습니다.");
            }
        } catch (Exception e) {
            response.setSuccess(false);
            response.setMessage(e.getMessage());
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }

        return new ResponseEntity<>(response, status);
    }

    @ApiOperation(value = "해시태그 등록", notes = "", response = Map.class)
    @PostMapping("/{hashtag}")
    public ResponseEntity<Message> hashtagAdd(@PathVariable(name = "hashtag") String hashtag) throws Exception {
        Message response = new Message();
        HttpStatus status = null;
        try {
            response.setSuccess(true);
            HashMap<String, Boolean> data = new HashMap<>();
            if (hashtagService.addHashtag(hashtag)) {
                response.setMessage("해시태그 등록 성공");
                data.put("isSuccess", true);
                response.setData(data);
                status = HttpStatus.OK;
            } else {
                response.setMessage("해시태그 등록 실패");
                data.put("isSuccess", false);
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

    @ApiOperation(value = "일기 해시태그 등록", notes = "", response = Map.class)
    @PostMapping
    public ResponseEntity<Message> hashtagAddtoDiary(@RequestBody(required = true) HashtagParamDto hashtagParamDto)
            throws Exception {
        Message response = new Message();
        HttpStatus status = null;
        try {
            response.setSuccess(true);
            HashMap<String, Boolean> data = new HashMap<>();
            status = HttpStatus.OK;
            if (hashtagService.findHashtagId(hashtagParamDto.getHashtag()) == null) { // 등록되지 않은 해시태그
                if (hashtagService.addHashtag(hashtagParamDto.getHashtag())) { // 해시태그 등록
                    if (hashtagService.addHashtagtoDiary(hashtagParamDto)) {
                        response.setMessage("다이어리에 해시태그 등록 성공");
                        data.put("isSuccess", true);
                        response.setData(data);
                    } else {
                        response.setMessage("다이어리에 해시태그 등록 실패");
                        data.put("isSuccess", false);
                        response.setData(data);
                    }
                } else {
                    response.setMessage("해시태그 등록 실패");
                    data.put("isSuccess", false);
                    response.setData(data);
                }
            } else {
                if (hashtagService.addHashtagtoDiary(hashtagParamDto)) {
                    response.setMessage("다이어리에 해시태그 등록 성공");
                    data.put("isSuccess", true);
                    response.setData(data);
                } else {
                    response.setMessage("다이어리에 해시태그 등록 실패");
                    data.put("isSuccess", false);
                    response.setData(data);
                }
            }
        } catch (Exception e) {
            response.setSuccess(false);
            response.setMessage(e.getMessage());
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }

        return new ResponseEntity<>(response, status);
    }

    @ApiOperation(value = "해시태그 삭제", notes = "해시태그를 삭제한다.", response = Map.class)
    @DeleteMapping("/{id}")
    public ResponseEntity<Message> hashtagRemove(@PathVariable("id") int id) throws Exception {
        Message response = new Message();
        HttpStatus status = null;
        try {
            status = HttpStatus.OK;
            response.setSuccess(true);
            if (hashtagService.removeHashtag(id)) {
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
