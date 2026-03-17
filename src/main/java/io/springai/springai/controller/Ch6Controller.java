package io.springai.springai.controller;

import io.springai.springai.app.service.ch6.Ch6_AccessSystemToolsService;
import io.springai.springai.app.service.ch6.Ch6_SearchCustomerToolsService;
import io.springai.springai.app.service.ch6.Ch6_ShoppingToolsService;
import io.springai.springai.app.service.ch6.Ch6_TimeWeatherToolsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/ch6")
@Slf4j
@RequiredArgsConstructor
// Chapter 6. Tool Calling Controller
public class Ch6Controller {

    // 1. Date Time
    final Ch6_TimeWeatherToolsService ch6ChatClientService;
    // 2. Customer Inquiry - JSON
    // 2. Customer Inquiry - String
    final Ch6_SearchCustomerToolsService ch6SearchCustomerToolsService;
    // 3. Recommendation
    final Ch6_ShoppingToolsService ch6ShoppingToolsService;
    // 4. Access System
    final Ch6_AccessSystemToolsService ch6OpenAiImageService;

    // 1. Date Time
    @RequestMapping("/data-time")
    public String chatTimeWeather(@RequestParam("prompt") String userPrompt) {
        log.info(userPrompt);
        return ch6ChatClientService.chat1(userPrompt);
    }

    // 2. Customer Inquiry - JSON
    @RequestMapping("/customer-inquiry-json")
    public String getCustomer(@RequestParam("prompt") String userPrompt) {
        log.info(userPrompt);
        return ch6SearchCustomerToolsService.getCustomer(userPrompt);
    }

    // 2. Customer Inquiry - String
    @RequestMapping("/customer-inquiry-string")
    public String getCustomerString(@RequestParam("prompt") String userPrompt) {
        log.info(userPrompt);
        return ch6SearchCustomerToolsService.getCustomerString(userPrompt);
    }

    // 3. Recommendation
    @RequestMapping("/recommendation")
    public String getOrderedByCustomer(@RequestParam("prompt") String userPrompt, @RequestParam("user_id") String userId) {
        log.info(userPrompt);
        return ch6ShoppingToolsService.getPropensity(userPrompt, userId);
    }

    // 4. Access System
    @RequestMapping(value = "/access-system")
    public String accessSystem(
            @RequestParam(value="attach", required = false) MultipartFile attach) throws IOException {
        return ch6OpenAiImageService.imageAnalysisText(attach.getContentType(), attach.getBytes());
    }
}
