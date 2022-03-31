package com.cutecatdog.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.cutecatdog.common.message.Message;
import com.cutecatdog.model.pet.PetDto;
import com.cutecatdog.model.pet.PetWithKind;
import com.cutecatdog.service.PetService;

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
@RequestMapping("/pets")
@CrossOrigin
@Api("반려 동물 컨트롤러  API V1")
public class PetController {
	@Autowired
	private PetService petService;

	@ApiOperation(value = "반려동물 전체 목록", notes = "모든 반려동물들의 정보를 반환한다.", response = List.class)
	@GetMapping()
	public ResponseEntity<Message> petList() throws Exception {
		HttpStatus status = HttpStatus.OK;
		Message message = new Message();
		List<PetDto> pets = new ArrayList<>();
		pets = petService.findPet();
		HashMap<String, List<PetDto>> map = new HashMap<>();
		map.put("pets", pets);
		message.setData(map);
		message.setSuccess(true);
		return new ResponseEntity<Message>(message, status);
	}

	@ApiOperation(value = "반려동물 등록", notes = "자신의 반려 동물을 등록한다.", response = List.class)
	@PostMapping("")
	public ResponseEntity<Message> petAdd(@RequestBody PetDto petDto) throws Exception {
		Message message = new Message();
		HttpStatus status = null;
		HashMap<String, Boolean> map = new HashMap<>();
		if (petService.addPet(petDto)) {
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

	@ApiOperation(value = "반려동물 정보 수정", notes = "자신의 반려 동물을 수정한다.", response = List.class)
	@PutMapping("")
	public ResponseEntity<Message> petModify(@RequestBody PetDto petDto) throws Exception {
		Message message = new Message();
		HttpStatus status = null;
		HashMap<String, Boolean> map = new HashMap<>();
		if (petService.modifyPet(petDto)) {
			map.put("isSuccess", true);
			message.setData(map);
			status = HttpStatus.OK;
			message.setSuccess(true);
			return new ResponseEntity<Message>(message, status);
		}
		message.setSuccess(false);
		map.put("isSuccess", false);
		message.setData(map);
		status = HttpStatus.OK;
		return new ResponseEntity<Message>(message, status);
	}

	@ApiOperation(value = "반려동물 조회", notes = "반려 동물의 id 값으로 조회한다.(남의 아이도 볼 수 있다)", response = List.class)
	@GetMapping("{id}")
	public ResponseEntity<Message> petDetails(@PathVariable("id") int id) throws Exception {
		HttpStatus status = HttpStatus.OK;
		Message message = new Message();
		List<PetWithKind> pets = new ArrayList<>();
		// pets = petService.findPetDetail(id);
		// HashMap<String,List<PetDto>> map = new HashMap<>();
		PetWithKind pet = petService.findPetDetail(id);
		HashMap<String, PetWithKind> map = new HashMap<>();
		map.put("pet", pet);
		message.setData(map);
		message.setSuccess(true);
		return new ResponseEntity<Message>(message, status);
	}

	@ApiOperation(value = "반려동물 삭제", notes = "반려 동물의 id 값으로 삭제한다.", response = List.class)
	@DeleteMapping("{id}")
	public ResponseEntity<Message> petRemove(@PathVariable("id") int id) throws Exception {
		HttpStatus status = HttpStatus.OK;
		Message message = new Message();
		HashMap<String, Boolean> map = new HashMap<>();
		if (petService.removePet(id)) {
			map.put("isSuccess", true);
			message.setData(map);
			message.setSuccess(true);
			return new ResponseEntity<Message>(message, status);
		}
		map.put("isSuccess", false);
		message.setData(map);
		message.setSuccess(false);
		return new ResponseEntity<Message>(message, status);
	}

	@ApiOperation(value = "반려동물 조회", notes = "반려 동물의 userId 값으로 조회한다.(내 아이만 볼 수 있다)", response = List.class)
	@GetMapping("my/{userId}")
	public ResponseEntity<Message> mypetDetails(@PathVariable("userId") int userId) throws Exception {
		HttpStatus status = HttpStatus.OK;
		Message message = new Message();
		List<PetDto> pets = new ArrayList<>();
		HashMap<String, List<PetDto>> map = new HashMap<>();
		pets = petService.findMyPetDetail(userId);
		map.put("pets", pets);
		message.setData(map);
		message.setSuccess(true);
		return new ResponseEntity<Message>(message, status);
	}
}
