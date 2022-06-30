package com.cutecatdog.controller;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;

import com.cutecatdog.common.message.Message;
import com.cutecatdog.model.kind.KindAddRequestDto;
import com.cutecatdog.model.kind.KindDetailResponseDto;
import com.cutecatdog.model.kind.KindDto;
import com.cutecatdog.model.kind.KindmodifyRequestDto;
import com.cutecatdog.service.KindService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
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
@RequestMapping("/kinds")
@CrossOrigin
@Api("반려 동물 품종 컨트롤러  API V1")
public class KindController {

	@Autowired
	private KindService kindService;

	@ApiOperation(value = "반려동물 품종 보기", notes = "반려 동물의 품종을 본다", response = List.class)
	@GetMapping()
	public ResponseEntity<Message> kindList() throws Exception {
		HttpStatus status = HttpStatus.OK;
		Message message = new Message();

		List<KindDto> kinds = new ArrayList<>();
		kinds = kindService.findKind();
		HashMap<String, List<KindDto>> map = new HashMap<>();
		map.put("kinds", kinds);
		message.setData(map);
		message.setSuccess(true);
		return new ResponseEntity<Message>(message, status);
	}

	@ApiOperation(value = "반려동물 품종 상세 보기", notes = "해당 동물의 품종 이름 반환", response = List.class)
	@GetMapping("{kind_id}")
	public ResponseEntity<Message> kindDetail(@PathVariable("kind_id") int kindId) throws Exception {
		HttpStatus status = HttpStatus.OK;
		Message message = new Message();

		List<KindDetailResponseDto> kinds = new ArrayList<>();
		kinds.add(kindService.findKindDetail(kindId));
		HashMap<String, List<KindDetailResponseDto>> map = new HashMap<>();
		map.put("kinds", kinds);
		message.setData(map);
		message.setSuccess(true);
		return new ResponseEntity<Message>(message, status);
	}

	@ApiOperation(value = "반려동물 품종 등록", notes = "자신의 반려 동물의 품종을 등록한다.", response = List.class)
	@PostMapping()
	public ResponseEntity<Message> kindAdd(@RequestBody KindAddRequestDto kindAddRequestDto) throws Exception {
		Message message = new Message();
		HttpStatus status = null;
		HashMap<String, Boolean> map = new HashMap<>();

		if (kindService.addKind(kindAddRequestDto)) {
			status = HttpStatus.OK;
			message.setSuccess(true);
			map.put("isSuccess", true);
			message.setData(map);
			return new ResponseEntity<Message>(message, status);
		}
		map.put("isSuccess", false);
		message.setSuccess(false);
		message.setData(map);
		status = HttpStatus.OK;
		return new ResponseEntity<Message>(message, status);
	}

	@ApiOperation(value = "반려동물 품종 정보 수정", notes = "반려 동물의 품종 정보를 수정한다.", response = List.class)
	@PutMapping("")
	public ResponseEntity<Message> kindModify(@RequestBody KindmodifyRequestDto KindmodifyRequestDto) throws Exception {
		Message message = new Message();
		HttpStatus status = null;
		HashMap<String, Boolean> map = new HashMap<>();
		if (kindService.modifyKind(KindmodifyRequestDto)) {
			status = HttpStatus.OK;
			map.put("isSuccess", true);
			message.setData(map);
			message.setSuccess(true);
			return new ResponseEntity<Message>(message, status);
		}

		message.setSuccess(false);
		map.put("isSuccess", false);
		message.setData(map);
		status = HttpStatus.OK;
		return new ResponseEntity<Message>(message, status);
	}

	@ApiOperation(value = "반려동물 품종 정보 삭제", notes = "반려 동물의 품종 정보를 삭제한다.", response = List.class)
	@DeleteMapping("{id}")
	public ResponseEntity<Message> kindRemove(@PathVariable("id") int id) throws Exception {
		Message message = new Message();
		HttpStatus status = null;
		HashMap<String, Boolean> map = new HashMap<>();
		if (kindService.removeKind(id)) {
			map.put("isSuccess", true);
			status = HttpStatus.OK;
			message.setSuccess(true);
			message.setData(map);
			return new ResponseEntity<Message>(message, status);
		}
		message.setSuccess(false);
		map.put("isSuccess", false);
		message.setData(map);
		status = HttpStatus.OK;
		return new ResponseEntity<Message>(message, status);
	}

}
