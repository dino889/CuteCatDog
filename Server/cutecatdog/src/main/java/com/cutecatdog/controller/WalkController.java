package com.cutecatdog.controller;

import java.util.HashMap;
import java.util.List;

import com.cutecatdog.common.message.Message;
import com.cutecatdog.model.walk.WalkInsertRequestDto;
import com.cutecatdog.model.walk.WalkRequestDto;
import com.cutecatdog.model.walk.WalkResponseDto;
import com.cutecatdog.service.WalkService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/walk")
@Api(tags = "Walk")
public class WalkController {
  
  @Autowired
  private WalkService WalkService;

  @ApiOperation(value = "산책 추천 경로", notes = "", response = List.class)
  @GetMapping("")
  @ApiImplicitParam(name="range",value="0.5를 입력하면 500m, 1을 입력하면 1km")
  public ResponseEntity<Message> walkList(@RequestParam("lat") double lat, @RequestParam("lng") double lng, @RequestParam("range") double range) throws Exception{
    HttpStatus status = HttpStatus.OK;
		Message message = new Message();
    WalkRequestDto dto = new WalkRequestDto();
    dto.setLat(lat);
    dto.setLng(lng);
    dto.setRange(range);
    List<WalkResponseDto> walks;
		walks = WalkService.findWalk(dto);
		HashMap<String,List<WalkResponseDto>> map = new HashMap<>();
		map.put("walks", walks);
		message.setData(map);
		message.setSuccess(true);
		return new ResponseEntity<Message>(message, status);
  }

  @ApiOperation(value = "산책 경로 등록", notes = "산책 경로 등록한다.", response = List.class)
  @PostMapping()
  public ResponseEntity<Message> walkAdd(@RequestBody WalkInsertRequestDto walkInsertRequestDto) throws Exception {
    Message message = new Message();
    HttpStatus status = null;
    HashMap<String, Boolean> map = new HashMap<>();
    if (WalkService.addWalk(walkInsertRequestDto)) {
      status = HttpStatus.OK;
      map.put("isSuccess", true);
      message.setSuccess(true);
      message.setData(map);
      return new ResponseEntity<Message>(message, status);
    }
    map.put("isSuccess", false);
    message.setSuccess(false);
    message.setData(map);
    status = HttpStatus.OK;
    return new ResponseEntity<Message>(message, status);
  }

}
