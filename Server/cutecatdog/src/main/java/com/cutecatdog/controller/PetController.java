package com.cutecatdog.controller;

import java.util.List;

import com.cutecatdog.common.message.Message;
import com.cutecatdog.model.PetDto;
import com.cutecatdog.service.PetService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
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
		message.setData(petService.findPet());
		message.setSuccess(true);
		return new ResponseEntity<Message>(message, status);
	}

	@ApiOperation(value = "반려동물 등록", notes = "자신의 반려 동물을 등록한다.", response = List.class)
  @PostMapping("")
  public ResponseEntity<Message> petAdd(@RequestBody PetDto petDto) throws Exception {
    Message message = new Message();
		HttpStatus status = null;
		if (petService.addPet(petDto)) {
			status = HttpStatus.OK;
			message.setSuccess(true);
			return new ResponseEntity<Message>(message, status);
		}
		message.setSuccess(false);
		status = HttpStatus.NO_CONTENT;
		return new ResponseEntity<Message>(message, status);
  }

  @ApiOperation(value = "반려동물 정보 수정", notes = "자신의 반려 동물을 수정한다.", response = List.class)
  @PutMapping("")
  public ResponseEntity<Message> petModify(@RequestBody PetDto petDto) throws Exception {
    Message message = new Message();
		HttpStatus status = null;
		if (petService.modifyPet(petDto)) {
			status = HttpStatus.OK;
			message.setSuccess(true);
			return new ResponseEntity<Message>(message, status);
		}
		message.setSuccess(false);
		status = HttpStatus.NO_CONTENT;
		return new ResponseEntity<Message>(message, status);
  }

  @ApiOperation(value = "반려동물 조회", notes = "반려 동물의 id 값으로 조회한다.(남의 아이도 볼 수 있다)", response = List.class)
	@GetMapping("{id}")
	public ResponseEntity<Message> petDetails(@PathVariable("id") int id) throws Exception {
		HttpStatus status = HttpStatus.OK;
		Message message = new Message();
		message.setData(petService.findPetDetail(id));
		message.setSuccess(true);
		return new ResponseEntity<Message>(message, status);
	}

}