package com.cutecatdog.controller;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.cutecatdog.model.board.BoardAddRequestDto;
import com.cutecatdog.model.board.BoardModifyRequestDto;
import com.cutecatdog.model.board.BoardResponsDto;
import com.cutecatdog.model.comment.CommentRequestDto;
import com.cutecatdog.service.BoardServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import io.opencensus.common.ServerStatsFieldEnums.Size;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.mockito.Mockito.verify;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;

@SpringBootTest
@AutoConfigureMockMvc // 이 어노테이션을 통해서 MockMvc를 Builder 없이 주입 받을 수 있음
public class BoardControllerTest {
  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private MockMvc mockMvc;
  @Autowired
  private BoardServiceImpl boardService;

  @Test
  @DisplayName("Board 데이터 가져오기 테스트") // 테스트 케이스 이름
  public void getBoardTest() throws Exception{
    //given : Mock 객체가 특정 상황에서 해야 하는 행위를 정의 하는 메소드
    int boardId = 5;
    int userId = 1;
    assertEquals(1, boardService.findDetailBoard(boardId).getUserId()); //현재는 있는 board 중 5가 최상단이라서 
    //andExpect : 내가 예상하는 값이 나왔는지 볼 수 있는 메소드
    mockMvc.perform(get("/board/" + boardId)).andExpect(status().isOk()).andExpect(jsonPath("$.data.board.userId").exists()).andDo(print()); //게시글 상세보기 테스트
    mockMvc.perform(get("/board")).andExpect(status().isOk()).andExpect(jsonPath("$.data.boards[0].userId").exists()).andDo(print()); //게시글 모두 보기 테스트
    mockMvc.perform(get("/board/" + boardId + "/comment")).andExpect(status().isOk()).andExpect(jsonPath("$.data.comments[0].nickname").exists()).andDo(print()); //댓글 상세 보기 테스트

    mockMvc.perform(get("/board/isLike?boardId="+boardId+"&userId="+userId)).andExpect(status().isOk()).andExpect(jsonPath("$.data.isSuccess").exists()).andDo(print()); //board/isLike 테스트
    mockMvc.perform(get("/board/user/"+userId)).andExpect(status().isOk()).andExpect(jsonPath("$.data.boards[0].userId").exists()).andDo(print()); //board/userId 값으로 찾기 테스트
    mockMvc.perform(get("/board/userid/"+userId)).andExpect(status().isOk()).andExpect(jsonPath("$.data.board[0]").exists()).andDo(print()); // userId 값으로 좋아요 여부 찾아보기
  }

  @Test
  @DisplayName("Board 데이터 등록 테스트") // 테스트 케이스 이름
  public void postBoardTest() throws Exception{
    
    BoardAddRequestDto dto = new BoardAddRequestDto();
    dto.setAuthor("author");
    dto.setContent("content");
    dto.setPhotoPath("photoPath");
    dto.setTime("time");
    dto.setTitle("title");
    dto.setTypeId(1);
    dto.setUserId(1);  
    String content = objectMapper.writeValueAsString(dto);  
    mockMvc.perform(post("/board").contentType(MediaType.APPLICATION_JSON).content(content)).andExpect(status().isOk()).andDo(print()); 
  }


  @Test
  @DisplayName("Board 댓글 데이터 등록 테스트") // 테스트 케이스 이름
  public void postBoardCommentTest() throws Exception{
    
    CommentRequestDto dto = new CommentRequestDto();
    dto.setBoardId(5);
    dto.setComment("test_case_comment");
    dto.setUserId(1);
    String content = objectMapper.writeValueAsString(dto);  
    mockMvc.perform(post("/board/comment").contentType(MediaType.APPLICATION_JSON).content(content)).andExpect(status().isOk()).andDo(print()); 
  }

  @DisplayName("board 내용 삭제")
  @Test
  public void deleteBoardTest() throws Exception {

    BoardAddRequestDto dto = new BoardAddRequestDto();
    dto.setAuthor("author");
    dto.setContent("content");
    dto.setPhotoPath("photoPath");
    dto.setTime("time");
    dto.setTitle("title");
    dto.setTypeId(1);
    dto.setUserId(1);  

    boardService.addBoard(dto);
    List<BoardResponsDto> list = boardService.findBoard();
    
    mockMvc.perform(delete("/board/"+list.get(list.size()-1).getId()))
        .andExpect(status().isOk())
        .andDo(print());
  }


  @DisplayName("Board 정보 수정")
  @Test
  public void fixBoardInfo() throws Exception {
    BoardAddRequestDto dto = new BoardAddRequestDto();
    dto.setAuthor("author");
    dto.setContent("content");
    dto.setPhotoPath("photoPath");
    dto.setTime("time");
    dto.setTitle("title");
    dto.setTypeId(1);
    dto.setUserId(1);  
    boardService.addBoard(dto);
    List<BoardResponsDto> list = boardService.findBoard();

    BoardModifyRequestDto req = new BoardModifyRequestDto();
    req.setId(list.get(list.size() - 1).getId());
    req.setContent("테스트 케이스 수정");
    req.setPhotoPath("테스트 케이스 포토 패스 수정");
    req.setTitle("테스트 케이스 타이틀 수정");
    req.setTypeId(1);
    mockMvc.perform(put("/board")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(req)))
            .andExpect(status().isOk())
            .andDo(print());
  }

}
