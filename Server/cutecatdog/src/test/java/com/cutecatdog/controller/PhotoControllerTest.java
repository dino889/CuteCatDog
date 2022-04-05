package com.cutecatdog.controller;

import com.cutecatdog.model.diary.PhotoDto;
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
public class PhotoControllerTest {
  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private MockMvc mockMvc;

  @Test
  @DisplayName("Photo 데이터 가져오기 테스트") // 테스트 케이스 이름
  public void getPhotoTest() throws Exception{
    //given : Mock 객체가 특정 상황에서 해야 하는 행위를 정의 하는 메소드
    int diaryId = 3;
    //andExpect : 내가 예상하는 값이 나왔는지 볼 수 있는 메소드
    mockMvc.perform(get("/photos/" + diaryId)).andExpect(status().isOk()).andExpect(jsonPath("$.data.schedules").exists()).andDo(print()); //일기 사진 조회

  }

  @Test
  @DisplayName("Photo 데이터 등록 테스트") // 테스트 케이스 이름
  public void postCalendarTest() throws Exception{
    
    PhotoDto dto = new PhotoDto();
    dto.setPhoto("PhotoTest");
    String content = objectMapper.writeValueAsString(dto);
    mockMvc.perform(post("/photos").contentType(MediaType.APPLICATION_JSON).content(content)).andExpect(status().isOk()).andDo(print()); 
  }

  @DisplayName("Photo 삭제 테스트")
  @Test
  public void fixAndDeleteCalendarInfo() throws Exception {
    int photoId = 17;
    mockMvc.perform(delete("/photos/"+photoId))
            .andExpect(status().isOk())
            .andDo(print());
    
  }

}
