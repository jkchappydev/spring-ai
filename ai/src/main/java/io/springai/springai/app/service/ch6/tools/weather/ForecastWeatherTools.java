package io.springai.springai.app.service.ch6.tools.weather;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
//@RequiredArgsConstructor
public class ForecastWeatherTools {

    final ForecastWeatherService forecastWeatherService; // 실제 예보/어제 날씨 조회 로직을 처리하는 서비스 객체

    public ForecastWeatherTools() { // 기본 생성자
        forecastWeatherService = new ForecastWeatherService(); // ForecastWeatherService 를 직접 생성해서 초기화
    }

    @Tool(description = """
            사용자가 요구하는 지역의 시간 별 일기 예보를 가지고 옵니다.
            지역 이름을 기반으로 latitude, longitude 정보를 조회 해서 날씨 정보를 가지고 옵니다,
            """) // 특정 지역의 시간대별 날씨 예보를 조회하는 Tool
    String getForecastWeather(
            @ToolParam(description = "latitude", required = true) double latitude, // 예보를 조회할 지역의 위도
            @ToolParam(description = "longitude", required = true) double longitude // 예보를 조회할 지역의 경도
    ) {
        return forecastWeatherService.getForecastWeather(latitude, longitude); // 서비스에 위도/경도를 넘겨 시간대별 날씨 예보 반환
    }

//    @Tool(description = "어제 날씨 정보를 가지고 옵니다. ") // 어제 날씨 정보를 조회하는 Tool
//    String getYesterdayWeather() throws IOException { // 파일 또는 외부 자원 처리 중 IOException 이 발생할 수 있음
//        return forecastWeatherService.getYesterdayWeather(); // 서비스에서 어제 날씨 정보를 조회해 반환
//    }

     @Tool(description = "어제 날씨 정보를 가지고 옵니다. ") // 어제 날씨 정보를 조회하는 Tool
     String getYesterdayWeather() throws RuntimeException { // 파일 또는 외부 자원 처리 중 RuntimeExecption 이 발생할 수 있음
         return forecastWeatherService.getYesterdayWeather(); // 서비스에서 어제 날씨 정보를 조회해 반환
     }

}