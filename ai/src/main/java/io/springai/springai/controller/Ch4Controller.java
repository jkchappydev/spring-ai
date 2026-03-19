package io.springai.springai.controller;

import io.springai.springai.app.service.ch4.Ch4_OpenAiImageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.util.Objects;

@RestController
@RequestMapping("/ch4")
@Slf4j
@RequiredArgsConstructor
// Chapter 4. Multimodality API – Images & Vision Controller
public class Ch4Controller {

    final Ch4_OpenAiImageService ch4OpenAiImageService;

    // 1. Generate Image for URL
    @RequestMapping("/generate-image-url")
    public String generateImageUrl(@RequestParam("prompt") String question) {
        log.info(question);
        return ch4OpenAiImageService.generateImageUrl(question);
    }

    // 2. Generate Image
    @RequestMapping("/generate-image")
    public String generateImage(@RequestParam("prompt") String question) {
        log.info(question);
        return ch4OpenAiImageService.generateImageToText(question);
    }

    // 3. Image Analysis
    @RequestMapping(value = "/image-analysis")
    public Flux<String> imageAnalysis(
            @RequestParam("question") String question,
            @RequestParam(value="attach", required = false) MultipartFile attach) throws IOException {
        // 이미지가 업로드 되지 않았을 경우
        if (attach == null || !Objects.requireNonNull(attach.getContentType()).contains("image/")) {
            return Flux.just("이미지를 올려주세요.");
        }
        return ch4OpenAiImageService.imageAnalysis(question, attach.getContentType(), attach.getBytes());
    }
}
