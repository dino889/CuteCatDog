package com.cutecatdog.controller;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.cutecatdog.model.diary.DiaryDto;
import com.cutecatdog.model.diary.HashtagDto;
import com.cutecatdog.model.diary.PhotoDto;
import com.cutecatdog.service.DiaryServiceImpl;
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

import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.mockito.Mockito.verify;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;

@SpringBootTest
@AutoConfigureMockMvc // 이 어노테이션을 통해서 MockMvc를 Builder 없이 주입 받을 수 있음
public class DiaryControllerTest {
  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private MockMvc mockMvc;
  @Autowired
  private DiaryServiceImpl diaryService;

  @Test
  @DisplayName("Diary 데이터 가져오기 테스트") // 테스트 케이스 이름
  public void getDiaryTest() throws Exception{
    //given : Mock 객체가 특정 상황에서 해야 하는 행위를 정의 하는 메소드
    int userId = 1;
    int id = 3;
    //andExpect : 내가 예상하는 값이 나왔는지 볼 수 있는 메소드
    mockMvc.perform(get("/diary?"+"user_id="+userId)).andExpect(status().isOk()).andExpect(jsonPath("$.data.diarys[0].id").exists()).andDo(print()); //최근 일주일 사용자 조회
    mockMvc.perform(get("/diary/"+id)).andExpect(status().isOk()).andExpect(jsonPath("$.data.diary.id").exists()).andDo(print()); //일기 상세 조회
    mockMvc.perform(get("/diary/asc?user_id="+userId)).andExpect(status().isOk()).andExpect(jsonPath("$.data.diarys[0].id").exists()).andDo(print()); //전체 일기 조회 오래된 순으로

  }

  @Test
  @DisplayName("Diary 데이터 등록 테스트") // 테스트 케이스 이름
  public void postDiaryTest() throws Exception{
    List<PhotoDto> phlist = new ArrayList<PhotoDto>();
    PhotoDto photoDto = new PhotoDto();
    photoDto.setId(0);
    photoDto.setPhoto("photo");
    phlist.add(photoDto);
    List<HashtagDto> list = new ArrayList<HashtagDto>();
    HashtagDto hashtag = new HashtagDto();
    hashtag.setHashtag("hashtag");
    hashtag.setId(0);
    list.add(hashtag);
    DiaryDto  dto = new DiaryDto();
    dto.setDatetime("1");
    dto.setUserId(1);
    dto.setContent("content");
    dto.setHashtag(list);
    dto.setPhoto(phlist);
    dto.setTitle("title");

    String content = objectMapper.writeValueAsString(dto);  
    mockMvc.perform(post("/diary").contentType(MediaType.APPLICATION_JSON).content(content)).andExpect(status().isOk()).andDo(print()); 
  }

  @DisplayName("Diary 등록 및 정보 수정 후 테스트 케이스 삭제")
  @Test
  public void fixAndDeleteDiaryInfo() throws Exception {
    List<PhotoDto> phlist = new ArrayList<PhotoDto>();
    PhotoDto photoDto = new PhotoDto();
    photoDto.setId(0);
    photoDto.setPhoto("photo");
    phlist.add(photoDto);
    List<HashtagDto> hslist = new ArrayList<HashtagDto>();
    HashtagDto hashtag = new HashtagDto();
    hashtag.setHashtag("hashtag");
    hashtag.setId(0);
    hslist.add(hashtag);
    DiaryDto dto = new DiaryDto();
    dto.setDatetime("1");
    dto.setUserId(1);
    dto.setContent("content");
    dto.setHashtag(hslist);
    dto.setPhoto(phlist);
    dto.setTitle("title");
    String content = objectMapper.writeValueAsString(dto);  
    mockMvc.perform(post("/diary").contentType(MediaType.APPLICATION_JSON).content(content)).andExpect(status().isOk()).andDo(print()); 
    //일단 하나 등록후
    List<DiaryDto> list =  diaryService.findDiaryAsc(1);

    dto.setId(list.get(0).getId());
    dto.setDatetime("수정");
    dto.setTitle("테케 수정");
    dto.setUserId(1);
    mockMvc.perform(put("/diary")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().isOk())
            .andDo(print());

    mockMvc.perform(delete("/diary/"+dto.getId()))
            .andExpect(status().isOk())
            .andDo(print());
    
  }

}
