package com.cutecatdog.controller;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.cutecatdog.model.board.BoardAddRequestDto;
import com.cutecatdog.model.board.BoardModifyRequestDto;
import com.cutecatdog.model.board.BoardResponsDto;
import com.cutecatdog.model.comment.CommentRequestDto;
import com.cutecatdog.service.BoardServiceImpl;
import com.cutecatdog.service.HashtagServiceImpl;
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
public class HashtagControllerTest {
  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private MockMvc mockMvc;
  @Autowired
  private HashtagServiceImpl hashtagService;

  @Test
  @DisplayName("hashtag 데이터 가져오기 테스트") // 테스트 케이스 이름
  public void getHashTagTest() throws Exception{
    //given : Mock 객체가 특정 상황에서 해야 하는 행위를 정의 하는 메소드
    int diaryId = 3;
    mockMvc.perform(get("/hashtags")).andExpect(status().isOk()).andExpect(jsonPath("$.data.hashtags[0].id").exists()).andDo(print()); //해쉬태그 조회
    mockMvc.perform(get("/hashtags/" + diaryId)).andExpect(status().isOk()).andExpect(jsonPath("$.data.hashtags[0].id").exists()).andDo(print()); //게시글 상세보기 테스트

  }

}
