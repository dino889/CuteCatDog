package com.cutecatdog.controller;

import java.util.HashMap;
import java.util.List;

import com.cutecatdog.common.message.Message;
import com.cutecatdog.model.friend.FriendDto;
import com.cutecatdog.model.friend.FriendResponseDto;
import com.cutecatdog.service.FriendService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
@RequestMapping("/friend")
@Api(tags = "Friend")
public class FriendController {
  @Autowired
  private FriendService friendService;

  @ApiOperation(value = "산책 친구 등록", notes = "친구를 등록한다.", response = List.class)
  @PostMapping()
  public ResponseEntity<Message> friendAdd(@RequestBody FriendDto friendDto) throws Exception {
    Message message = new Message();
    HttpStatus status = HttpStatus.OK;
    HashMap<String, Boolean> map = new HashMap<>();
    if(friendService.addFriend(friendDto)){
      map.put("isSuccess", true);
      message.setData(map);
      message.setMessage("성공");
      message.setSuccess(true);
      return new ResponseEntity<Message>(message, status);
    }
    map.put("isSuccess", false);
    message.setData(map);
    message.setMessage("실패(친구 중복 또는 본인 이메일 입력)");
    message.setSuccess(false);
    return new ResponseEntity<Message>(message, status);
  }

  @ApiOperation(value = "산책 친구 제거", notes = "친구를 제거한다.", response = List.class)
  @DeleteMapping()
  public ResponseEntity<Message> friendRemove(@RequestParam String friendEmail, @RequestParam int userId) throws Exception {
    Message message = new Message();
    HttpStatus status = HttpStatus.OK;
    HashMap<String, Boolean> map = new HashMap<>();
    FriendDto dto = new FriendDto();
    dto.setFriendEmail(friendEmail);
    dto.setUserId(userId);
    if(friendService.removeFriend(dto)){
      map.put("isSuccess", true);
      message.setData(map);
      message.setMessage("성공");
      message.setSuccess(true);
      return new ResponseEntity<Message>(message, status);
    }
    map.put("isSuccess", false);
    message.setData(map);
    message.setMessage("실패");
    message.setSuccess(false);
    return new ResponseEntity<Message>(message, status);
  }
  
  @ApiOperation(value = "내 산책 친구 보기", notes = "", response = List.class)
  @GetMapping("{userId}")
  public ResponseEntity<Message> friendList(@PathVariable("userId") int userId) throws Exception{
    HttpStatus status = HttpStatus.OK;
		Message message = new Message();
    HashMap<String,List<FriendResponseDto>> map = new HashMap<>();
		List<FriendResponseDto> friends = friendService.findFriend(userId);
    
    map.put("friends", friends);
    message.setData(map);
    message.setSuccess(true);
    message.setMessage("친구 출력 완료");
		status = HttpStatus.OK;
		return new ResponseEntity<Message>(message, status);
  }

  @ApiOperation(value = "email로 사람 찾기", notes = "", response = List.class)
  @GetMapping("/anothor/{email}")
  public ResponseEntity<Message> friendList(@PathVariable("email") String email) throws Exception{
    HttpStatus status = HttpStatus.OK;
		Message message = new Message();
    HashMap<String,List<FriendResponseDto>> map = new HashMap<>();
		List<FriendResponseDto> friends = friendService.findByEmailFriend(email);
    
    map.put("friends", friends);
    message.setData(map);
    message.setSuccess(true);
    message.setMessage("친구 출력 완료");
		status = HttpStatus.OK;
		return new ResponseEntity<Message>(message, status);
  }

}
