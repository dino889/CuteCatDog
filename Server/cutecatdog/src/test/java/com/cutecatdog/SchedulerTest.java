package com.cutecatdog;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class SchedulerTest {
    /** * Cron 표현식을 사용한 작업 예약 * 초(0-59) 분(0-59) 시간(0-23) 일(1-31) 월(1-12) 요일(0-7) */
    @Scheduled(cron = "0 0 12 * * *", zone = "Asia/Seoul") //정오에 로그 출력
    public void scheduleTaskUsingCronExpression() {
        long now = System.currentTimeMillis();
        log.info("schedule tasks using cron jobs - {}", now);
    }
}
