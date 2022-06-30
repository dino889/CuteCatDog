package com.cutecatdog.config;

import java.util.List;

import com.cutecatdog.model.UserDto;
import com.cutecatdog.model.Calendar.ScheduleDto;
import com.cutecatdog.model.fcm.FCMParamDto;
import com.cutecatdog.service.FCMService;
import com.cutecatdog.service.ScheduleService;
import com.cutecatdog.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class Scheduler {

    @Autowired
    ScheduleService scheduleService;

    @Autowired
    FCMService fcmService;

    @Autowired
    UserService userService;

    /**
     * 매일 정각에 해당 시간대의 일정 알림 전송
     */
    @Scheduled(cron = "0 0 0 * * *", zone = "Asia/Seoul")
    public void makeAlert0() throws Exception {
        alert();
    }

    @Scheduled(cron = "0 0 1 * * *", zone = "Asia/Seoul")
    public void makeAlert1() throws Exception {
        alert();
    }

    @Scheduled(cron = "0 0 2 * * *", zone = "Asia/Seoul")
    public void makeAlert2() throws Exception {
        alert();
    }

    @Scheduled(cron = "0 0 3 * * *", zone = "Asia/Seoul")
    public void makeAlert3() throws Exception {
        alert();
    }

    @Scheduled(cron = "0 0 4 * * *", zone = "Asia/Seoul")
    public void makeAlert4() throws Exception {
        alert();
    }

    @Scheduled(cron = "0 0 5 * * *", zone = "Asia/Seoul")
    public void makeAlert5() throws Exception {
        alert();
    }

    @Scheduled(cron = "0 0 6 * * *", zone = "Asia/Seoul")
    public void makeAlert6() throws Exception {
        alert();
    }

    @Scheduled(cron = "0 0 7 * * *", zone = "Asia/Seoul")
    public void makeAlert7() throws Exception {
        alert();
    }

    @Scheduled(cron = "0 0 8 * * *", zone = "Asia/Seoul")
    public void makeAlert8() throws Exception {
        alert();
    }

    @Scheduled(cron = "0 0 9 * * *", zone = "Asia/Seoul")
    public void makeAlert9() throws Exception {
        alert();
    }

    @Scheduled(cron = "0 0 10 * * *", zone = "Asia/Seoul")
    public void makeAlert10() throws Exception {
        alert();
    }

    @Scheduled(cron = "0 0 11 * * *", zone = "Asia/Seoul")
    public void makeAlert11() throws Exception {
        alert();
    }

    @Scheduled(cron = "0 0 12 * * *", zone = "Asia/Seoul")
    public void makeAlert12() throws Exception {
        alert();
    }

    @Scheduled(cron = "0 0 13 * * *", zone = "Asia/Seoul")
    public void makeAlert13() throws Exception {
        alert();
    }

    @Scheduled(cron = "0 0 14 * * *", zone = "Asia/Seoul")
    public void makeAlert14() throws Exception {
        alert();
    }

    @Scheduled(cron = "0 0 15 * * *", zone = "Asia/Seoul")
    public void makeAlert15() throws Exception {
        alert();
    }

    @Scheduled(cron = "0 0 16 * * *", zone = "Asia/Seoul")
    public void makeAlert16() throws Exception {
        alert();
    }

    @Scheduled(cron = "0 0 17 * * *", zone = "Asia/Seoul")
    public void makeAlert17() throws Exception {
        alert();
    }

    @Scheduled(cron = "0 0 18 * * *", zone = "Asia/Seoul")
    public void makeAlert18() throws Exception {
        alert();
    }

    @Scheduled(cron = "0 0 19 * * *", zone = "Asia/Seoul")
    public void makeAlert19() throws Exception {
        alert();
    }

    @Scheduled(cron = "0 0 20 * * *", zone = "Asia/Seoul")
    public void makeAlert20() throws Exception {
        alert();
    }

    @Scheduled(cron = "0 0 21 * * *", zone = "Asia/Seoul")
    public void makeAlert21() throws Exception {
        alert();
    }

    @Scheduled(cron = "0 0 22 * * *", zone = "Asia/Seoul")
    public void makeAlert22() throws Exception {
        alert();
    }

    @Scheduled(cron = "0 0 23 * * *", zone = "Asia/Seoul")
    public void makeAlert23() throws Exception {
        alert();
    }

    public void alert() throws Exception{
        long UnixTime = System.currentTimeMillis();
        List<ScheduleDto> list = scheduleService.findScheduleHour();

        for (ScheduleDto tmp : list) {
            if (tmp != null) {
                tmp.setIsNoti(1);
                scheduleService.modifySchedule(tmp);
                UserDto user = userService.findUser(tmp.getUserId());
                FCMParamDto fcmParamDto = new FCMParamDto();
                fcmParamDto.setToken(user.getDeviceToken());
                fcmParamDto.setTitle(tmp.getTitle());
                fcmParamDto.setContent(tmp.getMemo());
                fcmParamDto.setDatetime(String.valueOf(UnixTime));
                fcmParamDto.setType(3);
                fcmService.sendMessageTo(fcmParamDto);
            }
        }
    }

}