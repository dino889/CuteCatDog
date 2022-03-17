package com.cutecatdog.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.cutecatdog.common.message.Message;
import com.cutecatdog.model.board.BoardAddRequestDto;
import com.cutecatdog.model.board.BoardDto;
import com.cutecatdog.model.board.BoardModifyRequestDto;
import com.cutecatdog.service.BoardService;

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
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/board")
@Api(tags = "Board")
public class BoardController {
  
  @Autowired
  private BoardService boardService;

  @ApiOperation(value = "게시글 전체 보기", notes = "", response = List.class)
  @GetMapping()
  public ResponseEntity<Message> boardList() throws Exception{
    HttpStatus status = HttpStatus.OK;
		Message message = new Message();

    List<BoardDto> boards = new ArrayList();
		boards = boardService.findBoard();
		HashMap<String,List<BoardDto>> map = new HashMap<>();
		map.put("boards", boards);
		message.setData(map);
		message.setSuccess(true);
		return new ResponseEntity<Message>(message, status);
  }

  @ApiOperation(value = "게시글 등록", notes = "게시글을 등록한다.", response = List.class)
  @PostMapping()
  public ResponseEntity<Message> boardAdd(@RequestBody BoardAddRequestDto boardAddRequestDto) throws Exception {
    Message message = new Message();
		HttpStatus status = null;
    HashMap<String,Boolean> map = new HashMap<>();
		if (boardService.addBoard(boardAddRequestDto)) {
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

  @ApiOperation(value = "게시글 수정", notes = "게시글을 수정한다.", response = List.class)
  @PutMapping()
  public ResponseEntity<Message> boardModify(@RequestBody BoardModifyRequestDto boardModifyRequestDto) throws Exception {
    Message message = new Message();
		HttpStatus status = null;
    HashMap<String,Boolean> map = new HashMap<>();

		if (boardService.modifyBoard(boardModifyRequestDto)) {
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

  @ApiOperation(value = "게시글 삭제", notes = "게시글을 삭제한다.", response = List.class)
  @DeleteMapping("{id}")
  public ResponseEntity<Message> boardRemove(@PathVariable("id") int id) throws Exception {
    Message message = new Message();
		HttpStatus status = null;
    HashMap<String,Boolean> map = new HashMap<>();
		if (boardService.removeBoard(id)) {
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
