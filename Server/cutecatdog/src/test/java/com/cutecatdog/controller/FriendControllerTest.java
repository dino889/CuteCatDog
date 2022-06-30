package com.cutecatdog.controller;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.cutecatdog.model.board.BoardAddRequestDto;
import com.cutecatdog.model.board.BoardModifyRequestDto;
import com.cutecatdog.model.board.BoardResponsDto;
import com.cutecatdog.model.friend.FriendDto;
import com.cutecatdog.service.BoardServiceImpl;
import com.cutecatdog.service.FriendServiceImpl;
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
public class FriendControllerTest {
  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private MockMvc mockMvc;
  @Autowired
  private FriendServiceImpl friendService;

  @Test
  @DisplayName("친구 데이터 가져오기 테스트") // 테스트 케이스 이름
  public void getFriendTest() throws Exception{
    
    //given : Mock 객체가 특정 상황에서 해야 하는 행위를 정의 하는 메소드      
    
    //andExpect : 내가 예상하는 값이 나왔는지 볼 수 있는 메소드
    mockMvc.perform(get("/friend/anothor/" + friendService.findFriend(1).get(0).getFriendEmail()))
    .andExpect(status().isOk())
    .andExpect(jsonPath("$.data.friends[0].friendEmail")
    .exists())
    .andDo(print()); 
  
  }

  @Test
  @DisplayName("친구 데이터 등록 테스트") // 테스트 케이스 이름
  public void postFriendTest() throws Exception{
    
    FriendDto dto = new FriendDto();
    dto.setFriendEmail("test");
    dto.setUserId(1);
    String content = objectMapper.writeValueAsString(dto);  
    mockMvc.perform(post("/friend").contentType(MediaType.APPLICATION_JSON).content(content)).andExpect(status().isOk()).andDo(print()); 
  }


  @DisplayName("친구 내용 삭제")
  @Test
  public void deleteFriendTest() throws Exception {

    FriendDto dto = new FriendDto();
    dto.setFriendEmail("test");
    dto.setUserId(1);

    friendService.addFriend(dto);
    
    mockMvc.perform(delete("/friend/?friendEmail="+dto.getFriendEmail()+"&userId="+dto.getUserId()))
        .andExpect(status().isOk())
        .andDo(print());
  }

}
