package com.cutecatdog.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.cutecatdog.model.Notification.NotificationDto;
import com.cutecatdog.model.diary.DiaryDto;
import com.cutecatdog.model.diary.HashtagDto;
import com.cutecatdog.model.diary.PhotoDto;
import com.cutecatdog.service.DiaryServiceImpl;
import com.cutecatdog.service.NotificationService;
import com.cutecatdog.service.NotificationServiceImpl;
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
public class NotificationControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    NotificationService notificationService;

    @Test
    @DisplayName("Notification 데이터 가져오기 테스트") // 테스트 케이스 이름
    public void getNotiTest() throws Exception {
        // given : Mock 객체가 특정 상황에서 해야 하는 행위를 정의 하는 메소드
        int userId = 21;
        // andExpect : 내가 예상하는 값이 나왔는지 볼 수 있는 메소드
        mockMvc.perform(get("/notification/" + userId)).andExpect(status().isOk())
                .andExpect(jsonPath("$.data.notifications[0].id").exists()).andDo(print()); // 사용자 전체 알림 조회
        mockMvc.perform(get("/notification/event/" + userId)).andExpect(status().isOk())
                .andExpect(jsonPath("$.data.notifications[0].id").exists()).andDo(print()); // 이벤트 알림 조회
        mockMvc.perform(get("/notification/notice/" + userId)).andExpect(status().isOk())
                .andExpect(jsonPath("$.data.notifications[0].id").exists()).andDo(print()); // 공지사항 알림 조회
        mockMvc.perform(get("/notification/schedule/" + userId)).andExpect(status().isOk())
                .andExpect(jsonPath("$.data.notifications[0].id").exists()).andDo(print()); // 개인 일정 알림 조회

    }

    @Test
    @DisplayName("Notification 데이터 등록 테스트") // 테스트 케이스 이름
    public void postNotificationTest() throws Exception {
        NotificationDto dto = new NotificationDto();
        dto.setDatetime("1");
        dto.setUserId(1);
        dto.setContent("content");
        dto.setTitle("title");
        dto.setType(2);

        String content = objectMapper.writeValueAsString(dto);
        mockMvc.perform(post("/notification").contentType(MediaType.APPLICATION_JSON).content(content))
                .andExpect(status().isOk()).andDo(print());
    }

    @DisplayName("Notification 등록 후 테스트 케이스 삭제")
    @Test
    public void DeleteNotificationInfo() throws Exception {
        NotificationDto dto = new NotificationDto();
        dto.setDatetime("1");
        dto.setUserId(100);
        dto.setContent("content");
        dto.setTitle("title");
        dto.setType(1);
        String content = objectMapper.writeValueAsString(dto);
        mockMvc.perform(post("/diary").contentType(MediaType.APPLICATION_JSON).content(content))
                .andExpect(status().isOk()).andDo(print());
        // 일단 하나 등록후
        List<NotificationDto> list = notificationService.findNotificationList(100);
        int notiId = list.get(0).getId();

        mockMvc.perform(delete("/notification/" + notiId))
                .andExpect(status().isOk())
                .andDo(print());

    }

}
