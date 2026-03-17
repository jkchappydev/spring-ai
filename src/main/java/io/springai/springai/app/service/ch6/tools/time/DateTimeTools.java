package io.springai.springai.app.service.ch6.tools.time;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
@Slf4j
public class DateTimeTools {

    // @Tool 에 name 을 지정하지 않으면, 매서드 이름이 name 으로 됨
    @Tool(description = "사용자가 운영하는 시스템의 timezone 을 기반으로 현재 날짜와 시간 정보를 알려줍니다. ") // 현재 날짜와 시간을 반환하는 Tool
    String getCurrentDateTime() { // 현재 날짜/시간 조회 메서드
        log.info("현재 시간: {}", LocalDateTime.now().atZone(LocaleContextHolder.getTimeZone().toZoneId()).toString()); // 현재 시스템 timezone 기준 시간을 로그로 출력
        return LocalDateTime.now().atZone(LocaleContextHolder.getTimeZone().toZoneId()).toString(); // 현재 시스템 timezone 기준 시간을 문자열로 반환
    }

    @Tool(description = "사용자가 요청한 시간으로 알람 설정") // 사용자가 요청한 시간으로 알람을 설정하는 Tool
    void setAlarm(@ToolParam(description = "ISO-8601 형식으로 제공된 시간", required = true) String time) { // ISO-8601 형식의 시간을 입력받는 메서드
        LocalDateTime alarmTime = LocalDateTime.parse(time, DateTimeFormatter.ISO_DATE_TIME); // 전달받은 문자열 시간을 LocalDateTime 으로 파싱
        log.info("다음 시간으로 알람이 설정 되었습니다. {}", alarmTime); // 설정된 알람 시간을 로그로 출력
    }

}