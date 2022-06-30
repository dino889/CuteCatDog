package com.cutecatdog.controller;

import java.util.HashMap;
import java.util.List;

import com.cutecatdog.common.message.Message;
import com.cutecatdog.model.UserDto;
import com.cutecatdog.model.board.BoardAddRequestDto;
import com.cutecatdog.model.board.BoardDetailDto;
import com.cutecatdog.model.board.BoardDto;
import com.cutecatdog.model.board.BoardModifyRequestDto;
import com.cutecatdog.model.board.BoardResponsDto;
import com.cutecatdog.model.board.BoardWhereDto;
import com.cutecatdog.model.comment.CommentAddShowRequestDto;
import com.cutecatdog.model.comment.CommentModifyRequestDto;
import com.cutecatdog.model.comment.CommentRequestDto;
import com.cutecatdog.model.comment.commentDto;
import com.cutecatdog.model.fcm.FCMParamDto;
import com.cutecatdog.model.like.LikeDeleteDto;
import com.cutecatdog.model.like.LikeRequestDto;
import com.cutecatdog.service.BoardService;
import com.cutecatdog.service.CommentService;
import com.cutecatdog.service.FCMService;
import com.cutecatdog.service.LikeService;
import com.cutecatdog.service.UserService;

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

@RestController
@RequestMapping("/board")
@Api(tags = "Board")
public class BoardController {

  @Autowired
  private BoardService boardService;

  @Autowired
  private CommentService commentService;

  @Autowired
  private LikeService likeService;

  @Autowired
  private UserService userService;

  @Autowired
  private FCMService fcmService;

  @ApiOperation(value = "게시글 전체 보기", notes = "", response = List.class)
  @GetMapping()
  public ResponseEntity<Message> boardList() throws Exception {
    HttpStatus status = HttpStatus.OK;
    Message message = new Message();

    List<BoardResponsDto> boards;
		boards = boardService.findBoard();
		HashMap<String,List<BoardResponsDto>> map = new HashMap<>();
		map.put("boards", boards);
		message.setData(map);
		message.setSuccess(true);
		return new ResponseEntity<Message>(message, status);
  }

  @ApiOperation(value = "우리 동네 게시글 보기", notes = "", response = List.class)
  @GetMapping("/type/my")
  public ResponseEntity<Message> boardDongList(@RequestParam("lat") double lat,@RequestParam("lng") double lng) throws Exception {
    HttpStatus status = HttpStatus.OK;
    Message message = new Message();
    BoardWhereDto dto = new BoardWhereDto();
    dto.setLat(lat);
    dto.setLng(lng);
    List<BoardResponsDto> boards;
		boards = boardService.findDongBoard(dto);
		HashMap<String,List<BoardResponsDto>> map = new HashMap<>();
		map.put("boards", boards);
		message.setData(map);
		message.setSuccess(true);
		return new ResponseEntity<Message>(message, status);
  }

  @ApiOperation(value = "게시글 타입 보기", notes = "", response = List.class)
  @GetMapping("/type/{typeId}")
  public ResponseEntity<Message> boardTypeList(@PathVariable("typeId") int typeId) throws Exception{
    HttpStatus status = HttpStatus.OK;
		Message message = new Message();

    List<BoardDto> boards;
		boards = boardService.findTypeBoard(typeId);
		HashMap<String,List<BoardDto>> map = new HashMap<>();
		map.put("boards", boards);
		message.setData(map);
		message.setSuccess(true);
		return new ResponseEntity<Message>(message, status);
  }

  @ApiOperation(value = "userId로 게시글 보기", notes = "", response = List.class)
  @GetMapping("/user/{userId}")
  public ResponseEntity<Message> boardUserList(@PathVariable("userId") int userId) throws Exception{
    HttpStatus status = HttpStatus.OK;
		Message message = new Message();

    List<BoardDto> boards;
		boards = boardService.findUserBoard(userId);
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
    HashMap<String, Boolean> map = new HashMap<>();
    
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
  public ResponseEntity<Message> boardModify(@RequestBody BoardModifyRequestDto boardModifyRequestDto)
      throws Exception {
    Message message = new Message();
    HttpStatus status = null;
    HashMap<String, Boolean> map = new HashMap<>();

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
    HashMap<String, Boolean> map = new HashMap<>();
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

  @ApiOperation(value = "게시글 상세 보기", notes = "", response = List.class)
  @GetMapping("{id}")
  public ResponseEntity<Message> commentList(@PathVariable("id") int id) throws Exception{
    HttpStatus status = HttpStatus.OK;
		Message message = new Message();

    BoardDetailDto board;
    HashMap<String,BoardDetailDto> map = new HashMap<>();
		board = boardService.findDetailBoard(id);
    
    map.put("board", board);
    message.setData(map);
    message.setSuccess(true);
  
		status = HttpStatus.OK;
		return new ResponseEntity<Message>(message, status);
  }

  @ApiOperation(value = "userId로 게시글 좋아요 여부 보기", notes = "", response = List.class)
  @GetMapping("/userid/{userId}")
  public ResponseEntity<Message> userBoardList(@PathVariable("userId") int userId) throws Exception{
    HttpStatus status = HttpStatus.OK;
		Message message = new Message();

    List<Integer> board;
    HashMap<String,List<Integer>> map = new HashMap<>();
		board = likeService.findUserBoard(userId);
    
    map.put("board", board);
    message.setData(map);
    message.setSuccess(true);
  
		status = HttpStatus.OK;
		return new ResponseEntity<Message>(message, status);
  }

  @ApiOperation(value = "댓글 상세 보기", notes = "", response = List.class)
  @GetMapping("{id}/comment")
  public ResponseEntity<Message> commentShowList(@PathVariable("id") int id) throws Exception{
    HttpStatus status = HttpStatus.OK;
		Message message = new Message();

    BoardDetailDto board;
    HashMap<String,List<commentDto>> map = new HashMap<>();
		board = boardService.findDetailBoard(id);
    List<commentDto> commentdto = board.getCommentDto();
    map.put("comments", commentdto);
    message.setData(map);
    message.setSuccess(true);
  
		status = HttpStatus.OK;
		return new ResponseEntity<Message>(message, status);
  }

  @ApiOperation(value = "대댓글 등록", notes = "대댓글을 등록한다.", response = List.class)
  @PostMapping("/comment/add")
  public ResponseEntity<Message> commentAdd(@RequestBody CommentAddShowRequestDto commentAddShowRequestDto) throws Exception {
    Message message = new Message();
		HttpStatus status = null;
    HashMap<String,Boolean> map = new HashMap<>();
		if (commentService.addComment(commentAddShowRequestDto)) {
      //대댓글 알림 전송
      FCMParamDto fcmParamDto = new FCMParamDto();
      int pId = commentAddShowRequestDto.getParent();
      int userId = commentService.findUser(pId);
      UserDto user = userService.findUser(userId);
      fcmParamDto.setContent(user.getNickname()+"님의 댓글에 대댓글이 달렸습니다");
      fcmParamDto.setTitle("ㅋㅋㄷ 대댓글 알림");
      fcmParamDto.setType(3);
      long UnixTime = System.currentTimeMillis();
      fcmParamDto.setDatetime(String.valueOf(UnixTime));
      fcmParamDto.setToken(user.getDeviceToken());
      fcmService.sendMessageTo(fcmParamDto);
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

  @ApiOperation(value = "댓글 등록", notes = "댓글을 등록한다.", response = List.class)
  @PostMapping("/comment")
  public ResponseEntity<Message> commentAdd(@RequestBody CommentRequestDto commentRequestDto) throws Exception {
    Message message = new Message();
		HttpStatus status = null;
    HashMap<String,Boolean> map = new HashMap<>();
		if (commentService.addRealComment(commentRequestDto)) {
			status = HttpStatus.OK;
      map.put("isSuccess", true);

      //댓글 알림 전송
      FCMParamDto fcmParamDto = new FCMParamDto();
      BoardDetailDto boardDetailDto = boardService.findDetailBoard(commentRequestDto.getBoardId());
      String title = boardDetailDto.getTitle();
      UserDto user = userService.findUser(boardDetailDto.getUserId());
      String[] bType = {"", "울동네", "궁금해", "공유해"};
      String board = bType[boardDetailDto.getTypeId()];
      if (title.length()>7) {
        title = " \""+title.substring(0, 7) + "...\"";
      }else if(title == null || title.length() == 0){
        title = "\""+boardDetailDto.getContent().substring(0, 7)+"...\"";
      }else{
        title = "\""+title+"\"";
      }
      fcmParamDto.setContent(user.getNickname()+"님의 "+board+" "+title+" 게시글에 댓글이 달렸습니다");
      fcmParamDto.setTitle("ㅋㅋㄷ 댓글 알림");
      fcmParamDto.setType(3);
      long UnixTime = System.currentTimeMillis();
      fcmParamDto.setDatetime(String.valueOf(UnixTime));
      fcmParamDto.setToken(user.getDeviceToken());
      fcmService.sendMessageTo(fcmParamDto);

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

  @ApiOperation(value = "댓글 수정", notes = "댓글을 수정한다.", response = List.class)
  @PutMapping("/comment")
  public ResponseEntity<Message> commentModify(@RequestBody CommentModifyRequestDto commentModifyRequestDto) throws Exception {
    Message message = new Message();
		HttpStatus status = null;
    HashMap<String,Boolean> map = new HashMap<>();
		if (commentService.modifyComment(commentModifyRequestDto)) {
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

  @ApiOperation(value = "댓글 삭제", notes = "댓글을 삭제한다.", response = List.class)
  @DeleteMapping("/comment/{id}")
  public ResponseEntity<Message> commentRemove(@PathVariable("id") int id) throws Exception {
    Message message = new Message();
		HttpStatus status = null;
    HashMap<String,Boolean> map = new HashMap<>();
		if (commentService.removeComment(id)) {
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

 @ApiOperation(value = "좋아요 체크 후 등록 및 삭제", notes = "좋아요를 추가한다.", response = List.class)
  @PostMapping("/likes")
  public ResponseEntity<Message> likeAdd(@RequestBody LikeRequestDto likeRequestDto) throws Exception {
    Message message = new Message();
		HttpStatus status = null;
    HashMap<String,Boolean> map = new HashMap<>();
    if(likeService.findLike(likeRequestDto) == true){
      if (likeService.addLike(likeRequestDto)) {
        status = HttpStatus.OK;
        map.put("isSuccess", true);
        message.setSuccess(true);
        message.setMessage("등록");
        message.setData(map);
        return new ResponseEntity<Message>(message, status);
      }else{
        map.put("isSuccess", false);
        message.setSuccess(false);
        message.setData(map);
        message.setMessage("오류");
        status = HttpStatus.OK;
        return new ResponseEntity<Message>(message, status);
      }
    }else{
      LikeDeleteDto dto = new LikeDeleteDto();
      dto.setBoardId(likeRequestDto.getBoardId());
      dto.setUserId(likeRequestDto.getUserId());
      if (likeService.removeLike(dto)) {
        status = HttpStatus.OK;
        map.put("isSuccess", true);
        message.setSuccess(true);
        message.setMessage("삭제");

        message.setData(map);
        return new ResponseEntity<Message>(message, status);
      }else{
        map.put("isSuccess", false);
        message.setSuccess(false);
        message.setData(map);
        message.setMessage("오류");

        status = HttpStatus.OK;
        return new ResponseEntity<Message>(message, status);
      }
    }

    
  }

  @ApiOperation(value = "boardDetail like 확인", notes = "boardDetail like 확인")
  @GetMapping(value = "/isLike")
  public ResponseEntity<Message> boardDetailIsLike(@RequestParam("userId") int userId, @RequestParam("boardId") int boardId) throws Exception {

    Message message = new Message();
    HttpStatus status = null;
    LikeRequestDto likeRequestDto = new LikeRequestDto();
    likeRequestDto.setUserId(userId);
    likeRequestDto.setBoardId(boardId);
    HashMap<String,Boolean> map = new HashMap<>();

    if (!likeService.findBoardLikeByUID(likeRequestDto)) {
      status = HttpStatus.OK;
      map.put("isSuccess", false);
      message.setSuccess(false);
      message.setMessage("이미 like 한 게시물 이거나 게시물이 존재하지 않습니다.");
      message.setData(map);
      return new ResponseEntity<Message>(message, status);
    }
    status = HttpStatus.OK;

    map.put("isSuccess", true);
    message.setSuccess(true);
    message.setMessage("좋아요 가능");
    message.setData(map);

    return new ResponseEntity<Message>(message, status);
  }

}
