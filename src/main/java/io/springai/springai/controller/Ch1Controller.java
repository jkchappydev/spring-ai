package io.springai.springai.controller;

import io.springai.springai.app.service.ch1.Ch1_ChatClientMemoryService;
import io.springai.springai.app.service.ch1.Ch1_ChatClientService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/ch1")
@Slf4j
@RequiredArgsConstructor
// Chapter 1. Chat Completion Controller
public class Ch1Controller {

    // 1. Chat (Zero-Shot Prompting)
    // 2. Chat Stream
    // 3. Chat (Few-Shot Prompting)
    // 4. Chat (Chain-of-Thought Prompting)
    final Ch1_ChatClientService ch1ChatClientService;
    // 5. Chat Memory
    final Ch1_ChatClientMemoryService ch1ChatClientMemoryService;

    // 1. Chat (Zero-Shot Prompting)
    @RequestMapping("/completion")
    public String chatCompletion(@RequestParam("prompt") String userPrompt) {
        log.info(userPrompt);
        return ch1ChatClientService.chat(userPrompt);
    }

    // 2. Chat Stream
    @RequestMapping("/stream")
    public Flux<String> chatStream(@RequestParam("prompt") String userPrompt) {
        log.info(userPrompt);
        return ch1ChatClientService.chatStream(userPrompt);
    }

    // 3. Chat (Few-Shot Prompting)
    @RequestMapping("/chain")
    public Flux<String> chatChainOfThought(@RequestParam("prompt") String userPrompt) {
        log.info(userPrompt);
        return ch1ChatClientService.chatChainOfThought(userPrompt);
    }

    // 4. Chat (Chain-of-Thought Prompting)
    @RequestMapping("/few")
    public String chatFewShot(@RequestParam("prompt") String userPrompt) {
        log.info(userPrompt);
        return ch1ChatClientService.chatFewShot(userPrompt);
    }


    // 5. Chat Memory
    @RequestMapping("/memory")
    public Flux<String> chatMemory(@RequestParam("prompt") String userPrompt, HttpSession session) {
        log.info(userPrompt);
        return ch1ChatClientMemoryService.chatMemory(userPrompt, session.getId());
    }
}
