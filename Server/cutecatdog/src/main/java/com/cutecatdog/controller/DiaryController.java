package com.cutecatdog.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.cutecatdog.common.message.Message;
import com.cutecatdog.model.diary.DiaryDto;
import com.cutecatdog.model.diary.DiaryParamDto;
import com.cutecatdog.model.diary.HashtagDto;
import com.cutecatdog.model.diary.HashtagParamDto;
import com.cutecatdog.model.diary.PhotoDto;
import com.cutecatdog.model.diary.PhotoParamDto;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

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

    @ApiOperation(value = "일기 등록", notes = "새로 작성한 일기를 등록한다.", response = Map.class)
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
                HashtagParamDto hashtagParamDto = new HashtagParamDto();
                hashtagParamDto.setDiary_id(diaryDto.getId());
                for (HashtagDto hashtagDto : diaryDto.getHashtag()) { // 해시태그 등록
                    hashtagParamDto.setHashtag(hashtagDto.getHashtag());
                    hashtagService.addHashtag(hashtagDto.getHashtag());
                    hashtagService.addHashtagtoDiary(hashtagParamDto);
                }
                PhotoParamDto paramDto = new PhotoParamDto();
                paramDto.setDiary_id(diaryDto.getId());
                for (PhotoDto photoDto : diaryDto.getPhoto()) {
                    paramDto.setPhoto(photoDto.getPhoto());
                    photoService.addPhoto(paramDto);
                }
                response.setMessage("일기 등록 완료");
                data.put("isSuccess", true);
                response.setData(data);
                status = HttpStatus.OK;
            } else {
                response.setMessage("일기 등록 실패");
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

    @ApiOperation(value = "전체 일기 조회-최신순", notes = "최신 날짜순으로 일기를 조회한다.", response = Map.class)
    @GetMapping
    public ResponseEntity<Message> diaryListDesc(@RequestParam(value = "user_id", required = true) int user_id)
            throws Exception {
        Message response = new Message();
        HttpStatus status = null;
        try {
            response.setSuccess(true);
            HashMap<String, List<DiaryDto>> data = new HashMap<>();
            data.put("diarys", diaryService.findDiaryDesc(user_id));
            if (data.get("diarys").size() > 0) {
                for (DiaryDto diaryDto : data.get("diarys")) {
                    diaryDto.setHashtag(hashtagService.findHashtag(diaryDto.getId()));
                    diaryDto.setPhoto(photoService.findPhoto(diaryDto.getId()));
                }
                response.setData(data);
                response.setMessage("일기 목록 조회 성공");
                status = HttpStatus.OK;
            } else {
                response.setData(data);
                response.setMessage("일기 목록 없음");
                status = HttpStatus.OK;
            }
        } catch (Exception e) {
            response.setSuccess(false);
            response.setMessage(e.getMessage());
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }

        return new ResponseEntity<>(response, status);
    }

    @ApiOperation(value = "전체 일기 조회-오래된 순", notes = "오래된 날짜 순으로 일기를 조회한다.", response = Map.class)
    @GetMapping("/asc")
    public ResponseEntity<Message> diaryListAsc(@RequestParam(value = "user_id", required = true) int user_id)
            throws Exception {
        Message response = new Message();
        HttpStatus status = null;
        try {
            response.setSuccess(true);
            HashMap<String, List<DiaryDto>> data = new HashMap<>();
            data.put("diarys", diaryService.findDiaryAsc(user_id));
            if (data.get("diarys").size() > 0) {
                for (DiaryDto diaryDto : data.get("diarys")) {
                    diaryDto.setHashtag(hashtagService.findHashtag(diaryDto.getId()));
                    diaryDto.setPhoto(photoService.findPhoto(diaryDto.getId()));
                }
                response.setData(data);
                response.setMessage("일기 목록 조회 성공");
                status = HttpStatus.OK;
            } else {
                response.setData(data);
                response.setMessage("일기 목록 없음");
                status = HttpStatus.OK;
            }
        } catch (Exception e) {
            response.setSuccess(false);
            response.setMessage(e.getMessage());
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }

        return new ResponseEntity<>(response, status);
    }

    @ApiOperation(value = "날짜/기간으로 일기 조회", notes = "날짜 또는 기간 별로 일기를 조회한다. end_date를 쓰면 기간으로, 비우면 날짜로 조회", response = Map.class)
    @GetMapping({"/{user_id}/{start_date}/{end_date}", "/{user_id}/{start_date}"})
    public ResponseEntity<Message> diaryListByPeriod(@PathVariable(required = true)int user_id, @PathVariable(required = true)String start_date, @PathVariable(required = false)String end_date)
            throws Exception {
        Message response = new Message();
        HttpStatus status = null;
        try {
            response.setSuccess(true);
            HashMap<String, List<DiaryDto>> data = new HashMap<>();
            DiaryParamDto diaryParamDto = new DiaryParamDto();
            diaryParamDto.setUserId(user_id);
            diaryParamDto.setStart_date(start_date);
            diaryParamDto.setEnd_date(end_date);
            data.put("diarys", diaryService.findDiaryByDate(diaryParamDto));
            response.setData(data);
            if (data.get("diarys").size() > 0) {
                for (DiaryDto diaryDto : data.get("diarys")) {
                    diaryDto.setHashtag(hashtagService.findHashtag(diaryDto.getId()));
                    diaryDto.setPhoto(photoService.findPhoto(diaryDto.getId()));
                }
                response.setMessage("일기 목록 조회 성공");
                status = HttpStatus.OK;
            } else {
                response.setMessage("일기 목록 없음");
                status = HttpStatus.OK;
            }
        } catch (Exception e) {
            response.setSuccess(false);
            response.setMessage(e.getMessage());
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }

        return new ResponseEntity<>(response, status);
    }

    @ApiOperation(value = "일기 삭제", notes = "일기를 삭제한다.", response = Map.class)
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
                data.put("isSuccess", true);
                response.setData(data);
                status = HttpStatus.OK;
            } else {
                response.setMessage("일기 삭제 실패");
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

    @ApiOperation(value = "일기 상세 조회", notes = "일기를 상세 조회한다.", response = Map.class)
    @GetMapping("/{id}")
    public ResponseEntity<Message> diaryDetail(@PathVariable(name = "id") int id)
            throws Exception {
        Message response = new Message();
        HttpStatus status = null;
        try {
            response.setSuccess(true);
            HashMap<String, DiaryDto> data = new HashMap<>();
            DiaryDto diaryDto = diaryService.findDiaryDetail(id);
            if (diaryDto != null) {
                diaryDto.setHashtag(hashtagService.findHashtag(diaryDto.getId()));
                diaryDto.setPhoto(photoService.findPhoto(diaryDto.getId()));
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

    @ApiOperation(value = "일기 수정", notes = "일기를 수정한다.", response = Map.class)
    @PutMapping
    public ResponseEntity<Message> diaryModify(
            @RequestBody @ApiParam(value = "일기 정보", required = true) DiaryDto diaryDto) throws Exception {
        Message response = new Message();
        HttpStatus status = null;
        try {
            response.setSuccess(true);
            HashMap<String, Boolean> data = new HashMap<>();
            if (diaryService.modifyDiary(diaryDto)) {
                int diary_id = diaryDto.getId();
                ArrayList<String> hash = new ArrayList<>();
                List<String> list = hashtagService.findHashtagName(diary_id);
                if (diaryDto.getHashtag() != null) {
                    for (HashtagDto hashtagDto : diaryDto.getHashtag()) {
                        if (list.indexOf(hashtagDto.getHashtag())!=-1) {
                            list.remove(list.indexOf(hashtagDto.getHashtag()));
                        }else{
                            hash.add(hashtagDto.getHashtag());
                        }
                    }
                }
                for (String hashtag : list) { // 남아있는 해시태그 삭제 => 일기에서 삭제된 해시태그
                    HashtagParamDto hashtagParamDto = new HashtagParamDto();
                    hashtagParamDto.setDiary_id(diary_id);
                    hashtagParamDto.setHashtag(hashtag);
                    hashtagService.removeHashtagDiary(hashtagParamDto);
                }
                for (String hashtag : hash) {
                    hashtagService.addHashtag(hashtag); // 추가된 해시태그 넣기
                    HashtagParamDto hashtagParamDto = new HashtagParamDto();
                    hashtagParamDto.setDiary_id(diary_id);
                    hashtagParamDto.setHashtag(hashtag);
                    hashtagService.addHashtagtoDiary(hashtagParamDto);
                }

                ArrayList<String> photos = new ArrayList<>();
                List<PhotoDto> list2 = photoService.findPhoto(diary_id);
                if (diaryDto.getPhoto() != null) {
                    for (PhotoDto photoDto : diaryDto.getPhoto()) {
                        if (photoDto.getId() == 0) {
                            photos.add(photoDto.getPhoto());
                        } else {
                            list2.remove(list2.indexOf(photoDto));
                        }
                    }
                }
                for (PhotoDto photoDto : list2) { // 남아있는 사진 삭제 => 일기에서 삭제된 사진
                    photoService.removePhoto(photoDto.getId());
                }
                for (String photo : photos) { // 추가된 사진 넣기
                    PhotoParamDto paramDto = new PhotoParamDto();
                    paramDto.setDiary_id(diary_id);
                    paramDto.setPhoto(photo);
                    photoService.addPhoto(paramDto);
                }

                response.setMessage("일기 수정 성공");
                data.put("isSuccess", true);
                response.setData(data);
                status = HttpStatus.OK;
            } else {
                response.setMessage("일기 수정 실패");
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

}
