package com.cutecatdog.controller;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.cutecatdog.model.kind.KindAddRequestDto;
import com.cutecatdog.model.kind.KindmodifyRequestDto;
import com.cutecatdog.model.pet.PetDto;
import com.cutecatdog.service.KindServiceImpl;
import com.cutecatdog.service.PetServiceImpl;
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
public class PetControllerTest {
  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private MockMvc mockMvc;
  @Autowired
  private PetServiceImpl petService;

  @Test
  @DisplayName("Pet 데이터 가져오기 테스트") // 테스트 케이스 이름
  public void getPetTest() throws Exception{
    
    //given : Mock 객체가 특정 상황에서 해야 하는 행위를 정의 하는 메소드
    int petId = 1;
    int userId = 1;     
    
    //andExpect : 내가 예상하는 값이 나왔는지 볼 수 있는 메소드
    mockMvc.perform(get("/pets")).andExpect(status().isOk()).andExpect(jsonPath("$.data.pets[0].id").exists()).andDo(print()); 
  
    mockMvc.perform(get("/pets/"+petId)).andExpect(status().isOk()).andExpect(jsonPath("$.data.pet.name").exists()).andDo(print()); 
    mockMvc.perform(get("/pets/my/"+userId)).andExpect(status().isOk()).andExpect(jsonPath("$.data.pets[0].name").exists()).andDo(print()); 

  }

  @Test
  @DisplayName("Pet 데이터 등록 테스트") // 테스트 케이스 이름
  public void postPetTest() throws Exception{
    
    PetDto dto = new PetDto();
    dto.setBirth("Test Case");
    dto.setGender(0);
    dto.setIsNeutered(0);
    dto.setKindId(1);
    dto.setName("Test Case");
    dto.setUserId(1);
    dto.setPhotoPath("Test Case");
    dto.setId(0);

    String content = objectMapper.writeValueAsString(dto);  
    mockMvc.perform(post("/pets").contentType(MediaType.APPLICATION_JSON).content(content)).andExpect(status().isOk()).andDo(print()); 
  }


  @DisplayName("pet 내용 삭제")
  @Test
  public void deletePetTest() throws Exception {
    PetDto dto = new PetDto();
    dto.setBirth("Test Case");
    dto.setGender(0);
    dto.setIsNeutered(0);
    dto.setKindId(1);
    dto.setName("Test Case");
    dto.setUserId(1);
    dto.setPhotoPath("Test Case");
    dto.setId(0);

    String content = objectMapper.writeValueAsString(dto);  
    mockMvc.perform(post("/pets").contentType(MediaType.APPLICATION_JSON).content(content)).andExpect(status().isOk()).andDo(print());

    List<PetDto> list = petService.findPet();
    

    mockMvc.perform(delete("/pets/"+list.get(list.size()-1).getId()))
        .andExpect(status().isOk())
        .andDo(print());
  }


  @DisplayName("Pet 정보 수정")
  @Test
  public void fixPetInfo() throws Exception {
    PetDto dto = new PetDto();
    dto.setBirth("Test Case");
    dto.setGender(0);
    dto.setIsNeutered(0);
    dto.setKindId(1);
    dto.setName("Test Case");
    dto.setUserId(1);
    dto.setPhotoPath("Test Case");
    dto.setId(0);

    String content = objectMapper.writeValueAsString(dto);  
    mockMvc.perform(post("/pets").contentType(MediaType.APPLICATION_JSON).content(content)).andExpect(status().isOk());

    List<PetDto> list = petService.findPet();

    dto.setBirth("수정된 벌스");
    dto.setGender(0);
    dto.setIsNeutered(0);
    dto.setKindId(1);
    dto.setName("수정 Test Case");
    dto.setUserId(1);
    dto.setPhotoPath("수정 Test Case");
    dto.setId(list.get(list.size() - 1).getId());
    //실제 수정
    mockMvc.perform(put("/pets")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().isOk())
            .andDo(print());
    //수정 후 삭제
    mockMvc.perform(delete("/pets/"+list.get(list.size()-1).getId()))
        .andExpect(status().isOk());
  }

}
