package com.cutecatdog.controller;
import com.cutecatdog.model.UserDto;
import com.cutecatdog.service.UserServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;


import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;

@SpringBootTest
@AutoConfigureMockMvc // 이 어노테이션을 통해서 MockMvc를 Builder 없이 주입 받을 수 있음
public class UserControllerTest {
  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private MockMvc mockMvc;
  @Autowired
  private UserServiceImpl userService;

  @Test
  @DisplayName("User 데이터 가져오기 테스트") // 테스트 케이스 이름
  public void getUserTest() throws Exception{
    //given : Mock 객체가 특정 상황에서 해야 하는 행위를 정의 하는 메소드
    int id = 1;
    String email = "admin@cutecatdog.com";
    //andExpect : 내가 예상하는 값이 나왔는지 볼 수 있는 메소드
    mockMvc.perform(get("/users/?id=" + id)).andExpect(status().isOk()).andExpect(jsonPath("$.data.user.email").exists()).andDo(print()); //회원 정보 조회
    mockMvc.perform(get("/users/exists?email="+email)).andExpect(status().isOk()).andExpect(jsonPath("$.data.type").exists()).andDo(print()); // 이메일 조회
    mockMvc.perform(get("/users/id")).andExpect(status().isOk()).andExpect(jsonPath("$.data.user[0].id").exists()).andDo(print()); // 모든 회원의 id,profile,nickname 조회
    mockMvc.perform(get("/users/send-code?email="+email)).andExpect(status().isOk()).andExpect(jsonPath("$.data.code").exists()).andDo(print()); // 인증 코드 요청 확인

  }

  @Test
  @DisplayName("User 데이터 등록 테스트") // 테스트 케이스 이름
  public void postUserTest() throws Exception{
    
    UserDto dto = new UserDto();
    dto.setDeviceToken("deviceToken");
    dto.setEmail("test@email");
    dto.setNickname("nickname");
    dto.setPassword("password");
    dto.setProfileImage("profileImage");
    dto.setSNSToken("SNSToken");
    dto.setSocialType("socialType");
    String content = objectMapper.writeValueAsString(dto);  
    mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON).content(content)).andExpect(status().isOk()).andDo(print()); 
  }


  @DisplayName("User 정보 등록 및 수정 삭제")
  @Test
  public void postAndPutAndDeleteUserInfo() throws Exception {
    UserDto dto = new UserDto();
    dto.setDeviceToken("deviceToken");
    dto.setEmail("test@email");
    dto.setNickname("nickname");
    dto.setPassword("password");
    dto.setProfileImage("profileImage");
    dto.setSNSToken("SNSToken");
    dto.setSocialType("socialType");
    String content = objectMapper.writeValueAsString(dto);  
    mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON).content(content)).andExpect(status().isOk()).andDo(print()); 

    List<UserDto> list = userService.findAllUser();
    
    dto.setId(list.get(list.size()-1).getId());
    dto.setEmail("테스트 이메일 수정");

    mockMvc.perform(put("/users")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().isOk())
            .andDo(print());

    mockMvc.perform(delete("/users/"+list.get(list.size()-1).getId()))
        .andExpect(status().isOk())
        .andDo(print());

  }
  @Test
  @DisplayName("User 데이터 Login 테스트") // 테스트 케이스 이름
  public void postUserLoginTest() throws Exception{
    
    UserDto dto = new UserDto();
    dto.setEmail("string");
    dto.setPassword("string");
    String content = objectMapper.writeValueAsString(dto);  
    mockMvc.perform(post("/users/login").contentType(MediaType.APPLICATION_JSON).content(content)).andExpect(status().isOk()).andDo(print()); 
  }

  @DisplayName("User 비밀 번호 변경")
  @Test
  public void fixUserInfo() throws Exception {
    UserDto dto = new UserDto();
    dto.setEmail("email");
    dto.setPassword("password");
    mockMvc.perform(put("/users/reset-password?email="+"string")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().isOk())
            .andDo(print());
            //false 결과가 나와야함
  }

}
