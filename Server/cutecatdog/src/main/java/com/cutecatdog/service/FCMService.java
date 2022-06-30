package com.cutecatdog.service;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import com.cutecatdog.model.UserDto;
import com.cutecatdog.model.Notification.NotificationDto;
import com.cutecatdog.model.fcm.FCMParamDto;
import com.cutecatdog.model.fcm.FcmMessage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.auth.oauth2.GoogleCredentials;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

@Service
@Slf4j
@RequiredArgsConstructor
public class FCMService {
    private final String API_URL = "https://fcm.googleapis.com/v1/projects/cutecatdog-32527/messages:send";

    private final ObjectMapper objectMapper;

    @Autowired
    UserService userService;

    @Autowired
    NotificationService notificationService;

    /**
     * FCM에 push 요청을 보낼 때 인증을 위해 Header에 포함시킬 AccessToken 생성
     * 
     * @return
     * @throws IOException
     */
    private String getAccessToken() throws IOException {
        String firebaseConfigPath = "cutecatdog-32527-firebase-adminsdk-smy5i-60c7fd4694.json";

        // GoogleApi를 사용하기 위해 oAuth2를 이용해 인증한 대상을 나타내는 객체
        GoogleCredentials googleCredentials = GoogleCredentials
                // 서버로부터 받은 service key 파일 활용
                .fromStream(new ClassPathResource(firebaseConfigPath).getInputStream())
                // 인증하는 서버에서 필요로 하는 권한 지정
                .createScoped(Arrays.asList("https://www.googleapis.com/auth/cloud-platform"));

        // accessToken 생성
        googleCredentials.refreshIfExpired();

        // GoogleCredential의 getAccessToken으로 토큰 받아온 뒤, getTokenValue로 최종적으로 받음
        // REST API로 FCM에 push 요청 보낼 때 Header에 설정하여 인증을 위해 사용
        return googleCredentials.getAccessToken().getTokenValue();
    }

    /**
     * FCM 알림 메시지 생성
     * 
     * @param targetToken
     * @param title
     * @param body
     * @param path
     * @return
     * @throws JsonProcessingException
     */
    private String makeMessage(FCMParamDto fcmParamDto) throws JsonProcessingException {
        FcmMessage fcmMessage = FcmMessage.builder()
                .message(FcmMessage.Message.builder()
                        .token(fcmParamDto.getToken())
                        .notification(FcmMessage.Notification.builder()
                                .title(fcmParamDto.getTitle())
                                .body(fcmParamDto.getContent())
                                .image(null)
                                .build())
                        // .data(FcmMessage.Data.builder()
                        // .path(path)
                        // .build()
                        // )
                        .build())
                .validate_only(false).build();

        log.info(objectMapper.writeValueAsString(fcmMessage));
        return objectMapper.writeValueAsString(fcmMessage);
    }

    /**
     * targetToken에 해당하는 device로 FCM 푸시 알림 전송
     * 
     * @param targetToken
     * @param title
     * @param body
     * @param path
     * @throws Exception
     */
    public void sendMessageTo(FCMParamDto fcmParamDto) throws Exception {
        String message = makeMessage(fcmParamDto);
        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = RequestBody.create(message, MediaType.get(("application/json; charset=utf-8")));
        Request request = new Request.Builder()
                .url(API_URL)
                .post(requestBody)
                .addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + getAccessToken())
                .addHeader(HttpHeaders.CONTENT_TYPE, "application/json; UTF-8")
                .build();

        Response response = client.newCall(request).execute();

        log.info(response.body().string());

        if (response.isSuccessful()) {
            NotificationDto noti = new NotificationDto();
            UserDto user = userService.findUserByToken(fcmParamDto.getToken());
            if (user != null) {
                noti.setUserId(user.getId());
                noti.setTitle(fcmParamDto.getTitle());
                noti.setContent(fcmParamDto.getContent());
                noti.setType(fcmParamDto.getType());
                noti.setDatetime(fcmParamDto.getDatetime());
                notificationService.addNotification(noti);
            }
        }
    }

    /**
     * client의 Token이 변경되면 user 테이블 값을 갱신한다.
     * 
     * @param token
     * @param userId
     * @throws Exception
     */
    public void modifyToken(String token, int userId) throws Exception {
        UserDto user = new UserDto();
        user.setId(userId);
        user.setDeviceToken(token);
        userService.modifyTokenByUserId(user);
    }

    /**
     * 등록된 모든 토큰을 이용해서 broadcasting
     * 
     * @param title
     * @param body
     * @return
     * @throws Exception
     */
    public int broadCastMessage(FCMParamDto fcmParamDto) throws Exception {
        // path는 application 초기 화면
        List<UserDto> users = userService.findAllUser();
        for (int i = 0; i < users.size(); i++) {
            UserDto user = users.get(i);
            if (user.getDeviceToken() != null) {
                // if(user != null && !user.getToken().isEmpty() && user.getToken() != null) {
                log.debug("broadcastmessage : {},{},{}", user.getDeviceToken(), fcmParamDto.getTitle(),
                        fcmParamDto.getContent());
                fcmParamDto.setToken(user.getDeviceToken());
                sendMessageTo(fcmParamDto);
            }
        }
        return users.size();
    }
}
