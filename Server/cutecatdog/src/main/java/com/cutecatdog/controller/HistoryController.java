package com.cutecatdog.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.cutecatdog.common.message.Message;
import com.cutecatdog.model.history.HistoryDto;
import com.cutecatdog.model.history.HistoryRequestDto;
import com.cutecatdog.model.history.HistoryTimeDto;
import com.cutecatdog.service.HistoryService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
	public ResponseEntity<Message> historyList(@RequestParam int userId) throws Exception {
		HttpStatus status = HttpStatus.OK;
		Message message = new Message();

		List<HistoryDto> historys = new ArrayList<HistoryDto>();
		historys = historyService.findHistory(userId);
		HashMap<String, List<HistoryDto>> map = new HashMap<>();
		map.put("historys", historys);
		message.setData(map);
		message.setSuccess(true);
		return new ResponseEntity<Message>(message, status);
	}

	@ApiOperation(value = "반려동물 보기", notes = "반려 동물의 지난 일을 본다", response = List.class)
	@GetMapping("/detail")
	public ResponseEntity<Message> historyDetail(@RequestParam int id) throws Exception {
		HttpStatus status = HttpStatus.OK;
		Message message = new Message();

		HistoryDto history = new HistoryDto();
		history = historyService.findHistoryDetail(id);
		HashMap<String, HistoryDto> map = new HashMap<>();
		map.put("history", history);
		message.setData(map);
		message.setSuccess(true);
		return new ResponseEntity<Message>(message, status);
	}


	@ApiOperation(value = "반려동물 지난 일 보기", notes = "반려 동물의 지난 일을 본다", response = List.class)
	@GetMapping("{user_id}")
	public ResponseEntity<Message> historyList(@PathVariable("user_id") int userId, @RequestParam String startdate,
			String enddate) throws Exception {
		HttpStatus status = HttpStatus.OK;
		Message message = new Message();
		HistoryTimeDto historyTimeDto = new HistoryTimeDto();
		historyTimeDto.setEnddate(enddate);
		historyTimeDto.setStartdate(startdate);
		historyTimeDto.setUserId(userId);
		List<HistoryDto> historys = new ArrayList<>();
		historys = historyService.findHistoryTime(historyTimeDto);
		HashMap<String, List<HistoryDto>> map = new HashMap<>();
		map.put("history", historys);
		message.setData(map);
		message.setSuccess(true);
		return new ResponseEntity<Message>(message, status);
	}

	@ApiOperation(value = "반려동물 기록 등록", notes = "자신의 반려 동물의 기록을 등록한다.", response = List.class)
	@PostMapping()
	public ResponseEntity<Message> historyAdd(@RequestBody HistoryRequestDto historyRequestDto) throws Exception {
		Message message = new Message();
		HttpStatus status = null;
		HashMap<String, Boolean> map = new HashMap<>();
		if (historyService.addHistroy(historyRequestDto)) {
			status = HttpStatus.OK;
			message.setSuccess(true);
			map.put("isSuccess", true);
			message.setData(map);
			return new ResponseEntity<Message>(message, status);
		}
		message.setSuccess(false);
		map.put("isSuccess", false);
		message.setData(map);
		status = HttpStatus.OK;
		return new ResponseEntity<Message>(message, status);
	}

	@ApiOperation(value = "반려동물 기록 삭제", notes = "반려 동물의 기록을 id 값으로 삭제한다.", response = List.class)
	@DeleteMapping("{history_id}")
	public ResponseEntity<Message> historyRemove(@PathVariable("history_id") int history_id) throws Exception {
		HttpStatus status = HttpStatus.OK;
		Message message = new Message();
		HashMap<String, Boolean> map = new HashMap<>();
		if (historyService.removeHistory(history_id)) {
			status = HttpStatus.OK;
			message.setSuccess(true);
			map.put("isSuccess", true);
			message.setData(map);
			return new ResponseEntity<Message>(message, status);
		}
		message.setSuccess(false);
		map.put("isSuccess", false);
		message.setData(map);
		return new ResponseEntity<Message>(message, status);
	}

}
