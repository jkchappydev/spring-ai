package io.springai.springai.app.service.ch6.tools.weather;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.time.LocalDateTime;

@Component
@Slf4j
public class CurrentWeatherTools {

    public record WeatherResponse(Current current) { // 현재 날씨 응답 전체를 담는 record
        public record Current(LocalDateTime time, int interval, double temperature_2m) {} // 현재 시간, 조회 간격, 현재 온도를 담는 record
    }

    @Tool(description = """
            오늘의 날씨 또는 현재 온도를 가지고 옵니다. 
            지역 이름을 기반으로 latitude, longitude 정보를 조회 해서 날씨 정보를 가지고 옵니다,
            """) // 현재 날씨 또는 현재 온도를 조회하는 Tool
    WeatherResponse getCurrentWeather(
            @ToolParam(description = "latitude", required = true) double latitude, // 위도 입력값
            @ToolParam(description = "longitude", required = true) double longitude // 경도 입력값
    ) {
        return RestClient.create() // RestClient 생성
                .get() // GET 방식으로 요청
                .uri("https://api.open-meteo.com/v1/forecast?latitude={latitude}&longitude={longitude}&current=temperature_2m", latitude, longitude) // 위도, 경도를 넣어 현재 온도 조회 API 호출
                .retrieve() // 응답 본문 조회 시작
                .body(WeatherResponse.class); // 응답 JSON 을 WeatherResponse 객체로 변환
    }

}