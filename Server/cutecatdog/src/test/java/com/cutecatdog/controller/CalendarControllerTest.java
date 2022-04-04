package com.cutecatdog.controller;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.cutecatdog.model.Calendar.ScheduleDto;
import com.cutecatdog.service.ScheduleServiceImpl;
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
public class CalendarControllerTest {
  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private MockMvc mockMvc;
  @Autowired
  private ScheduleServiceImpl scheduleService;

  @Test
  @DisplayName("Calendar 데이터 가져오기 테스트") // 테스트 케이스 이름
  public void getCalendarTest() throws Exception{
    //given : Mock 객체가 특정 상황에서 해야 하는 행위를 정의 하는 메소드
    int userId = 1;
    String dateTime = "2";
    int petId = 1;
    //assertEquals(1, scheduleService.findSchedule(userId).get(0).getId()); //현재는 있는 board 중 5가 최상단이라서 
    //andExpect : 내가 예상하는 값이 나왔는지 볼 수 있는 메소드
    mockMvc.perform(get("/calendar/" + userId)).andExpect(status().isOk()).andExpect(jsonPath("$.data.schedules").exists()).andDo(print()); //최근 일주일 사용자 조회
    mockMvc.perform(get("/calendar/" + userId+"/"+dateTime)).andExpect(status().isOk()).andExpect(jsonPath("$.data.schedules").exists()).andDo(print()); //사용자 등록 일정별 조회
    mockMvc.perform(get("/calendar/" + "inoculation"+"/"+petId)).andExpect(status().isOk()).andExpect(jsonPath("$.data.schedules[0].id").exists()).andDo(print()); //반려동물 접종 일정 조회
    mockMvc.perform(get("/calendar/" + "pet"+"/"+petId)).andExpect(status().isOk()).andExpect(jsonPath("$.data.schedules[0].id").exists()).andDo(print()); //반려동물 일정 조회
    mockMvc.perform(get("/calendar/" + "user"+"/"+userId)).andExpect(status().isOk()).andExpect(jsonPath("$.data.schedules[0].id").exists()).andDo(print()); //사용자 등록 일정 조회
    mockMvc.perform(get("/calendar/" + "walk"+"/"+petId)).andExpect(status().isOk()).andExpect(jsonPath("$.data.schedules[0].id").exists()).andDo(print()); //반려 동물 산책 일정 조회

  }

  @Test
  @DisplayName("Calendar 데이터 등록 테스트") // 테스트 케이스 이름
  public void postCalendarTest() throws Exception{
    
    ScheduleDto dto = new ScheduleDto();
    dto.setDatetime("datetime");
    dto.setMemo("memo");
    dto.setPetId(1);
    dto.setPlace("place");
    dto.setTitle("title");
    dto.setType(1);
    dto.setUserId(1);
    String content = objectMapper.writeValueAsString(dto);  
    mockMvc.perform(post("/calendar").contentType(MediaType.APPLICATION_JSON).content(content)).andExpect(status().isOk()).andDo(print()); 
  }

  @DisplayName("Calendar 등록 및 정보 수정 후 테스트 케이스 삭제")
  @Test
  public void fixAndDeleteCalendarInfo() throws Exception {
    ScheduleDto dto = new ScheduleDto();
    dto.setDatetime("datetime");
    dto.setMemo("memo");
    dto.setPetId(1);
    dto.setPlace("place");
    dto.setTitle("title");
    dto.setType(1);
    dto.setUserId(1);
    String content = objectMapper.writeValueAsString(dto);  
    mockMvc.perform(post("/calendar").contentType(MediaType.APPLICATION_JSON).content(content)).andExpect(status().isOk()).andDo(print()); 
    //일단 하나 등록후
    List<ScheduleDto> list =  scheduleService.findSchedule(1);
    list.get(list.size()-1).getId();

    ScheduleDto req = new ScheduleDto();
    req.setId(list.get(list.size() - 1).getId());
    req.setDatetime("수정");
    req.setMemo("수정");
    req.setPetId(1);
    req.setPlace("수정");
    req.setTitle("테케 수정");
    req.setType(1);
    req.setUserId(1);
    mockMvc.perform(put("/calendar")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(req)))
            .andExpect(status().isOk())
            .andDo(print());

    mockMvc.perform(delete("/calendar/"+req.getId()))
            .andExpect(status().isOk())
            .andDo(print());
    
  }

}
