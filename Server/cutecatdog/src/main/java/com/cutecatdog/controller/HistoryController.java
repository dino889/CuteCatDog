package com.cutecatdog.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.cutecatdog.common.message.Message;
import com.cutecatdog.model.history.HistoryDto;
import com.cutecatdog.model.history.HistoryTimeDto;
import com.cutecatdog.service.HistoryService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController 
@RequestMapping("/history")
@CrossOrigin
@Api("이전 정보 보기 컨트롤러  API V1")
public class HistoryController {
  
  @Autowired
  private HistoryService historyService;
  
  @ApiOperation(value = "반려동물 지난 일 보기", notes = "반려 동물의 지난 일을 본다", response = List.class)
  @GetMapping()
  public ResponseEntity<Message> historyList(@RequestParam int petId) throws Exception{
    HttpStatus status = HttpStatus.OK;
		Message message = new Message();

    List<HistoryDto> historys = new ArrayList();
		historys = historyService.findHistory(petId);
		HashMap<String,List<HistoryDto>> map = new HashMap<>();
		map.put("historys", historys);
		message.setData(map);
		message.setSuccess(true);
		return new ResponseEntity<Message>(message, status);
  }

  @ApiOperation(value = "반려동물 지난 일 보기", notes = "반려 동물의 지난 일을 본다", response = List.class)
  @GetMapping("{pet_id}")
  public ResponseEntity<Message> historyList(@PathVariable("pet_id") int petId, @RequestParam String startdate, String enddate ) throws Exception{
    HttpStatus status = HttpStatus.OK;
		Message message = new Message();
    HistoryTimeDto historyTimeDto = new HistoryTimeDto();
    historyTimeDto.setEnddate(enddate);
    historyTimeDto.setStartdate(startdate);
    historyTimeDto.setPetId(petId);
    List<HistoryDto> historys = new ArrayList<>();
		historys = historyService.findHistoryTime(historyTimeDto);
		HashMap<String,List<HistoryDto>> map = new HashMap<>();
		map.put("history", historys);
		message.setData(map);
		message.setSuccess(true);
		return new ResponseEntity<Message>(message, status);
  }

}
