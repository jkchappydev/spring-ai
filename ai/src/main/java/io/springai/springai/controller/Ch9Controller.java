package io.springai.springai.controller;

import io.springai.springai.app.service.ch9.Ch9_AccessSystemService;
import io.springai.springai.app.service.ch9.Ch9_WeatherService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/ch9")
@Slf4j
@RequiredArgsConstructor
// 9. MCP(Model Context Protocol) Controller
public class Ch9Controller {

    private final Ch9_WeatherService ch9WeatherService;
    private final Ch9_AccessSystemService ch9AccessSystemService;

    // 1. MVC Chat
    @RequestMapping("/weather")
    public String chatTimeWeather(@RequestParam("prompt") String userPrompt) {
        log.info(userPrompt);
        return ch9WeatherService.chat(userPrompt);
    }
    // 2. MVC Access
    @RequestMapping("/access")
    public String access(   @RequestParam(value="attach", required = false) MultipartFile attach) throws IOException {
        return ch9AccessSystemService.imageAnalysisText(attach.getContentType(), attach.getBytes());
    }

}
