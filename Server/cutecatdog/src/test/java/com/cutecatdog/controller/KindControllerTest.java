package com.cutecatdog.controller;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.cutecatdog.model.kind.KindAddRequestDto;
import com.cutecatdog.model.kind.KindDto;
import com.cutecatdog.model.kind.KindmodifyRequestDto;
import com.cutecatdog.service.KindServiceImpl;
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
public class KindControllerTest {
  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private MockMvc mockMvc;
  @Autowired
  private KindServiceImpl kindService;

  @Test
  @DisplayName("Kind 데이터 가져오기 테스트") // 테스트 케이스 이름
  public void getKindTest() throws Exception{
    
    //given : Mock 객체가 특정 상황에서 해야 하는 행위를 정의 하는 메소드
    int kindId = 1;
       
    
    //andExpect : 내가 예상하는 값이 나왔는지 볼 수 있는 메소드
    mockMvc.perform(get("/kinds")).andExpect(status().isOk()).andExpect(jsonPath("$.data.kinds[0].id").exists()).andDo(print()); 

    mockMvc.perform(get("/kinds/"+kindId)).andExpect(status().isOk()).andExpect(jsonPath("$.data.kinds[0].name").exists()).andDo(print()); 

  }

  @Test
  @DisplayName("Kind 데이터 등록 테스트") // 테스트 케이스 이름
  public void postKindTest() throws Exception{
    
    KindAddRequestDto dto = new KindAddRequestDto();
    dto.setName("등록 테스트용");
    dto.setTypeId(1);

    String content = objectMapper.writeValueAsString(dto);  
    mockMvc.perform(post("/kinds").contentType(MediaType.APPLICATION_JSON).content(content)).andExpect(status().isOk()).andDo(print()); 
  }


  @DisplayName("kind 내용 삭제")
  @Test
  public void deleteKindTest() throws Exception {
    KindAddRequestDto dto = new KindAddRequestDto();
    dto.setName("등록 테스트용");
    dto.setTypeId(1);

    String content = objectMapper.writeValueAsString(dto);  
    mockMvc.perform(post("/kinds").contentType(MediaType.APPLICATION_JSON).content(content)).andExpect(status().isOk()).andDo(print()); 

    mockMvc.perform(delete("/kinds/"+185))
        .andExpect(status().isOk())
        .andDo(print());
  }


  @DisplayName("Kind 정보 수정")
  @Test
  public void fixKindInfo() throws Exception {
    KindmodifyRequestDto dto = new KindmodifyRequestDto();
    KindAddRequestDto testdto = new KindAddRequestDto();
    testdto.setName("등록 테스트용");
    testdto.setTypeId(1);

    String content = objectMapper.writeValueAsString(testdto);  
    mockMvc.perform(post("/kinds").contentType(MediaType.APPLICATION_JSON).content(content)).andExpect(status().isOk()).andDo(print()); 

    dto.setId(185); //등록이 실행 된 후 무조건 수행되어야 한다. 그리고 id 값은 상승 시켜 주어야 해당하는 값을 수정 할 수 있다.
    dto.setName("수정할 이름");
    dto.setTypeId(1);

    mockMvc.perform(put("/kinds")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().isOk())
            .andDo(print());
  }

}
