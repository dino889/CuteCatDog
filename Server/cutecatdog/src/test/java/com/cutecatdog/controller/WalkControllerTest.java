package com.cutecatdog.controller;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.cutecatdog.model.walk.WalkInsertRequestDto;
import com.cutecatdog.model.walk.WalkRequestDto;
import com.cutecatdog.service.WalkServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;

@SpringBootTest
@AutoConfigureMockMvc // 이 어노테이션을 통해서 MockMvc를 Builder 없이 주입 받을 수 있음
public class WalkControllerTest {
  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private MockMvc mockMvc;
  @Autowired
  private WalkServiceImpl walkService;

  @Test
  @DisplayName("Walk 데이터 가져오기 테스트") // 테스트 케이스 이름
  public void getWalkTest() throws Exception{
    
    //given : Mock 객체가 특정 상황에서 해야 하는 행위를 정의 하는 메소드
    WalkRequestDto dto = new WalkRequestDto();
    dto.setLat(37.58312625);
    dto.setLng(126.8050072);
    dto.setRange(5.0);
    assertEquals(37.58312625, walkService.findWalk(dto).get(0).getLat()); //현재는 있는 board 중 5가 최상단이라서 
    
    
    //andExpect : 내가 예상하는 값이 나왔는지 볼 수 있는 메소드
    mockMvc.perform(get("/walk?lat=" + dto.getLat()+"&lng="+dto.getLng()+"&range="+dto.getRange())).andExpect(status().isOk()).andExpect(jsonPath("$.data.walks[0].place").exists()).andDo(print()); 
  }

  @Test
  @DisplayName("Walk 데이터 등록 테스트") // 테스트 케이스 이름
  public void postWalkTest() throws Exception{
    WalkInsertRequestDto dto = new WalkInsertRequestDto();
    dto.setLat(0.0);
    dto.setLng(0.0);
    dto.setPlace("test");
    String content = objectMapper.writeValueAsString(dto);  
    mockMvc.perform(post("/walk").contentType(MediaType.APPLICATION_JSON).content(content)).andExpect(status().isOk()).andDo(print()); 
  }

}
