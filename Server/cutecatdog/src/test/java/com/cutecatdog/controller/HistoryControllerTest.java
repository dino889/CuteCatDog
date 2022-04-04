package com.cutecatdog.controller;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.cutecatdog.model.history.HistoryRequestDto;
import com.cutecatdog.service.HistoryServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;


import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.mockito.Mockito.verify;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;

@SpringBootTest
@AutoConfigureMockMvc // 이 어노테이션을 통해서 MockMvc를 Builder 없이 주입 받을 수 있음
public class HistoryControllerTest {
  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private MockMvc mockMvc;
  @Autowired
  private HistoryServiceImpl historyService;

  @Test
  @DisplayName("History 데이터 가져오기 테스트") // 테스트 케이스 이름
  public void getHistoryTest() throws Exception{
    
    //given : Mock 객체가 특정 상황에서 해야 하는 행위를 정의 하는 메소드
    int userId = 1;
    
    assertEquals(userId, historyService.findHistory(userId).get(0).getUserId()); //현재는 있는 board 중 5가 최상단이라서 
    
    
    //andExpect : 내가 예상하는 값이 나왔는지 볼 수 있는 메소드
    mockMvc.perform(get("/history?userId=" + userId)).andExpect(status().isOk()).andExpect(jsonPath("$.data.historys[0].userId").exists()).andDo(print()); 
    userId = 13;
    mockMvc.perform(get("/history/" + userId + "?enddate="+"1648703792905"+"&startdate=1648703792903")).andExpect(status().isOk()).andExpect(jsonPath("$.data.history[0].id").exists()).andDo(print()); 
    int id = 2;
    mockMvc.perform(get("/history/detail/?id=" + id)).andExpect(status().isOk()).andExpect(jsonPath("$.data.history.id").exists()).andDo(print()); 

  }

  @Test
  @DisplayName("History 데이터 등록 테스트") // 테스트 케이스 이름
  public void postHistoryTest() throws Exception{
    
    HistoryRequestDto dto = new HistoryRequestDto();
    dto.setDatetime("datetime");
    dto.setEmotion("emotion");
    dto.setPhotoPath("photoPath");
    dto.setUserId(1);
    String content = objectMapper.writeValueAsString(dto);  
    mockMvc.perform(post("/history").contentType(MediaType.APPLICATION_JSON).content(content)).andExpect(status().isOk()).andDo(print()); 
  }


  @DisplayName("History 내용 삭제")
  @Test
  public void deleteHistoryTest() throws Exception {

    HistoryRequestDto dto = new HistoryRequestDto(); //새로 등록 후 
    dto.setDatetime("datetime");
    dto.setEmotion("emotion");
    dto.setPhotoPath("photoPath");
    dto.setUserId(1);
    String content = objectMapper.writeValueAsString(dto);  
    mockMvc.perform(post("/history").contentType(MediaType.APPLICATION_JSON).content(content)).andExpect(status().isOk()).andDo(print()); 

    int deleteId = 18; //삭제 완료후 갱신 필요
    mockMvc.perform(delete("/history/"+deleteId)) //삭제
        .andExpect(status().isOk())
        .andDo(print());
  }

}
