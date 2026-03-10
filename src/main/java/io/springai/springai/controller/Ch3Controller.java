package io.springai.springai.controller;

import io.springai.springai.app.dto.ch2.Contents;
import io.springai.springai.app.dto.ch2.Question;
import io.springai.springai.app.service.ch3.Ch3_AdvisorService;
import io.springai.springai.app.service.ch3.Ch3_StructuredOutputConverterService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;


@RestController
@RequestMapping("/ch3")
@Slf4j
@RequiredArgsConstructor
// Chapter 3. Advisors Controller
public class Ch3Controller {

    // 1. Advisor
    // 2. Advisor: Stream
    final Ch3_AdvisorService ch3AdvisorService;
    // 3. Recursive Advisors
    final Ch3_StructuredOutputConverterService ch3_StructuredOutputConverterService;

    // 1. Advisor
    @RequestMapping("/completion")
    public String chatCompletion(@RequestParam("prompt") String userPrompt) {
        log.info(userPrompt);
        return ch3AdvisorService.chat(userPrompt);
    }

    // 2. Advisor: Stream
    @RequestMapping("/stream")
    public Flux<String> chatStream(@RequestParam("prompt") String userPrompt) {
        log.info(userPrompt);
        return ch3AdvisorService.chatStream(userPrompt);
    }

    // 3. Recursive Advisors
    @RequestMapping("/bean-output")
    public Contents beanOutput(Question question) {
        log.info(question.toString());
        return ch3_StructuredOutputConverterService.beanOutputConverter(question);
    }

}
